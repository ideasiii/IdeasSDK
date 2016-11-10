package sdk.ideas.iot.amx.matrix;

import java.util.HashMap;
import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.MatrixBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;
import sdk.ideas.module.Controller;

public class AMXMatrixHandler extends AMXBaseHandler implements MatrixBehavior, StatusQueryBehavior
{
	@Override
	public void handleControlMessage(Message msg)
	{
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_MATRIX_HANDLER, msg, null);
	}

	@Override
	public void handleStatusMessage(Message msg)
	{

	}

	public AMXMatrixHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);
	}

	@Override
	public void changeMatrixBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.CONTROL_MATRIX_INPUT_1,
				AMXParameterSetting.CONTROL_MATRIX_INPUT_8))
		{
			super.mAMXDataTransmitHandler.sendControlCommand(super.trasferToJsonCommand(
					AMXParameterSetting.TYPE_CONTROL_COMMAND, AMXParameterSetting.FUCTION_MATRIX_SWITCH, 0, index));
		}
		else
		{
			// callback ERROR: invalid value

		}
	}

	@Override
	public void statusQuery(int index, int requestState)
	{
		if (isInInterval(requestState, AMXParameterSetting.REQUEST_STATUS_MATRIX,
				AMXParameterSetting.REQUEST_STATUS_MATRIX))
		{
			super.mAMXDataTransmitHandler
					.sendStatusCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_STATUS_COMMAND,
							AMXParameterSetting.FUCTION_MATRIX_SWITCH, 0, requestState));
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
