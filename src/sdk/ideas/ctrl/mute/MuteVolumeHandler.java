package sdk.ideas.ctrl.mute;

import java.util.HashMap;
import android.content.Context;
import android.content.IntentFilter;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ReturnIntentAction;

public class MuteVolumeHandler extends BaseHandler
{
	private static Context mContext = null;
	private static VolumeKeyReceiver mVolumeKeyReceiver = null;
	private IntentFilter intentFilter = null;
	private static boolean isMute = true;
	private static boolean isActive = false;

	public MuteVolumeHandler(Context context)
	{
		super(context);
		mContext = context;
		ControlMuteVolume.initAudioManager(mContext);
		init();
	}

	public void init()
	{
		mVolumeKeyReceiver = new VolumeKeyReceiver();
		intentFilter = new IntentFilter();
		intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");

		mVolumeKeyReceiver.setOnReceiverListener(new ReturnIntentAction()
		{
			@Override
			public void returnIntentAction(HashMap<String, String> action)
			{
				/* 以下為未知狀況，等service 好了後再test */
				// if (isMute == true)
				{
					// if (ControlMuteVolume.getDeviceRigerMode() != 0)
					{
						// ControlMuteVolume.muteDevice(isMute, isActive);
					}
				}

			}

		});
	}

	public void startMute()
	{
		if (isActive == false)
		{
			mContext.registerReceiver(mVolumeKeyReceiver, intentFilter);
		}

		if (isActive && isMute)
		{
			return;
		}
		isActive = true;
		isMute = true;
		ControlMuteVolume.muteDevice(isMute, isActive, mResponseMessage);

		returnRespose(CtrlType.MSG_RESPONSE_VOLUME_HANDLER,
				ResponseCode.METHOLD_VOLUME_MUTE_ENABLE);
	}

	public void stopMute()
	{
		if (isActive == true)
		{
			isMute = false;
			ControlMuteVolume.muteDevice(isMute, isActive, mResponseMessage);
			returnRespose(CtrlType.MSG_RESPONSE_VOLUME_HANDLER,
					ResponseCode.METHOLD_VOLUME_MUTE_DISABLE);
		}

	}

	public void stopReceiver()
	{
		if (isActive == false)
		{
			return;
		}
		try
		{
			if (null != mContext && null != mVolumeKeyReceiver)
			{
				Logs.showTrace("unregisterReceiver");
				mContext.unregisterReceiver(mVolumeKeyReceiver);
				isActive = false;
			}
		}
		catch (Exception e)
		{
			Logs.showTrace(e.toString());
		}
	}
}
