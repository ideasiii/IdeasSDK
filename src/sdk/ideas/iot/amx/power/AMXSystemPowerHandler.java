package sdk.ideas.iot.amx.power;

import java.util.HashMap;
import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.PowerBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;
import sdk.ideas.module.Controller;

public class AMXSystemPowerHandler extends AMXBaseHandler implements PowerBehavior, StatusQueryBehavior
{
	@Override
	public void handleControlMessage(Message msg)
	{
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_SYSTEMPOWER_HANDLER, msg, null);
	}

	@Override
	public void handleStatusMessage(Message msg)
	{

	}

	public AMXSystemPowerHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);
	}

	@Override
	public void onBehavior(int index)
	{
		super.mAMXDataTransmitHandler
				.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
						AMXParameterSetting.FUCTION_SYSTEM_POWER, 0, AMXParameterSetting.CONTROL_ON));
	}

	@Override
	public void offBehavior(int index)
	{
		super.mAMXDataTransmitHandler
				.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
						AMXParameterSetting.FUCTION_SYSTEM_POWER, 0, AMXParameterSetting.CONTROL_OFF));
	}

	@Override
	public void statusQuery(int index, int requestState)
	{
		super.mAMXDataTransmitHandler.sendStatusCommand(super.trasferToJsonCommand(
				AMXParameterSetting.TYPE_STATUS_COMMAND, AMXParameterSetting.FUCTION_SYSTEM_POWER, 0, 0));
	}

	@Override
	public void allStatusQuery()
	{

	}

}
