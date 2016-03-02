package sdk.ideas.ctrl.camera;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.ctrl.admin.DeviceAdminHandler.PolicyData;

public class CameraHandler extends BaseHandler
{
	private ControlCamera camera = null;

	public CameraHandler(PolicyData data)
	{
		super(data.getContext());
		camera = new ControlCamera(data);
	}

	public void setCameraDisable(boolean disable)
	{
		camera.setCamera(disable, mResponseMessage);
		if (disable == true)
		{
			returnRespose(CtrlType.MSG_RESPONSE_CAMERA_HANDLER,
					ResponseCode.METHOLD_CAMERA_DISABLE);
		}
		else
		{
			returnRespose(CtrlType.MSG_RESPONSE_CAMERA_HANDLER,
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
