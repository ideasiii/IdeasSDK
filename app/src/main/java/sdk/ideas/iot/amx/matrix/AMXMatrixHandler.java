package sdk.ideas.iot.amx.matrix;

import java.util.HashMap;
import org.json.JSONException;
import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.MatrixBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;

public class AMXMatrixHandler extends AMXBaseHandler implements MatrixBehavior, StatusQueryBehavior
{
	@Override
	protected void handleControlMessage(Message msg)
	{
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_MATRIX_HANDLER, msg);
	}

	@Override
	protected void handleStatusMessage(Message msg)
	{
		if (msg.what == CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER)
		{
			super.handleStatusResponseMessage(CtrlType.MSG_RESPONSE_AMX_MATRIX_HANDLER,
					ResponseCode.METHOD_AMX_STATUS_COMMAND, msg);
		}
		else if (msg.what == CtrlType.MSG_RESPONSE_AMXBROADCAST_TRANSMIT_HANDLER)
		{
			if (msg.arg1 == ResponseCode.ERR_SUCCESS)
			{
				try
				{
					if (isFunctionIDSame(((HashMap<String, String>) msg.obj).get("message"),
							AMXParameterSetting.FUNCTION_MATRIX_SWITCH))
					{
						super.handleStatusResponseMessage(CtrlType.MSG_RESPONSE_AMX_MATRIX_HANDLER,
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
				Logs.showTrace("[AMXMatrixHandler] ERROR while AMXBROADCAST, message: " + msg.obj);
			}
		}

	}

	public AMXMatrixHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort, String.valueOf(AMXParameterSetting.FUNCTION_MATRIX_SWITCH));
	}

	@SuppressWarnings("static-access")
	@Override
	public void changeMatrixBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.CONTROL_MATRIX_INPUT_1,
				AMXParameterSetting.CONTROL_MATRIX_INPUT_8))
		{
			super.mAMXDataTransmitHandler.sendControlCommand(super.trasferToJsonCommand(
					AMXParameterSetting.TYPE_CONTROL_COMMAND, AMXParameterSetting.FUNCTION_MATRIX_SWITCH, 0, index));
		}
		else
		{
			// callback ERROR: invalid value
			sendIllegalArgumentResponse(CtrlType.MSG_RESPONSE_AMX_MATRIX_HANDLER,
					ResponseCode.METHOD_AMX_COTROL_COMMAND, "index");

		}
	}

	@Override
	public void statusQuery(int index, int requestState)
	{

		super.mAMXDataTransmitHandler
				.sendStatusCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_STATUS_COMMAND,
						AMXParameterSetting.FUNCTION_MATRIX_SWITCH, 0, AMXParameterSetting.REQUEST_STATUS_MATRIX));

	}

	@Override
	public void allStatusQuery()
	{

	}

}
