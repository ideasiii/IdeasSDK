package sdk.ideas.iot.amx.curtain;

import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.LiftingBehavior;

public class AMXCurtainHandler extends AMXBaseHandler implements LiftingBehavior
{
	@Override
	public void handleControlMessage(Message msg)
	{
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_CURTAIN_HANDLER, msg, null);
	}

	@Override
	public void handleStatusMessage(Message msg)
	{
		return;
	}

	public AMXCurtainHandler(Context mContext, String strIP, int port)
	{
		super(mContext, strIP, port);
	}

	@Override
	public void upBehavior(int index)
	{
		mAMXDataTransmitHandler.sendControlCommand(trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
				AMXParameterSetting.FUCTION_CURTAIN, 0, AMXParameterSetting.CONTROL_UP));
	}

	@Override
	public void downBehavior(int index)
	{
		mAMXDataTransmitHandler.sendControlCommand(trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
				AMXParameterSetting.FUCTION_CURTAIN, 0, AMXParameterSetting.CONTROL_DOWN));
	}

}
