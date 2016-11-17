package sdk.ideas.iot.amx.project;

import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
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
	public void handleControlMessage(Message msg)
	{
		
		super.handleControlResponseMessage(CtrlType.MSG_RESPONSE_AMX_PROJECT_HANDLER, msg);
	}

	@Override
	public void handleStatusMessage(Message msg)
	{

	}

	public AMXProjectHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);
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
		}

	}

	@Override
	public void statusQuery(int index, int requestState)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_PROJECT_LEFT, AMXParameterSetting.DEVICE_PROJECT_RIGHT)
				&& super.isInInterval(requestState, AMXParameterSetting.REQUEST_STATUS_POWER,
						AMXParameterSetting.REQUEST_STATUS_MUTE))
		{
			super.mAMXDataTransmitHandler.sendStatusCommand(super.trasferToJsonCommand(
					AMXParameterSetting.TYPE_STATUS_COMMAND, AMXParameterSetting.FUNCTION_PROJECT, index, requestState));

		}
		else
		{
			// callback ERROR: invalid value
		}
	}

	@Override
	public void allStatusQuery()
	{
		// TODO Auto-generated method stub
		
	}

}
