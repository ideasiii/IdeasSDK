package sdk.ideas.iot.amx.project;

import java.util.HashMap;
import org.json.JSONException;
import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.LiftingBehavior;
import sdk.ideas.iot.amx.PowerBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;
import sdk.ideas.iot.amx.VideoSignalBehavior;
import sdk.ideas.iot.amx.VolumeBehavior;

public class AMXProjectHandler extends AMXBaseHandler
		implements VolumeBehavior, VideoSignalBehavior, LiftingBehavior, PowerBehavior, StatusQueryBehavior
{

	@Override
	protected void handleControlMessage(Message msg)
	{
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER, msg);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void handleStatusMessage(Message msg)
	{
		if (msg.what == CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER)
		{
			super.handleStatusResponseMessage(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER,
					ResponseCode.METHOD_AMX_STATUS_COMMAND, msg);
		}
		else if (msg.what == CtrlType.MSG_RESPONSE_AMXBROADCAST_TRANSMIT_HANDLER)
		{
			if (msg.arg1 == ResponseCode.ERR_SUCCESS)
			{
				try
				{
					if (isFunctionIDSame(((HashMap<String, String>) msg.obj).get("message"),
							AMXParameterSetting.FUNCTION_PROJECT))
					{
						super.handleStatusResponseMessage(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER,
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
				Logs.showTrace("[AMXProjectHandler] ERROR while AMXBROADCAST, message: " + msg.obj);
			}
		}

	}

	public AMXProjectHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort, String.valueOf(AMXParameterSetting.FUNCTION_PROJECT));
	}

	@Override
	public void onBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_PROJECT_LEFT,
				AMXParameterSetting.DEVICE_PROJECT_RIGHT))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUNCTION_PROJECT, index, AMXParameterSetting.CONTROL_ON));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER,
					ResponseCode.METHOD_AMX_COTROL_COMMAND, "index");
		}
	}

	@Override
	public void offBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_PROJECT_LEFT,
				AMXParameterSetting.DEVICE_PROJECT_RIGHT))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUNCTION_PROJECT, index, AMXParameterSetting.CONTROL_OFF));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER,
					ResponseCode.METHOD_AMX_COTROL_COMMAND, "index");

		}
	}

	@Override
	public void upBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_PROJECT_LEFT,
				AMXParameterSetting.DEVICE_PROJECT_RIGHT))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUNCTION_PROJECT, index, AMXParameterSetting.CONTROL_UP));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER,
					ResponseCode.METHOD_AMX_COTROL_COMMAND, "index");
		}
	}

	@Override
	public void downBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_PROJECT_LEFT,
				AMXParameterSetting.DEVICE_PROJECT_RIGHT))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUNCTION_PROJECT, index, AMXParameterSetting.CONTROL_DOWN));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER,
					ResponseCode.METHOD_AMX_COTROL_COMMAND, "index");
		}
	}

	@Override
	public void hdmiBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_PROJECT_LEFT,
				AMXParameterSetting.DEVICE_PROJECT_RIGHT))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUNCTION_PROJECT, index, AMXParameterSetting.CONTROL_PROJECT_HDMI));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER,
					ResponseCode.METHOD_AMX_COTROL_COMMAND, "index");
		}
	}

	@Override
	public void vgaBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_PROJECT_LEFT,
				AMXParameterSetting.DEVICE_PROJECT_RIGHT))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUNCTION_PROJECT, index, AMXParameterSetting.CONTROL_PROJECT_VGA));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER,
					ResponseCode.METHOD_AMX_COTROL_COMMAND, "index");
		}
	}

	@Override
	public void muteBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_PROJECT_LEFT,
				AMXParameterSetting.DEVICE_PROJECT_RIGHT))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUNCTION_PROJECT, index, AMXParameterSetting.CONTROL_MUTE));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER,
					ResponseCode.METHOD_AMX_COTROL_COMMAND, "index");
		}
	}

	@Override
	public void unMuteBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_PROJECT_LEFT,
				AMXParameterSetting.DEVICE_PROJECT_RIGHT))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUNCTION_PROJECT, index, AMXParameterSetting.CONTROL_UNMUTE));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER,
					ResponseCode.METHOD_AMX_COTROL_COMMAND, "index");
		}

	}

	@Override
	public void statusQuery(int index, int requestState)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_PROJECT_LEFT, AMXParameterSetting.DEVICE_PROJECT_RIGHT)
				&& super.isInInterval(requestState, AMXParameterSetting.REQUEST_STATUS_POWER,
						AMXParameterSetting.REQUEST_STATUS_MUTE))
		{
			super.mAMXDataTransmitHandler
					.sendStatusCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_STATUS_COMMAND,
							AMXParameterSetting.FUNCTION_PROJECT, index, requestState));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER,
					ResponseCode.METHOD_AMX_STATUS_COMMAND, "index or requestState");
		}
	}

	@Override
	public void allStatusQuery()
	{
		// TODO Auto-generated method stub

	}

}
