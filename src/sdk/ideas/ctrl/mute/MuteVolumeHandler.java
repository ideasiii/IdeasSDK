package sdk.ideas.ctrl.mute;

import android.content.Context;
import android.content.IntentFilter;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;

public class MuteVolumeHandler extends BaseHandler
{
	@SuppressWarnings("unused")
	private static VolumeKeyReceiver	mVolumeKeyReceiver	= null;
	@SuppressWarnings("unused")
	private IntentFilter				intentFilter		= null;

	private static boolean				isMute				= false;
	// private static boolean isActive = false;

	public MuteVolumeHandler(Context context)
	{
		super(context);
		ControlMuteVolume.initAudioManager(mContext);
		// init();
	}
	/*
	 * private void init() { mVolumeKeyReceiver = new VolumeKeyReceiver(); intentFilter = new IntentFilter(); intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
	 * mVolumeKeyReceiver.setOnReceiverListener(new ReturnIntentAction() {
	 * @Override public void returnIntentAction(HashMap<String, String> action) { // �H�U���������p�A��service �n�F��Atest // if (isMute == true) { // if
	 * (ControlMuteVolume.getDeviceRigerMode() != 0) { // ControlMuteVolume.muteDevice(isMute, isActive); } } } }); }
	 */

	public void startMute()
	{
		/*
		 * if (isActive == false) { //mContext.registerReceiver(mVolumeKeyReceiver, intentFilter); }
		 */

		if (/* isActive == true && */ isMute == true)
		{
			return;
		}
		// isActive = true;
		isMute = true;
		ControlMuteVolume.muteDevice(isMute, mResponseMessage);

		returnRespose(CtrlType.MSG_RESPONSE_VOLUME_HANDLER, ResponseCode.METHOLD_VOLUME_MUTE_ENABLE);
	}

	public void stopMute()
	{
		if (isMute == false)
		{
			return;
		}
		// if (isActive == true)
		// {
		isMute = false;
		ControlMuteVolume.muteDevice(isMute, mResponseMessage);
		returnRespose(CtrlType.MSG_RESPONSE_VOLUME_HANDLER, ResponseCode.METHOLD_VOLUME_MUTE_DISABLE);

		// isActive = false;
		// }

	}

	/*
	 * private void stopReceiver() { if (isActive == false) { return; } try { if (null != mContext && null != mVolumeKeyReceiver) { Logs.showTrace("unregisterReceiver");
	 * mContext.unregisterReceiver(mVolumeKeyReceiver); isActive = false; } } catch (Exception e) { Logs.showTrace(e.toString()); } }
	 */
}
