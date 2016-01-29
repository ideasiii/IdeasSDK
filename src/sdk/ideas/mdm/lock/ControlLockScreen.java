package sdk.ideas.mdm.lock;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.admin.MDMDeviceAdmin.PolicyData;

public class ControlLockScreen
{
	private  DevicePolicyManager devicePolicyManager = null;
	private ComponentName lockAdmin = null;

	
	
	public ControlLockScreen(PolicyData data)
	{
		if (null != data)
		{
			devicePolicyManager = data.getManager();
			lockAdmin = data.getComponentName();
		}
		else
		{
			Logs.showTrace("fail to create lock");
		}

	}
	
	public boolean lockNow()
	{
		
		if (isAdminActive())
		{
			devicePolicyManager.lockNow();
			return true;
		}
		else
		{
			Logs.showTrace("fail to lock sceen");
			return false;
		}
	}
	
	private boolean isAdminActive()
	{
		if (null != devicePolicyManager && null != lockAdmin)
		{
			
			return devicePolicyManager.isAdminActive(lockAdmin);
		}
		return false;
	}

}
