package sdk.ideas.iot.amx.player;

import java.util.HashMap;
import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.PlayerBehavior;
import sdk.ideas.iot.amx.PowerBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;
import sdk.ideas.module.Controller;

public class AMXBDPlayer extends AMXBaseHandler implements StatusQueryBehavior, PowerBehavior, PlayerBehavior
{

	@Override
	public void handleControlMessage(Message msg)
	{
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_BDPLAYER_HANDLER, msg, null);
	}

	@Override
	public void handleStatusMessage(Message msg)
	{

	}

	public AMXBDPlayer(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);
	}

	@Override
	public void BDPlayerBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.CONTROL_BD_OPEN, AMXParameterSetting.CONTROL_BD_SUBTITLE))
		{
			super.mAMXDataTransmitHandler.sendControlCommand(super.trasferToJsonCommand(
					AMXParameterSetting.TYPE_CONTROL_COMMAND, AMXParameterSetting.FUCTION_BD_PLAYER, 0, index));
		}
		else
		{
			// callback ERROR: invalid value

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
					AMXParameterSetting.TYPE_STATUS_COMMAND, AMXParameterSetting.FUCTION_BD_PLAYER, 0, requestState));
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
