package sdk.ideas.iot.amx.mode;

import android.content.Context;
import android.os.Message;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.ModeBehavior;

public class AMXModeHandler extends AMXBaseHandler implements ModeBehavior
{

	@Override
	public void handleControlMessage(Message msg)
	{

	}

	@Override
	public void handleStatusMessage(Message msg)
	{
		return;
	}

	public AMXModeHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);
	}

	@Override
	public void changeModeBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_MODE_SPEECH, AMXParameterSetting.DEVICE_MODE_CINEMA))
		{
			super.mAMXDataTransmitHandler.sendControlCommand(super.trasferToJsonCommand(
					AMXParameterSetting.TYPE_CONTROL_COMMAND, AMXParameterSetting.FUCNTON_MODE_SWITCH, index, 0));
		}
		else
		{
			// callback ERROR: invalid value

			
			
		}
	}

}
