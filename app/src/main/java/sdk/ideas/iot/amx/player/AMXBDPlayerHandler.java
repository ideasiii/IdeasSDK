package sdk.ideas.iot.amx.player;

import java.util.HashMap;
import org.json.JSONException;
import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.PlayerBehavior;
import sdk.ideas.iot.amx.PowerBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;

public class AMXBDPlayerHandler extends AMXBaseHandler implements StatusQueryBehavior, PowerBehavior, PlayerBehavior
{

	@Override
	protected void handleControlMessage(Message msg)
	{
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_BDPLAYER_HANDLER, msg);
	}

	@Override
	protected void handleStatusMessage(Message msg)
	{
		if (msg.what == CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER)
		{
			super.handleStatusResponseMessage(CtrlType.MSG_RESPONSE_AMX_BDPLAYER_HANDLER,
					ResponseCode.METHOD_AMX_STATUS_COMMAND, msg);
		}
		else if (msg.what == CtrlType.MSG_RESPONSE_AMXBROADCAST_TRANSMIT_HANDLER)
		{
			if (msg.arg1 == ResponseCode.ERR_SUCCESS)
			{
				try
				{
					if (isFunctionIDSame(((HashMap<String, String>) msg.obj).get("message"),
							AMXParameterSetting.FUNCTION_BD_PLAYER))
					{
						super.handleStatusResponseMessage(CtrlType.MSG_RESPONSE_AMX_BDPLAYER_HANDLER,
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
				Logs.showTrace("[AMXBDPlayerHandler] ERROR while AMXBROADCAST, message: " + msg.obj);
			}
		}

	}

	public AMXBDPlayerHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort, String.valueOf(AMXParameterSetting.FUNCTION_BD_PLAYER));
	}

	@Override
	public void bdPlayerBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.CONTROL_BD_POWER, AMXParameterSetting.CONTROL_BD_SUBTITLE))
		{
			super.mAMXDataTransmitHandler.sendControlCommand(super.trasferToJsonCommand(
					AMXParameterSetting.TYPE_CONTROL_COMMAND, AMXParameterSetting.FUNCTION_BD_PLAYER, 0, index));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_BDPLAYER_HANDLER,
					ResponseCode.METHOD_AMX_COTROL_COMMAND, "index");
		}

	}

	@Override
	public void onBehavior(int index)
	{
		// not yet define
	}

	@Override
	public void offBehavior(int index)
	{
		// not yet define
	}

	@Override
	public void statusQuery(int index, int requestState)
	{
		if (super.isInInterval(requestState, AMXParameterSetting.REQUEST_STATUS_POWER,
				AMXParameterSetting.REQUEST_STATUS_POWER))
		{
			super.mAMXDataTransmitHandler.sendStatusCommand(super.trasferToJsonCommand(
					AMXParameterSetting.TYPE_STATUS_COMMAND, AMXParameterSetting.FUNCTION_BD_PLAYER, 0, requestState));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_BDPLAYER_HANDLER,
					ResponseCode.METHOD_AMX_STATUS_COMMAND, "requestState");
		}
	}

	@Override
	public void allStatusQuery()
	{

	}

}
