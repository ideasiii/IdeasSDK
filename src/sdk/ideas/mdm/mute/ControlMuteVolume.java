package sdk.ideas.mdm.mute;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode.ResponseMessage;

public class ControlMuteVolume
{

	public static AudioManager audioManager = null;

	public static void initAudioManager(final Context mContext)
	{
		audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}

	public static int getDeviceRigerMode()
	{
		if (null != audioManager)
		{
			switch (audioManager.getRingerMode())
			{
			case AudioManager.RINGER_MODE_NORMAL:
				return AudioManager.RINGER_MODE_NORMAL;
			case AudioManager.RINGER_MODE_SILENT:
				return AudioManager.RINGER_MODE_SILENT;
			case AudioManager.RINGER_MODE_VIBRATE:
				return AudioManager.RINGER_MODE_VIBRATE;
			default:
				return -1;
			}
		}
		return -1;

	}

	/**
	 * Mute the device
	 */
	@SuppressWarnings({ "deprecation" })
	@TargetApi(23)
	public static boolean muteDevice(boolean mute, boolean isActive, ResponseMessage mResponseMessage)
	{
		try{
		if (isActive == true)
		{
			if (mute == true)
			{
				Logs.showTrace("set mute");
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
				{
					audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE,
							AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
					audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE,
							AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
					audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE,
							AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
					audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE,
							AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
				}
				else
				{

					audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
					audioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
					audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
					audioManager.setStreamMute(AudioManager.STREAM_RING, true);
				}
			}
			else
			{
				Logs.showTrace("set unmute");
				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
				{
					audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,
							AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
					audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE,
							AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
					audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE,
							AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
				}
				else
				{
					//for (int i = 0; i < 4; i++)
					{
						audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
						audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
						audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
						audioManager.setStreamMute(AudioManager.STREAM_RING, false);
					}
				}
			}

		}
		}
	catch(Exception e)
	{
		mResponseMessage.mnCode = 0;
		mResponseMessage.mStrContent = e.toString();
		return false;
		
	}
		mResponseMessage.mnCode = 1;
		mResponseMessage.mStrContent = "success";
		
		return true;
	}

}
