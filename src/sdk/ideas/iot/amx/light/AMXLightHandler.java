package sdk.ideas.iot.amx.light;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
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
		HashMap<String, String> message = new HashMap<String, String>();

		if (msg.arg1 == Controller.STATUS_ROK)
		{
			JSONObject data = null;
			try
			{
				data = new JSONObject((String) msg.obj);
			}
			catch (JSONException e)
			{
				Logs.showError(e.toString());
			}

		}
		else
		{

		}

	}

	public AMXLightHandler(Context mContext, String strIP, int port)
	{
		super(mContext, strIP, port,String.valueOf(AMXParameterSetting.FUNCTION_LIGHT));
	}

	@Override
	public void onBehavior(int index)
	{
		if (isInInterval(index, AMXParameterSetting.DEVICE_LIGHT_1, AMXParameterSetting.DEVICE_LIGHT_8)
				|| index == AMXParameterSetting.DEVICE_LIGHT_ALL)
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUNCTION_LIGHT, index, AMXParameterSetting.CONTROL_ON));
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
							AMXParameterSetting.FUNCTION_LIGHT, index, AMXParameterSetting.CONTROL_OFF));
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
					AMXParameterSetting.TYPE_STATUS_COMMAND, AMXParameterSetting.FUNCTION_LIGHT, index, requestState));
		}
		else
		{
			// callback ERROR: invalid value

		}
	}

	@Override
	public void allStatusQuery()
	{

	}

}
