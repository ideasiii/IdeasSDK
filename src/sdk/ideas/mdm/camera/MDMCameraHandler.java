package sdk.ideas.mdm.camera;

import sdk.ideas.common.Logs;
import sdk.ideas.mdm.admin.MDMDeviceAdmin.PolicyData;

public class MDMCameraHandler
{
	private ControlCamera camera = null;
	
	public MDMCameraHandler (PolicyData data)
	{
		camera = new ControlCamera(data);
		
	}

	public void setCameraDisable(boolean disable)
	{
		if (null != camera)
		{
			camera.setCamera(disable);
		}
		else
		{
			Logs.showTrace("not new MDMCameraHandler");
		}
	}
	public boolean getCameraStatus()
	{
		if(null!=camera)
		{
			return camera.getCameraStatus();
		}
		return false;
	}
	
	

}
