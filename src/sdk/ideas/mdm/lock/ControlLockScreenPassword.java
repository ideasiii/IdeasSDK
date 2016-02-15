package sdk.ideas.mdm.lock;

import com.google.android.gms.fitness.data.DataSet;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.admin.MDMDeviceAdmin.PolicyData;

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

			// to enable
			// _keyguardLock.reenableKeyguard();
		}
		else
		{
			Logs.showTrace("fail to create password lock");
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
	 * @return
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
			if (null == newPassword)
			{
				Logs.showTrace("is null password");
				//Logs.showTrace("min length: " + devicePolicyManager.getPasswordMinimumLength(lockAdmin));
				devicePolicyManager.setPasswordMinimumLength(lockAdmin, "".length());
				//Logs.showTrace("min length2: " + devicePolicyManager.getPasswordMinimumLength(lockAdmin));
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

	public static void lockNow()
	{
		if (isAdminActive())
		{
			devicePolicyManager.lockNow();
		}
	}

}
