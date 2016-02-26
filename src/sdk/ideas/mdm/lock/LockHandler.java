package sdk.ideas.mdm.lock;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.admin.MDMDeviceAdmin.PolicyData;

public class LockHandler extends BaseHandler
{
	private int mPasswordQuality;
	private int mPasswordLength;
	private int mPasswordMinUpperCase;
	private boolean useDefaultQuality = true;

	public LockHandler(PolicyData data)
	{
		
		super(data.getContext());
		
		ControlLockStatusBar.controlLockStatusBarInit(mContext);

		ControlLockScreenPassword.setControlLockScreenPassword(data);
		
	}

	public void lockStatusBar()
	{
		ControlLockStatusBar.preventStatusBarExpansion();
	}

	public void unLockStatusBar()
	{
		ControlLockStatusBar.unLockStatusBarExpansion();
	}

	public void lockFullSceen()
	{
		if (null != mContext)
		{
			ControlLockStatusBar.fullSceen(mContext);
		}
	}


	public void lockSceenNow(String password)
	{
		
		if (useDefaultQuality == true)
		{
			// passwordLocker.setLockPasswordPolicyConfigure(PasswordQualityValues.PASSWORD_QUALITY_NUMERIC,
			// 5, 0);
		}

		Logs.showTrace(String.valueOf(ControlLockScreenPassword.resetPassword(password)));
		ControlLockScreenPassword.lockNow();
	}

	public void setLockPasswordPolicyConfigure(int passwordQuality, int passwordLength, int passwordMinUpperCase)
	{
		
		useDefaultQuality = false;
		ControlLockScreenPassword.setLockPasswordPolicyConfigure(passwordQuality, passwordLength, passwordMinUpperCase);

	}

}
