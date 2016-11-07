package sdk.ideas.iot.amx.light;

import android.content.Context;
import android.os.Message;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.PowerBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;

public class AMXLightHandler extends AMXBaseHandler implements PowerBehavior, StatusQueryBehavior
{
	@Override
	public void handleControlMessage(Message msg)
	{
		if(msg.arg1 == )
		{
			
		}
	}

	@Override
	public void handleStatusMessage(Message msg)
	{
		if(msg.arg1 == )
		{
			
		}
		
		
		
	}

	public AMXLightHandler(Context mContext, String strIP, int port)
	{
		super(mContext, strIP, port);
	}

	@Override
	public void onBehavior(int index)
	{
		if (isInInterval(index, AMXParameterSetting.DEVICE_LIGHT_1, AMXParameterSetting.DEVICE_LIGHT_8)
				|| index == AMXParameterSetting.DEVICE_LIGHT_ALL)
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUCTION_LIGHT, index, AMXParameterSetting.CONTROL_ON));
		}
		else
		{
			// callback ERROR: invalid value
		}
	}

	@Override
	public void offBehavior(int index)
	{
		if (isInInterval(index, AMXParameterSetting.DEVICE_LIGHT_1, AMXParameterSetting.DEVICE_LIGHT_8)
				|| index == AMXParameterSetting.DEVICE_LIGHT_ALL)
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUCTION_LIGHT, index, AMXParameterSetting.CONTROL_OFF));
		}
		else
		{
			// callback ERROR: invalid value
		}
	}

	@Override
	public void statusQuery(int index, int requestState)
	{
		if (isInInterval(index, AMXParameterSetting.DEVICE_LIGHT_1, AMXParameterSetting.DEVICE_LIGHT_7) && isInInterval(
				requestState, AMXParameterSetting.REQUEST_STATUS_POWER, AMXParameterSetting.REQUEST_STATUS_POWER))
		{
			super.mAMXDataTransmitHandler.sendStatusCommand(super.trasferToJsonCommand(
					AMXParameterSetting.TYPE_STATUS_COMMAND, AMXParameterSetting.FUCTION_LIGHT, index, requestState));
		}
		else
		{
			// callback ERROR: invalid value
			
		}
	}

}
