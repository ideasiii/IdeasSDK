package sdk.ideas.mdm.lock;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Build;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.admin.MDMDeviceAdmin.PolicyData;

@SuppressLint("NewApi")
public class ControlLockScreenPassword
{

	private DevicePolicyManager devicePolicyManager = null;
	private ComponentName lockAdmin = null;

	public ControlLockScreenPassword(PolicyData data)
	{
		if (null != data)
		{
			devicePolicyManager = data.getManager();
			lockAdmin = data.getComponentName();
		}
		else
		{
			Logs.showTrace("fail to create password lock");
		}
	}

	public void setLockPasswordPolicyConfigure(int passwordQuality, int passwordLength, int passwordMinUpperCase)
	{
		if (isAdminActive())
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
				devicePolicyManager.setPasswordMinimumUpperCase(lockAdmin, passwordMinUpperCase);
				devicePolicyManager.setPasswordQuality(lockAdmin, passwordQuality);
				devicePolicyManager.setPasswordMinimumLength(lockAdmin, passwordLength);
			}
		}
		else
		{

		}

	}

	/**
	 * Indicates whether the device administrator is currently active.
	 *
	 * @return
	 */
	private boolean isAdminActive()
	{
		if (null != devicePolicyManager && null != lockAdmin)
		{
			return devicePolicyManager.isAdminActive(lockAdmin);
		}
		return false;
	}

	private boolean isActivePasswordSufficient()
	{
		if (null != devicePolicyManager && null != lockAdmin)
		{
			return devicePolicyManager.isActivePasswordSufficient();
		}
		return false;
	}

	public boolean isDeviceSecured()
	{
		return (isAdminActive() && isActivePasswordSufficient());
	}

	public boolean resetPassword(String newPassword)
	{
		if (isAdminActive())
		{
			if (null != devicePolicyManager)
			{
				return devicePolicyManager.resetPassword(newPassword, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	public void lockNow()
	{
		if (isAdminActive())
		{
			devicePolicyManager.lockNow();
		}
	}

}
