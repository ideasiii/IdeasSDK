package sdk.ideas.iot.amx.light;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.PowerBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;
import sdk.ideas.module.Controller;

public class AMXLightHandler extends AMXBaseHandler implements PowerBehavior, StatusQueryBehavior
{
	@Override
	protected void handleControlMessage(Message msg)
	{
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_LIGHT_HANDLER, msg);
	}

	@Override
	protected void handleStatusMessage(Message msg)
	{
		if (msg.what == CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER)
		{
			super.handleStatusResponseMessage(CtrlType.MSG_RESPONSE_AMX_LIGHT_HANDLER,
					ResponseCode.METHOD_AMX_STATUS_COMMAND, msg);
		}
		else if (msg.what == CtrlType.MSG_RESPONSE_AMXBROADCAST_TRANSMIT_HANDLER)
		{
			if (msg.arg1 == ResponseCode.ERR_SUCCESS)
			{
				try
				{
					if (isFunctionIDSame(((HashMap<String, String>) msg.obj).get("message"),
							AMXParameterSetting.FUNCTION_LIGHT))
					{
						super.handleStatusResponseMessage(CtrlType.MSG_RESPONSE_AMX_LIGHT_HANDLER,
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
				Logs.showTrace("[AMXLightHandler] ERROR while AMXBROADCAST, message: " + msg.obj);
			}
		}

	}

	public AMXLightHandler(Context mContext, String strIP, int port)
	{
		super(mContext, strIP, port, String.valueOf(AMXParameterSetting.FUNCTION_LIGHT));
	}
	
	//簡報燈光
	public void brief()
	{
		super.mAMXDataTransmitHandler
		.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
				AMXParameterSetting.FUNCTION_LIGHT, AMXParameterSetting.DEVICE_LIGHT_8, AMXParameterSetting.CONTROL));
		
	}

	@Override
	public void onBehavior(int index)
	{
		if (isInInterval(index, AMXParameterSetting.DEVICE_LIGHT_1, AMXParameterSetting.DEVICE_LIGHT_7)
				|| index == AMXParameterSetting.DEVICE_LIGHT_ALL)
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUNCTION_LIGHT, index, AMXParameterSetting.CONTROL_ON));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_LIGHT_HANDLER, ResponseCode.METHOD_AMX_COTROL_COMMAND,
					"index");
		}
	}

	@Override
	public void offBehavior(int index)
	{
		if (isInInterval(index, AMXParameterSetting.DEVICE_LIGHT_1, AMXParameterSetting.DEVICE_LIGHT_7)
				|| index == AMXParameterSetting.DEVICE_LIGHT_ALL)
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUNCTION_LIGHT, index, AMXParameterSetting.CONTROL_OFF));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_LIGHT_HANDLER, ResponseCode.METHOD_AMX_COTROL_COMMAND,
					"index");
		}
	}

	@Override
	public void statusQuery(int index, int requestState)
	{
		if (isInInterval(index, AMXParameterSetting.DEVICE_LIGHT_1, AMXParameterSetting.DEVICE_LIGHT_7) && isInInterval(
				requestState, AMXParameterSetting.REQUEST_STATUS_POWER, AMXParameterSetting.REQUEST_STATUS_POWER))
		{
			super.mAMXDataTransmitHandler.sendStatusCommand(super.trasferToJsonCommand(
					AMXParameterSetting.TYPE_STATUS_COMMAND, AMXParameterSetting.FUNCTION_LIGHT, index, requestState));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_LIGHT_HANDLER, ResponseCode.METHOD_AMX_STATUS_COMMAND,
					"index or requestState");

		}
	}

	@Override
	public void allStatusQuery()
	{

	}

}
