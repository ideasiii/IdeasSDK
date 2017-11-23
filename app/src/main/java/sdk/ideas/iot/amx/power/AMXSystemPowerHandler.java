package sdk.ideas.iot.amx.power;

import java.util.HashMap;
import org.json.JSONException;
import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.PowerBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;

public class AMXSystemPowerHandler extends AMXBaseHandler implements PowerBehavior, StatusQueryBehavior
{
	@Override
	protected void handleControlMessage(Message msg)
	{
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_SYSTEMPOWER_HANDLER, msg);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleStatusMessage(Message msg)
	{
		if (msg.what == CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER)
		{
			super.handleStatusResponseMessage(CtrlType.MSG_RESPONSE_AMX_SYSTEMPOWER_HANDLER,
					ResponseCode.METHOD_AMX_STATUS_COMMAND, msg);
		}
		else if (msg.what == CtrlType.MSG_RESPONSE_AMXBROADCAST_TRANSMIT_HANDLER)
		{
			if (msg.arg1 == ResponseCode.ERR_SUCCESS)
			{
				try
				{
					if (isFunctionIDSame(((HashMap<String, String>) msg.obj).get("message"),
							AMXParameterSetting.FUNCTION_SYSTEM_POWER))
					{
						super.handleStatusResponseMessage(CtrlType.MSG_RESPONSE_AMX_SYSTEMPOWER_HANDLER,
								ResponseCode.METHOD_AMX_STATUS_RESPONSE_COMMAND, msg);
					}

				}
				catch (JSONException e)
				{
					Logs.showTrace(e.toString());
				}
				catch (ClassCastException e)
				{
					Logs.showTrace(e.toString());
				}
			}
			else
			{
				Logs.showTrace("[AMXSystemPowerHandler] ERROR while AMXBROADCAST, message: " + msg.obj);
			}

		}

	}

	public AMXSystemPowerHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort, String.valueOf(AMXParameterSetting.FUNCTION_SYSTEM_POWER));
	}

	@SuppressWarnings("static-access")
	@Override
	public void onBehavior(int index)
	{
		super.mAMXDataTransmitHandler
				.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
						AMXParameterSetting.FUNCTION_SYSTEM_POWER, 0, AMXParameterSetting.CONTROL_ON));
	}

	@SuppressWarnings("static-access")
	@Override
	public void offBehavior(int index)
	{
		super.mAMXDataTransmitHandler
				.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
						AMXParameterSetting.FUNCTION_SYSTEM_POWER, 0, AMXParameterSetting.CONTROL_OFF));
	}

	@SuppressWarnings("static-access")
	@Override
	public void statusQuery(int index, int requestState)
	{
		super.mAMXDataTransmitHandler
				.sendStatusCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_STATUS_COMMAND,
						AMXParameterSetting.FUNCTION_SYSTEM_POWER, 0, AMXParameterSetting.REQUEST_STATUS_POWER));
	}

	@Override
	public void allStatusQuery()
	{

	}

}
