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
	private Locale defaultLocale = Locale.getDefault();
	private static int textID = 0;
	private boolean returnInitValue = false;

	public TextToSpeechHandler(Context mContext)
	{
		super(mContext);
	}

	public TextToSpeechHandler(Context mContext, Locale mLocale)
	{
		super(mContext);
		defaultLocale = mLocale;
	}

	public void init()
	{

		if (checkPackageExist(mContext, "com.google.android.tts") == false)
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
				tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener()
				{

					@Override
					public void onInit(int status)
					{
						//Logs.showTrace("status: " + String.valueOf(status));

						if (status == TextToSpeech.SUCCESS)
						{
							//Logs.showTrace("TextToSpeech onInit");
							int result = tts.setLanguage(defaultLocale);

							//Logs.showTrace("Set Language Result: " + result);

							if (result == TextToSpeech.LANG_MISSING_DATA)
							{
								HashMap<String, String> message = new HashMap<String, String>();
								message.put("message", "this Language is missing data, please download it");

								callBackMessage(ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION,
										CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
										ResponseCode.METHOD_TEXT_TO_SPEECH_INIT, message);

							}
							// 假設手機不支援此語言tts
							else if (result == TextToSpeech.LANG_NOT_SUPPORTED)
							{
								HashMap<String, String> message = new HashMap<String, String>();
								message.put("message", "this Language is not supported, please try another ones");

								callBackMessage(ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION,
										CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
										ResponseCode.METHOD_TEXT_TO_SPEECH_INIT, message);

							}

							else
							{
								returnInitValue = true;
								
								HashMap<String, String> message = new HashMap<String, String>();
								message.put("message", "init success");

								callBackMessage(ResponseCode.ERR_SUCCESS,
										CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
										ResponseCode.METHOD_TEXT_TO_SPEECH_INIT, message);
								
								tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
								{

									@SuppressLint("NewApi")
									@Override
									public void onError(String utteranceId, int errorCode)
									{
									//	Logs.showTrace("speech onError!!!");
									//	Logs.showTrace(
									//			"Text ID: " + utteranceId + "ERROR Code: " + String.valueOf(errorCode));

										HashMap<String, String> message = new HashMap<String, String>();

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
									//	Logs.showTrace("speech onStop!");
									//	Logs.showTrace("Text ID: " + utteranceId + "Interrupted: "
									//			+ String.valueOf(interrupted));

										HashMap<String, String> message = new HashMap<String, String>();

										message.put("TextID", utteranceId);
										message.put("TextStatus", "STOP");
										message.put("message", "the speech is stoped");
										callBackMessage(ResponseCode.ERR_SUCCESS,
												CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
												ResponseCode.METHOD_TEXT_TO_SPEECH_SPEECHING, message);

										super.onStop(utteranceId, interrupted);
									}

									@Override
									public void onStart(String utteranceId)
									{
									//	Logs.showTrace(utteranceId);
									//	Logs.showTrace("speech onStart!");

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
									//	Logs.showTrace(utteranceId);
									//	Logs.showTrace("speech onDone!");
										HashMap<String, String> message = new HashMap<String, String>();

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
									//	Logs.showTrace(utteranceId);
									//	Logs.showTrace("speech onError!");

										HashMap<String, String> message = new HashMap<String, String>();

										message.put("TextID", utteranceId);
										message.put("TextStatus", "ERROR");
										message.put("message", "the speech occured error while speeching");

										callBackMessage(ResponseCode.ERR_UNKNOWN,
												CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
												ResponseCode.METHOD_TEXT_TO_SPEECH_SPEECHING, message);
									}

								});

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

	public void textToSpeech(String text, String textID)
	{
		if (returnInitValue == true && null != tts)
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
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("message", "init frist");

			callBackMessage(ResponseCode.ERR_NOT_INIT, CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
					ResponseCode.METHOD_TEXT_TO_SPEECH_SPEECHING, message);
		}
	}

	public void stop()
	{
		if (returnInitValue == true && null != tts)
		{
			tts.stop();
		}
		else
		{
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("message", "init frist");
			callBackMessage(ResponseCode.ERR_NOT_INIT, CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER,
					ResponseCode.METHOD_TEXT_TO_SPEECH_SPEECHING, message);
		}
	}

	public void shutdown()
	{
		if (returnInitValue == true && null != tts)
		{
			tts.shutdown();
			returnInitValue = false;
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
			boolean getSysPackages = false;
			List<PackageInfo> packs = mContext.getPackageManager().getInstalledPackages(0);
			for (int i = 0; i < packs.size(); i++)
			{
				PackageInfo p = packs.get(i);
				if ((!getSysPackages) && (p.versionName == null))
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

}
