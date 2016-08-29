package sdk.ideas.ctrl.lock;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Build;
import sdk.ideas.common.Logs;
import sdk.ideas.ctrl.admin.DeviceAdminHandler.PolicyData;

@SuppressLint("NewApi")
public class ControlLockScreenPassword
{

	private static DevicePolicyManager devicePolicyManager = null;
	private static ComponentName lockAdmin = null;
	
	public static void setControlLockScreenPassword(PolicyData data)
	{
		if (null != data)
		{
			devicePolicyManager = data.getManager();
			lockAdmin = data.getComponentName();
		}
	}

	public static void setLockPasswordPolicyConfigure(int passwordQuality, int passwordLength, int passwordMinUpperCase)
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
	 */
	private static boolean isAdminActive()
	{
		if (null != devicePolicyManager && null != lockAdmin)
		{
			return devicePolicyManager.isAdminActive(lockAdmin);
		}
		return false;
	}

	private static boolean isActivePasswordSufficient()
	{
		if (null != devicePolicyManager && null != lockAdmin)
		{
			return devicePolicyManager.isActivePasswordSufficient();
		}
		return false;
	}

	public static boolean isDeviceSecured()
	{
		return (isAdminActive() && isActivePasswordSufficient());
	}

	public static boolean resetPassword(String newPassword)
	{
		if (isAdminActive())
		{
			if (null == newPassword )
			{
				Logs.showTrace("is null password");
				
				devicePolicyManager.setPasswordMinimumLength(lockAdmin, "".length());
				devicePolicyManager.setPasswordQuality(lockAdmin, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);

				return devicePolicyManager.resetPassword("", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
			}
			else if("" == newPassword)
			{
				Logs.showTrace("is empty password");

				devicePolicyManager.setPasswordMinimumLength(lockAdmin, "".length());
				devicePolicyManager.setPasswordQuality(lockAdmin, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);

				return devicePolicyManager.resetPassword("", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
			}
			else
			{
				devicePolicyManager.setPasswordMinimumLength(lockAdmin, newPassword.length());
				return devicePolicyManager.resetPassword(newPassword, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
			}
		}
		else
		{
			return false;
		}
	}

	public static boolean lockNow()
	{
		if (isAdminActive())
		{
			devicePolicyManager.lockNow();
			return true;
		}
		return false;
	}

}
