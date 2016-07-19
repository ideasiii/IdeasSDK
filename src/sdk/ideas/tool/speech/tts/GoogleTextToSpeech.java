package sdk.ideas.tool.speech.tts;

import java.util.HashMap;
import java.util.Locale;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import sdk.ideas.common.Logs;

public class GoogleTextToSpeech
{
	private TextToSpeech tts = null;
	private Context mContext = null;
	private Locale defaultLocale = Locale.getDefault();

	public GoogleTextToSpeech(Context mContext)
	{
		this.mContext = mContext;
		init();
	}

	public GoogleTextToSpeech(Context mContext, Locale mLocale)
	{
		this.mContext = mContext;
		defaultLocale = mLocale;
		init();
	}

	private void init()
	{
		tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener()
		{
			@Override
			public void onInit(int status)
			{
				if (status != TextToSpeech.ERROR)
				{
					int result = tts.setLanguage(defaultLocale);
					
					//	假設手機不支援此語言tts
					 if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
					 {
						Logs.showTrace("This Language is not supported");
						Intent installIntent = new Intent();
						installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
						((Activity)mContext).startActivity(installIntent);
					 }
				}
				else
				{
					Logs.showTrace("ERROR status is ERROR");
				}
			}
		});
	}

	public void textToSpeech(String text)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			ttsGreater21(text);
		}
		else
		{
			ttsUnder20(text);
		}
	}

	public void stop()
	{
		tts.stop();
		tts.shutdown();
	}

	@SuppressWarnings("deprecation")
	private void ttsUnder20(String text)
	{
		Logs.showTrace("Device under 21!");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void ttsGreater21(String text)
	{
		Logs.showTrace("Device greater 21!");
		String utteranceId = this.hashCode() + "";
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
	}

}
