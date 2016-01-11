package sdk.ideas.mdm.lock;

import android.app.admin.DevicePolicyManager;

public abstract class PasswordQualityValues
{
	public final static int PASSWORD_QUALITY_UNSPECIFIED = DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED;
	public final static int PASSWORD_QUALITY_SOMETHING = DevicePolicyManager.PASSWORD_QUALITY_SOMETHING;
	public final static int PASSWORD_QUALITY_NUMERIC = DevicePolicyManager.PASSWORD_QUALITY_NUMERIC;
	public final static int PASSWORD_QUALITY_ALPHABETIC = DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC;
	public final static int PASSWORD_QUALITY_ALPHANUMERIC = DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC;
	public final static int PASSWORD_QUALITY_COMPLEX = DevicePolicyManager.PASSWORD_QUALITY_COMPLEX;

}
