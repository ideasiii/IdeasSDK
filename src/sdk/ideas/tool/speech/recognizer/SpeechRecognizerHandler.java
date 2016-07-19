package sdk.ideas.tool.speech.recognizer;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.module.MediaCheckUtility;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

public class SpeechRecognizerHandler extends BaseHandler
{
	private SpeechRecognizer mSpeechRecognizer = null;
	private RecognizerIntent mIntent = null;
	private boolean customFormula = false;
	private String promptInfomation = "Please Say Something!";
	private HashMap<String, String> message = null;
	private boolean isMediaCheckPassed = false;
	
	//private int 
	
	
	public SpeechRecognizerHandler(Context mContext, boolean isCustomFormula)
	{
		super(mContext);
		customFormula = isCustomFormula;
		init();

	}

	public SpeechRecognizerHandler(Context mContext)
	{
		this(mContext, false);
	}

	private void init()
	{
		message = new HashMap<String,String>();
		if(MediaCheck() == true)
		{
			isMediaCheckPassed = true;
		}
		else
		{
			isMediaCheckPassed = false;
			return;
		}
		if (customFormula == true)
		{

		}

	}

	public void startVoiceRecognition()
	{
		if(isMediaCheckPassed = false)
		{
			message.put("message", "device UnSupported ");
			callBackMessage(ResponseCode.ERR_DEVICE_NOT_SUPPORT_BLUETOOTH, CtrlType.MSG_RESPONSE_SPEECH_RECOGNITION_HANDLER,
					ResponseCode.METHOD_START_SPEECH_RECOGNIZER_SIMPLE, message);
			return;
		}
		
		if (customFormula == true)
		{

		}
		else
		{
			startVoiceRecognitionActivity(promptInfomation);
		}

	}

	private void startVoiceRecognitionActivity(String promptInfomation)
	{
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, promptInfomation);
		try
		{
			((Activity) mContext).startActivityForResult(intent, CtrlType.REQUEST_CODE_GOOGLE_SPEECH_SIMPLE);
		}
		catch (ActivityNotFoundException e)
		{

			Logs.showTrace("[ERROR] intent actitvity is not Found: " + e.toString());
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_ACTIVITY_NOT_FOUND, CtrlType.MSG_RESPONSE_SPEECH_RECOGNITION_HANDLER,
					ResponseCode.METHOD_START_SPEECH_RECOGNIZER_SIMPLE, message);
			message.clear();

		}

	}

	private boolean MediaCheck()
	{
		if (MediaCheckUtility.getMicrophoneExists(mContext) == true
				&& MediaCheckUtility.getMicrophoneExists(mContext) == true
				&& MediaCheckUtility.getSpeechRecognizeAvailable(mContext) == true)
		{
			Logs.showTrace("MediaCheckUtility all GREEN");
			return true;
		}

		Logs.showTrace("MediaCheckUtility has ERROR!!");

		return false;

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == CtrlType.REQUEST_CODE_GOOGLE_SPEECH_SIMPLE)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				final ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				for (int i = 0; i < matches.size(); i++)
				{
					Log.d("match", matches.get(i));
				}
				message.put("message",matches.get(0) );
				callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_SPEECH_RECOGNITION_HANDLER,
						ResponseCode.METHOD_RETURN_TEXT_SPEECH_RECOGNIZER_SIMPLE, message);
				message.clear();
			}
		}
		if (requestCode == CtrlType.REQUEST_CODE_GOOGLE_SPEECH_CUSTOM)
		{

		}

	}

}
