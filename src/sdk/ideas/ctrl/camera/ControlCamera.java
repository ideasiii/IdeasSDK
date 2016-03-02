package sdk.ideas.ctrl.camera;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Build;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ResponseCode.ResponseMessage;
import sdk.ideas.ctrl.admin.DeviceAdminHandler.PolicyData;

@SuppressLint("NewApi")
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
		HashMap<String,String> message = new HashMap<String,String>();
		if (isAdminActive())
		{
			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			{
				devicePolicyManager.setCameraDisabled(cameraAdmin, isDisable);
				mResponseMessage.mnCode = 1;
				message.put("message", "Success");
			}
			else
			{
				mResponseMessage.mnCode = ResponseCode.ERR_OS_BUILD_VERSION_SDK_INT;
				message.put("message", "fail to set camera cause smart phone build version lower than ICE_CREAM_SANDWICH");
			}
		}
		else
		{
			mResponseMessage.mnCode = ResponseCode.ERR_ADMIN_POLICY_INACTIVE;
			message.put("message", "fail to set camera cause admin inactive policy");
		}
		mResponseMessage.mStrContent = new HashMap<String,String>(message);
	
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
