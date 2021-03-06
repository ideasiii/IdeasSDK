package sdk.ideas.tool.speech.voice;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.module.MediaCheckUtility;

public class VoiceRecognition extends BaseHandler
{
    private Locale defaultLocale;
    private boolean isListening = false;
    private boolean isWarning = false;
    private Intent recognizerIntent;
    private boolean returnInitValue;
    private SpeechRecognizer speech;
    private String sttID = "";
    
    class GoogleRecognitionListener implements RecognitionListener
    {
        GoogleRecognitionListener()
        {
        }
        
        public void onReadyForSpeech(Bundle params)
        {
        }
        
        public void onBeginningOfSpeech()
        {
        }
        
        public void onRmsChanged(float rmsDB)
        {
        
        }
        
        public void onBufferReceived(byte[] buffer)
        {
        }
        
        public void onEndOfSpeech()
        {
        
        }
        
        @Override
        public void onError(int errorCode)
        {
            String errorMessage = getErrorText(errorCode);
            HashMap<String, String> message = new HashMap<String, String>();
            message.put("message", errorMessage);
            
            // add STT ID
            message = setSttID(message);
            
            switch (errorMessage)
            {
                case "Network error":
                case "Network timeout":
                case "Error from server, please check network connection":
                    callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType
                            .MSG_RESPONSE_VOICE_RECOGNITION_HANDLER, ResponseCode
                            .METHOD_RETURN_TEXT_VOICE_RECOGNIZER, message);
                    break;
                case "Insufficient permissions":
                    callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION, CtrlType
                            .MSG_RESPONSE_VOICE_RECOGNITION_HANDLER, ResponseCode
                            .METHOD_RETURN_TEXT_VOICE_RECOGNIZER, message);
                    break;
                default:
                    callBackMessage(ResponseCode.ERR_SPEECH_ERRORMESSAGE, CtrlType
                            .MSG_RESPONSE_VOICE_RECOGNITION_HANDLER, ResponseCode
                            .METHOD_RETURN_TEXT_VOICE_RECOGNIZER, message);
                    break;
            }
        }
        
        @Override
        public void onResults(Bundle results)
        {
            
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            HashMap<String, String> message = new HashMap<String, String>();
            if (null != matches)
            {
                String text = matches.get(0);
                
                message.put("message", text);
                
                // add STT ID
                message = setSttID(message);
                
                callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER,
                        ResponseCode.METHOD_RETURN_TEXT_VOICE_RECOGNIZER, message);
            }
            else
            {
                message.put("message", "Error from server, please check network connection");
                callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType
                        .MSG_RESPONSE_VOICE_RECOGNITION_HANDLER, ResponseCode
                        .METHOD_RETURN_TEXT_VOICE_RECOGNIZER, message);
                
            }
        }
        
        public void onPartialResults(Bundle partialResults)
        {
        
        }
        
        public void onEvent(int eventType, Bundle params)
        {
        }
    }
    
    private HashMap<String, String> setSttID(HashMap<String, String> data)
    {
        if (!sttID.isEmpty())
        {
            data.put("sttID", sttID);
            sttID = "";
        }
        
        return data;
        
    }
    
    public VoiceRecognition(Context mContext)
    {
        super(mContext);
        this.speech = null;
        this.defaultLocale = Locale.TAIWAN;
        this.returnInitValue = false;
        this.isListening = false;
        this.isWarning = false;
    }
    
    public void setLocale(Locale locale)
    {
        this.defaultLocale = locale;
    }
    
    public Locale getLocale()
    {
        return this.defaultLocale;
    }
    
    public synchronized void stopListen()
    {
        Logs.showTrace("$$$this.isListening is: " + String.valueOf(this.isListening));
        if (this.speech == null)
        {
            Logs.showTrace("$$$this.speech is null ");
        }
        
        if (this.isListening && this.speech != null)
        {
            // this.speech.stopListening();
            this.speech.destroy();
            this.speech = null;
            this.isListening = false;
            this.isWarning = false;
        }
        else if (!this.isWarning)
        {
            HashMap<String, String> message = new HashMap<String, String>();
            message.put("message", "please ''startListen()'' first");
            callBackMessage(ResponseCode.ERR_NOT_INIT, CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER,
                    ResponseCode.METHOD_START_VOICE_RECOGNIZER, message);
            this.isWarning = true;
        }
    }
    
    public synchronized void startListen(String sttID)
    {
        this.sttID = sttID;
        startListen();
    }
    
    public synchronized void startListen()
    {
        
        if (!this.isListening)
        {
            this.isListening = true;
            HashMap<String, String> message = new HashMap<String, String>();
            if (MediaCheck())
            {
                startVoiceRecognitionActivity();
                if (this.returnInitValue)
                {
                    message.put("message", "start listening");
                    callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType
                            .MSG_RESPONSE_VOICE_RECOGNITION_HANDLER, ResponseCode
                            .METHOD_START_VOICE_RECOGNIZER, message);
                    this.speech = SpeechRecognizer.createSpeechRecognizer(this.mContext);
                    this.speech.setRecognitionListener(new GoogleRecognitionListener());
                    this.speech.startListening(this.recognizerIntent);
                }
            }
            else
            {
                message.put("message", "device UnSupported ");
                callBackMessage(ResponseCode.ERR_MICROPHONE_NOT_EXISTS, CtrlType
                        .MSG_RESPONSE_VOICE_RECOGNITION_HANDLER, ResponseCode
                        .METHOD_START_VOICE_RECOGNIZER, message);
            }
        }
    }
    
    private boolean MediaCheck()
    {
        if (MediaCheckUtility.getMicrophoneExists(this.mContext) && MediaCheckUtility
                .getSpeechRecognizeAvailable(this.mContext))
        {
            Logs.showTrace("MediaCheckUtility all GREEN");
            return true;
        }
        Logs.showTrace("MediaCheckUtility has ERROR");
        return false;
    }
    
    private void startVoiceRecognitionActivity()
    {
        recognizerIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent
                .LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, defaultLocale.toString());
        returnInitValue = true;
    }
    
    private static String getErrorText(int errorCode)
    {
        String msg;
        switch (errorCode)
        {
            case SpeechRecognizer.ERROR_AUDIO:
                msg = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                msg = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                msg = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                msg = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                msg = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                msg = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                msg = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                msg = "Error from server, please check network connection";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                msg = "No speech input";
                break;
            default:
                msg = "Didn't understand, please try again.";
                break;
        }
        
        Logs.showTrace("&&&&&" + msg);
        return msg;
    }
}