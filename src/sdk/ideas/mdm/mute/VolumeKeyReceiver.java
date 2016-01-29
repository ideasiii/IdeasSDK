package sdk.ideas.mdm.mute;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import sdk.ideas.common.Logs;

public class VolumeKeyReceiver extends BroadcastReceiver
{
	private VolumeKeyReceiver.ReturnVolumeAction listener = null;

	public VolumeKeyReceiver()
	{

	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Logs.showTrace(intent.getAction());
		if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION") && null != listener)
		{
			listener.onVolumeChange(intent.getAction());
		}
	}

	public void setOnVolumeKeyReceiverListener(VolumeKeyReceiver.ReturnVolumeAction listener)
	{
		this.listener = listener;
	}

	interface ReturnVolumeAction
	{
		void onVolumeChange(String action);

	}
}