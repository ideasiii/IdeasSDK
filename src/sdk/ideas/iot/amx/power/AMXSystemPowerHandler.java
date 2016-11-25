package sdk.ideas.iot.amx.power;

import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.PowerBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;

public class AMXSystemPowerHandler extends AMXBaseHandler implements PowerBehavior, StatusQueryBehavior
{
	@Override
	public void handleControlMessage(Message msg)
	{
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_SYSTEMPOWER_HANDLER, msg);
	}

	@Override
	public void handleStatusMessage(Message msg)
	{
		super.handleStatusResponseMessage(CtrlType.MSG_RESPONSE_AMX_SYSTEMPOWER_HANDLER, msg);
	}

	public AMXSystemPowerHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort,String.valueOf(AMXParameterSetting.FUNCTION_SYSTEM_POWER));
	}

	@Override
	public void onBehavior(int index)
	{
		super.mAMXDataTransmitHandler
				.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
						AMXParameterSetting.FUNCTION_SYSTEM_POWER, 0, AMXParameterSetting.CONTROL_ON));
	}

	@Override
	public void offBehavior(int index)
	{
		super.mAMXDataTransmitHandler
				.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
						AMXParameterSetting.FUNCTION_SYSTEM_POWER, 0, AMXParameterSetting.CONTROL_OFF));
	}

	@Override
	public void statusQuery(int index, int requestState)
	{
		super.mAMXDataTransmitHandler.sendStatusCommand(super.trasferToJsonCommand(
				AMXParameterSetting.TYPE_STATUS_COMMAND, AMXParameterSetting.FUNCTION_SYSTEM_POWER, 0, AMXParameterSetting.REQUEST_STATUS_POWER));
	}

	@Override
	public void allStatusQuery()
	{

	}

}
