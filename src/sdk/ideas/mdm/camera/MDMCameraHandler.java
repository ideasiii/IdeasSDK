package sdk.ideas.mdm.camera;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.mdm.MDMType;
import sdk.ideas.mdm.admin.MDMDeviceAdmin.PolicyData;

public class MDMCameraHandler extends BaseHandler
{
	private ControlCamera camera = null;

	public MDMCameraHandler(PolicyData data)
	{
		super(data.getContext());
		camera = new ControlCamera(data);
	}

	public void setCameraDisable(boolean disable)
	{
		camera.setCamera(disable, mResponseMessage);
		if (disable == true)
		{
			returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_CAMERA_HANDLER,
					ResponseCode.METHOLD_CAMERA_DISABLE);
		}
		else
		{
			returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_CAMERA_HANDLER,
					ResponseCode.METHOLD_CAMERA_ENABLE);
		}
	}

	public boolean getCameraStatus()
	{
		if (null != camera)
		{
			return camera.getCameraStatus();
		}
		return false;
	}

}
