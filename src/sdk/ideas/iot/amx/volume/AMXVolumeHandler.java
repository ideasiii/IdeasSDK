package sdk.ideas.iot.amx.volume;

import android.content.Context;
import android.os.Message;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.AMXParameterSetting;
import sdk.ideas.iot.amx.LiftingBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;
import sdk.ideas.iot.amx.VolumeBehavior;

public class AMXVolumeHandler extends AMXBaseHandler implements LiftingBehavior, VolumeBehavior, StatusQueryBehavior
{
	@Override
	public void handleControlMessage(Message msg)
	{

	}

	@Override
	public void handleStatusMessage(Message msg)
	{

	}

	public AMXVolumeHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);

	}

	@Override
	public void upBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_VOLUME_INPUT_1,
				AMXParameterSetting.DEVICE_VOLUME_OUTPUT_6))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUCTION_VOLUME, index, AMXParameterSetting.CONTROL_UP));

		}
		else
		{
			// callback ERROR: invalid value
		}
	}

	@Override
	public void downBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_VOLUME_INPUT_1,
				AMXParameterSetting.DEVICE_VOLUME_OUTPUT_6))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUCTION_VOLUME, index, AMXParameterSetting.CONTROL_DOWN));
		}
		else
		{
			// callback ERROR: invalid value
		}
	}

	@Override
	public void muteBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_VOLUME_INPUT_1,
				AMXParameterSetting.DEVICE_VOLUME_OUTPUT_6))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUCTION_VOLUME, index, AMXParameterSetting.CONTROL_MUTE));
		}
		else
		{
			// callback ERROR: invalid value
		}
	}

	@Override
	public void unMuteBehavior(int index)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_VOLUME_INPUT_1,
				AMXParameterSetting.DEVICE_VOLUME_OUTPUT_6))
		{
			super.mAMXDataTransmitHandler
					.sendControlCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_CONTROL_COMMAND,
							AMXParameterSetting.FUCTION_VOLUME, index, AMXParameterSetting.CONTROL_UNMUTE));
		}
		else
		{
			// callback ERROR: invalid value
		}
	}

	@Override
	public void statusQuery(int index, int requestState)
	{
		if (super.isInInterval(index, AMXParameterSetting.DEVICE_VOLUME_INPUT_1,
				AMXParameterSetting.DEVICE_VOLUME_OUTPUT_6)
				&& super.isInInterval(requestState, AMXParameterSetting.REQUEST_STATUS_MUTE,
						AMXParameterSetting.REQUEST_STATUS_MUTE))
		{
			super.mAMXDataTransmitHandler
					.sendStatusCommand(super.trasferToJsonCommand(AMXParameterSetting.TYPE_STATUS_COMMAND,
							AMXParameterSetting.FUCTION_VOLUME, index, AMXParameterSetting.REQUEST_STATUS_MUTE));
		}
		else
		{
			// callback ERROR: invalid value

		}

	}

}
