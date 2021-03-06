package sdk.ideas.tool.speech.tts;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

public class TextToSpeechHandler extends BaseHandler
{
	private TextToSpeech tts = null;
	private Locale mLocale = Locale.TAIWAN;

	// 要切換的語言，當執行 TextToSpeech() 方法時會檢查是否需重新產生一個不同語言的 TTS  物件
	private Locale mPostponedChangingLocale = null;
	private float mPitch;
	private float mRate;

	// 尚未成功說完的TTS要求，尚未說完的原因可能有：正在切換語言、正在執行...等
	private SpeechElement pendingSpeech;

	private static int textID = 0;
	private boolean mTtsServiceHasInitialized = false;

	public TextToSpeechHandler(Context mContext)
	{
		super(mContext);
	}

	public TextToSpeechHandler(Context mContext, Locale locale)
	{
		super(mContext);
		mLocale = locale;
	}

	public void setLocale(Locale locale)
	{
	    if (locale != null && !mLocale.equals(locale))
        {
            mPostponedChangingLocale = locale;
        }
	}

	public Locale getLocale()
	{
		return mLocale;
	}
	
	public void init()
	{
		if (!checkPackageExist(mContext, "com.google.android.tts"))
		{
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("message", "package not found error");
			message.put("packageName", "com.google.android.tts");

			callBackMessage(ResponseCode.ERR_PACKAGE_NOT_FIND, CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
					ResponseCode.METHOD_TEXT_TO_SPEECH_INIT, message);
		}
		else
		{
			try
			{
                shutdown();

				tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener()
				{
					@Override
					public void onInit(int status)
					{
						// Logs.showTrace("status: " + String.valueOf(status));

						if (status == TextToSpeech.SUCCESS)
						{
							// Logs.showTrace("TextToSpeech onInit");
                            if (mPostponedChangingLocale != null)
                            {
                                mLocale = mPostponedChangingLocale;
                                mPostponedChangingLocale = null;
                            }

							int result = tts.setLanguage(mLocale);
							if (result == TextToSpeech.LANG_MISSING_DATA)
							{
								HashMap<String, String> message = new HashMap<>();
								message.put("message", "this Language is missing data, please download it");

								callBackMessage(ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION,
										CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
										ResponseCode.METHOD_TEXT_TO_SPEECH_INIT, message);

							}
							else if (result == TextToSpeech.LANG_NOT_SUPPORTED)
							{
							    // 假設手機不支援此語言tts
								HashMap<String, String> message = new HashMap<String, String>();
								message.put("message", "this Language is not supported, please try another ones");

								callBackMessage(ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION,
										CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
										ResponseCode.METHOD_TEXT_TO_SPEECH_INIT, message);

							}
							else
							{
								mTtsServiceHasInitialized = true;
                                tts.setPitch(mPitch);
                                tts.setSpeechRate(mRate);

								HashMap<String, String> message = new HashMap<String, String>();
								message.put("message", "init success");

								callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
										ResponseCode.METHOD_TEXT_TO_SPEECH_INIT, message);

								tts.setOnUtteranceProgressListener(mUtteranceProgressListener);

                                // flush cache
                                if (null != pendingSpeech)
                                {
                                    if (pendingSpeech.tryCount <= 1)
                                    {
                                        Logs.showTrace("TTSCache holds a pending request, do it");
                                        setPitch(pendingSpeech.pitch);
                                        setSpeechRate(pendingSpeech.rate);
                                        textToSpeech(pendingSpeech.text, pendingSpeech.utteranceId);
                                    }
                                    else
                                    {
                                        pendingSpeech = null;
                                    }
                                }
							}
						}
						else
						{
							HashMap<String, String> message = new HashMap<String, String>();
							message.put("message", "ERROR status is" + status + " while new TextToSpeech method");

							callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
									ResponseCode.METHOD_TEXT_TO_SPEECH_INIT, message);
						}
					}
				});
			}
			catch (Exception e)
			{
				HashMap<String, String> message = new HashMap<String, String>();
				message.put("message", e.toString());

				callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
						ResponseCode.METHOD_TEXT_TO_SPEECH_INIT, message);
			}
		}
	}

	public synchronized static int getTextID()
	{
		textID++;
		return textID;
	}

	public void downloadTTS()
	{
		final String appPackageName = "com.google.android.tts";
		try
		{
			((Activity) mContext)
					.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
		}
		catch (android.content.ActivityNotFoundException anfe)
		{
			((Activity) mContext).startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
		}
	}

	public void setPitch(float pitch)
	{
		tts.setPitch(pitch);
		mPitch = pitch;
	}

	public void setSpeechRate(float rate)
	{
		tts.setSpeechRate(rate);
		mRate = rate;
	}

	public void textToSpeech(String text, String textID)
	{
        pendingSpeech = new SpeechElement();
        pendingSpeech.text = text;
        pendingSpeech.utteranceId = textID;
        pendingSpeech.pitch = mPitch;
        pendingSpeech.rate = mRate;

		if (mPostponedChangingLocale != null)
		{
			Logs.showTrace("Need to regenerate TTS service to change language");
			Logs.showTrace("Queue this request until new TTS service is initialized: `" + text + "`");

			init();
			return;
		}

		if (mTtsServiceHasInitialized && null != tts)
		{
			if (null == text)
			{
				text = "";
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			{
				ttsGreater21(text, textID);
			}
			else
			{
				ttsUnder20(text, textID);
			}
		}
		else
		{
			HashMap<String, String> message = new HashMap<>();
			message.put("message", "init first");
			callBackMessage(ResponseCode.ERR_NOT_INIT, CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
					ResponseCode.METHOD_TEXT_TO_SPEECH_SPEECHING, message);
		}
	}

	public void stop()
	{
		if (mTtsServiceHasInitialized && null != tts)
		{
			tts.stop();
		}
		else
		{
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("message", "init frist");
			//Logs.showTrace("line 307");
			callBackMessage(ResponseCode.ERR_NOT_INIT, CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
					ResponseCode.METHOD_TEXT_TO_SPEECH_SPEECHING, message);
		}
	}

	public void shutdown()
	{
		if (null != tts)
		{
			tts.shutdown();
			mTtsServiceHasInitialized = false;
			tts = null;
		}
	}

	@SuppressWarnings("deprecation")
	private void ttsUnder20(String text, String textID)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		if (null == textID)
		{
			textID = String.valueOf(TextToSpeechHandler.getTextID());
		}

		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, textID);
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, params);

		params.clear();
		params = null;

	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void ttsGreater21(String text, String textID)
	{
		if (null == textID)
		{
			textID = String.valueOf(TextToSpeechHandler.getTextID());
		}
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, textID);
	}

	private boolean checkPackageExist(Context mContext, String packageName)
	{
		if (null != mContext && null != packageName)
		{
			List<PackageInfo> packs = mContext.getPackageManager().getInstalledPackages(0);
			for (int i = 0; i < packs.size(); i++)
			{
				PackageInfo p = packs.get(i);
				if (p.versionName == null)
				{
					continue;
				}

				if (packageName.equals(p.packageName))
				{
					return true;
				}
			}
			return false;
		}
		return false;
	}

    private static class SpeechElement
    {
        String text;
        String utteranceId;
        float pitch;
        float rate;
        byte tryCount = 0;
    }

	private UtteranceProgressListener mUtteranceProgressListener = new UtteranceProgressListener()
    {
        @SuppressLint("NewApi")
        @Override
        public void onError(String utteranceId, int errorCode)
        {
            // Logs.showTrace("speech onError!!!");
            // Logs.showTrace("Text ID: " + utteranceId + "ERROR Code: " + String.valueOf(errorCode));

            if (pendingSpeech != null && pendingSpeech.tryCount < 1)
            {
                pendingSpeech.tryCount++;

                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                shutdown();
                init();

                return;
            }

            pendingSpeech = null;

            HashMap<String, String> message = new HashMap<>();
            message.put("TextID", utteranceId);
            message.put("TextStatus", "ERROR");
            message.put("message", "ERROR code is" + errorCode
                    + ", please reference Google TextToSpeech Class ERROR Code");

            callBackMessage(ResponseCode.ERR_UNKNOWN,
                    CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
                    ResponseCode.METHOD_TEXT_TO_SPEECH_SPEECHING, message);

            super.onError(utteranceId, errorCode);
        }

        @SuppressLint("NewApi")
        @Override
        public void onStop(String utteranceId, boolean interrupted)
        {
            // Logs.showTrace("speech onStop!");
            // Logs.showTrace("Text ID: " + utteranceId + "Interrupted: " + String.valueOf(interrupted));

            pendingSpeech = null;

            HashMap<String, String> message = new HashMap<String, String>();
            message.put("TextID", utteranceId);
            message.put("TextStatus", "STOP");
            message.put("message", "the speech is stopped");

            callBackMessage(ResponseCode.ERR_SUCCESS,
                    CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
                    ResponseCode.METHOD_TEXT_TO_SPEECH_SPEECHING, message);

            super.onStop(utteranceId, interrupted);
        }

        @Override
        public void onStart(String utteranceId)
        {
            // Logs.showTrace(utteranceId);
            // Logs.showTrace("speech onStart!");

            HashMap<String, String> message = new HashMap<String, String>();

            message.put("TextID", utteranceId);
            message.put("TextStatus", "START");
            message.put("message", "the speech is started");
            callBackMessage(ResponseCode.ERR_SUCCESS,
                    CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
                    ResponseCode.METHOD_TEXT_TO_SPEECH_SPEECHING, message);
        }

        @Override
        public void onDone(String utteranceId)
        {
            // Logs.showTrace(utteranceId);
            // Logs.showTrace("speech onDone!");

            pendingSpeech = null;
            HashMap<String, String> message = new HashMap<>();

            message.put("TextID", utteranceId);
            message.put("TextStatus", "DONE");
            message.put("message", "the speech is done");
            callBackMessage(ResponseCode.ERR_SUCCESS,
                    CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
                    ResponseCode.METHOD_TEXT_TO_SPEECH_SPEECHING, message);
        }

        @Override
        public void onError(String utteranceId)
        {
            // Logs.showTrace(utteranceId);
            // Logs.showTrace("speech onError!");

            if (pendingSpeech != null && pendingSpeech.tryCount < 1)
            {
                pendingSpeech.tryCount++;

                try
                {
                    Thread.sleep(10);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                return;
            }

            pendingSpeech = null;

            HashMap<String, String> message = new HashMap<>();

            message.put("TextID", utteranceId);
            message.put("TextStatus", "ERROR");
            message.put("message", "the speech occurred error while speeching");

            callBackMessage(ResponseCode.ERR_UNKNOWN,
                    CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
                    ResponseCode.METHOD_TEXT_TO_SPEECH_SPEECHING, message);
        }
    };
}
