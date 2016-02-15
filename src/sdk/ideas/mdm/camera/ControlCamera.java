package sdk.ideas.mdm.camera;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Build;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ResponseCode.ResponseMessage;
import sdk.ideas.mdm.admin.MDMDeviceAdmin.PolicyData;

public class ControlCamera
{
	public  DevicePolicyManager devicePolicyManager = null;
	private ComponentName cameraAdmin = null;

	public ControlCamera(PolicyData data)
	{
		if (null != data)
		{
			devicePolicyManager = data.getManager();
			cameraAdmin = data.getComponentName();
		}
	}
	public void setCamera(boolean isDisable , ResponseMessage mResponseMessage)
	{
		if (isAdminActive())
		{
			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			{
				devicePolicyManager.setCameraDisabled(cameraAdmin, isDisable);
				mResponseMessage.mnCode = 1;
				mResponseMessage.mStrContent = "Success";
			}
			else
			{
				mResponseMessage.mnCode = ResponseCode.ERR_OS_BUILD_VERSION_SDK_INT;
				mResponseMessage.mStrContent = "fail to set camera cause smart phone build version lower than ICE_CREAM_SANDWICH";
			}
		}
		else
		{
			mResponseMessage.mnCode = ResponseCode.ERR_ADMIN_POLICY_INACTIVE;
			mResponseMessage.mStrContent = "fail to set camera cause admin inactive policy";
			Logs.showTrace("fail to set camera");
		}
	
	}
	
	private boolean isAdminActive()
	{
		if (null != devicePolicyManager && null != cameraAdmin)
		{
			return devicePolicyManager.isAdminActive(cameraAdmin);
		}
		return false;
	}
	
	
	public boolean getCameraStatus()
	{
		if(isAdminActive())
		{
			return devicePolicyManager.getCameraDisabled(cameraAdmin);
		}
		return false;
	}

	
}
