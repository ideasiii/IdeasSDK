package sdk.ideas.tool.speech.voice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.module.MediaCheckUtility;

public class VoiceRecognition extends BaseHandler
{
	private SpeechRecognizer speech = null;
	private Intent recognizerIntent;
	private Locale defaultLocale = Locale.getDefault();
	private boolean returnInitValue = false;

	public VoiceRecognition(Context mContext)
	{
		super(mContext);
	}

	public void stopListen()
	{
		if (null == speech)
		{
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("message", "please ''startListen()'' first");
			callBackMessage(ResponseCode.ERR_NOT_INIT, CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER,
					ResponseCode.METHOD_START_VOICE_RECOGNIZER, message);
		}
		else
		{
			speech.stopListening();
		}		
	}

	public void startListen()
	{
		HashMap<String, String> message = new HashMap<String, String>();
		if (MediaCheck() == true)
		{
			startVoiceRecognitionActivity();
			if (returnInitValue == true)
			{
				message.put("message", "start listening");
				callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER,
						ResponseCode.METHOD_START_VOICE_RECOGNIZER, message);
				speech = SpeechRecognizer.createSpeechRecognizer(mContext);
				speech.setRecognitionListener(new RecognitionListener()
				{
					@Override
					public void onReadyForSpeech(Bundle params)
					{
						// Logs.showTrace("onReadyForSpeech");
					}

					@Override
					public void onBeginningOfSpeech()
					{
						// Logs.showTrace("onBeginningOfSpeech");
					}

					@Override
					public void onRmsChanged(float rmsdB)
					{
						// Logs.showTrace("onRmsChanged: " + rmsdB);
					}

					@Override
					public void onBufferReceived(byte[] buffer)
					{
						// Logs.showTrace("onBufferReceived: " + buffer);
					}

					@Override
					public void onEndOfSpeech()
					{
						// Logs.showTrace("onEndOfSpeech");
					}

					@Override
					public void onError(int errorCode)
					{
						String errorMessage = getErrorText(errorCode);
						// Logs.showTrace("onError: " + errorMessage);
						HashMap<String, String> message = new HashMap<String, String>();
						message.put("message", errorMessage);
						callBackMessage(ResponseCode.ERR_SPEECH_ERRORMESSAGE, CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER,
								ResponseCode.METHOD_RETURN_TEXT_VOICE_RECOGNIZER, message);
					}

					@Override
					public void onResults(Bundle results)
					{
						// Logs.showTrace("onResults");
						ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
						String text = matches.get(0);
						HashMap<String, String> message = new HashMap<String, String>();
						message = new HashMap<String, String>();
						message.put("message", text);
						callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER,
								ResponseCode.METHOD_RETURN_TEXT_VOICE_RECOGNIZER, message);
					}

					@Override
					public void onPartialResults(Bundle partialResults)
					{
						// Logs.showTrace("onPartialResults");
						// String msg = "";
						// HashMap<String, String> message = new HashMap<String,
						// String>();
						// if (null != partialResults)
						// {
						// ArrayList<String> matches = partialResults
						// .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
						// if (null != matches)
						// {
						// for (int i = 0; i < matches.size(); ++i)
						// {
						// if (null != matches.get(i))
						// {
						// msg = matches.get(i);
						// }
						// }
						// Logs.showTrace("onPartialResults Success");
						// message = new HashMap<String, String>();
						// message.put("message", msg);
						// callBackMessage(ResponseCode.ERR_SUCCESS,
						// CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER,
						// ResponseCode.METHOD_RETURN_TEXT_VOICE_RECOGNIZER,
						// message);
						// }
						// }
					}

					@Override
					public void onEvent(int eventType, Bundle params)
					{
						// Logs.showTrace("onEvent");
					}

				});
				speech.startListening(recognizerIntent);
			}
		}
		else
		{
			message.put("message", "device UnSupported ");
			callBackMessage(ResponseCode.ERR_MICROPHONE_NOT_EXISTS, CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER,
					ResponseCode.METHOD_START_VOICE_RECOGNIZER, message);
			return;
		}
	}

	private boolean MediaCheck()
	{
		if (MediaCheckUtility.getMicrophoneExists(mContext) == true
				&& MediaCheckUtility.getSpeechRecognizeAvailable(mContext) == true)
		{
			Logs.showTrace("MediaCheckUtility all GREEN");
			return true;
		}
		Logs.showTrace("MediaCheckUtility has ERROR");
		return false;
	}

	public void startVoiceRecognitionActivity()
	{
		recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, defaultLocale);
		recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, defaultLocale);
		returnInitValue = true;
	}

	public static String getErrorText(int errorCode)
	{
		String msg = "";
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
		return msg;
	}
}
