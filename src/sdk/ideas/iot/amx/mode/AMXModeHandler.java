package sdk.ideas.iot.amx.mode;

import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.ModeBehavior;

public class AMXModeHandler extends AMXBaseHandler implements ModeBehavior
{

	@Override
	protected void handleControlMessage(Message msg)
	{
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_MODE_HANDLER, msg);
	}

	@Override
	protected void handleStatusMessage(Message msg)
	{
		return;
	}

	public AMXModeHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort, String.valueOf(AMXParameterSetting.FUNCTION_MODE_SWITCH));
	}

	@SuppressWarnings("static-access")
	@Override
	public void changeModeBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_MODE_SPEECH, AMXParameterSetting.DEVICE_MODE_CINEMA))
		{
			super.mAMXDataTransmitHandler.sendControlCommand(super.trasferToJsonCommand(
					AMXParameterSetting.TYPE_CONTROL_COMMAND, AMXParameterSetting.FUNCTION_MODE_SWITCH, index, 0));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_MODE_HANDLER,
					ResponseCode.METHOD_AMX_COTROL_COMMAND, "index");

		}
	}

}
