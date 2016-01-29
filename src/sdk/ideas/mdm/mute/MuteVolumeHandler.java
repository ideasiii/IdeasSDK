package sdk.ideas.mdm.mute;

import android.content.Context;
import android.content.IntentFilter;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.mute.VolumeKeyReceiver.ReturnVolumeAction;

public class MuteVolumeHandler
{
	private static Context mContext = null;
	private static VolumeKeyReceiver keyReceiver = null;
	private IntentFilter intentFilter = null;
	private static boolean isMute = true;
	private static boolean isActive = false;
	private ControlMuteVolume mControlMuteVolume = null;

	public MuteVolumeHandler(Context context)
	{
		mContext = context;
		mControlMuteVolume = new ControlMuteVolume(mContext);
		ControlMuteVolume.initAudio();
		init();
	}

	public void init()
	{
		keyReceiver = new VolumeKeyReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
		
		keyReceiver.setOnVolumeKeyReceiverListener(new ReturnVolumeAction()
		{

			@Override
			public void onVolumeChange(String action)
			{
				Logs.showTrace("@@@@@isMute: " + String.valueOf(isMute) + " isActive: " + String.valueOf(isActive));
				if (mControlMuteVolume.getDeviceRigerMode() != 0)
				{
					ControlMuteVolume.muteDevice(isMute, isActive);
				}
			}

		});
	}

	public void startMute()
	{
		if (isActive == false)
		{
			mContext.registerReceiver(keyReceiver, intentFilter);
		}

		isActive = true;
		isMute = true;
		ControlMuteVolume.muteDevice(isMute, isActive);
		
	}

	public void stopMute()
	{
		isMute = false;

		Logs.showTrace("######isMute: " + String.valueOf(isMute) + " isActive: " + String.valueOf(isActive));
		if(null!=mControlMuteVolume)
			ControlMuteVolume.muteDevice(isMute, isActive);

	}

	public void stopReceiver()
	{
		if (isActive == false)
			return;
		
		
		try
		{
			if (null != mContext && null != keyReceiver)
			{
				Logs.showTrace("unregisterReceiver");
				mContext.unregisterReceiver(keyReceiver);
				isActive = false;
			}
		}
		catch (Exception e)
		{
			Logs.showTrace(e.toString());
		}
	}
}
