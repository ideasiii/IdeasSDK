package sdk.ideas.mdm.mute;

import android.content.Context;
import android.content.Intent;
import sdk.ideas.common.BaseReceiver;

public class VolumeKeyReceiver extends BaseReceiver
{

	public VolumeKeyReceiver()
	{
		super();
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		//Logs.showTrace(intent.getAction());
		if (null != listener)
		{
			actionData.put("Action", intent.getAction());
			listener.returnIntentAction(actionData);
		}
	}

	

}