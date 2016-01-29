package sdk.ideas.mdm.mute;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import sdk.ideas.common.Logs;

public class ControlMuteVolume
{

	public static AudioManager audioManager = 	null;

	private static Context mContext = null; 
	public ControlMuteVolume(Context context)
	{
		mContext = context;
	    
	}
	public static void initAudio()
	{
		audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	
	
	public int getDeviceRigerMode()
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
	public static void muteDevice( boolean mute, boolean isActive)
	{

		Logs.showTrace("in mute Device");
		if (isActive == true)
		{
			if (null != mContext)
			{
				if (mute == true)
				{

					audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
					{
						audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
						audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
						audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
						audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
					}
					else
					{
						Logs.showTrace("set mute");
						audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI );
						audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_VIBRATE);
						
						//audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI );
						//audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE,  AudioManager.FLAG_PLAY_SOUND);
						
						//audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI );
						//audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND);
						
						audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
						audioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
						audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
						//audioManager.setStreamMute(AudioManager.STREAM_RING, true);
					 
					}
				}
				else
				{
					audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
					{
						audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
						audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
						audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
					}
					else
					{
						Logs.showTrace("set unmute");
						
						//audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
						//audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
						//audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
						audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
						audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
						audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
						//audioManager.setStreamMute(AudioManager.STREAM_RING, false);
						
					
					}
				}
			}

		}
	}

}
