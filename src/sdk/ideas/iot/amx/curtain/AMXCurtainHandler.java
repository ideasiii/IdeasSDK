package sdk.ideas.iot.amx.curtain;

import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.LiftingBehavior;

public class AMXCurtainHandler extends AMXBaseHandler implements LiftingBehavior
{

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

	@Override
	public void handleMessage(Message msg)
	{
		if (msg.what == CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER)
		{
			if (msg.arg2 == ResponseCode.METHOD_COTROL_COMMAND_AMX)
			{
				
			}

		}

	}

}
