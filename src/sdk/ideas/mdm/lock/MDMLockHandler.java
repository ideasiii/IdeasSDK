package sdk.ideas.mdm.lock;

import android.content.Context;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.admin.MDMDeviceAdmin.PolicyData;

public class MDMLockHandler
{
	private ControlLockScreen locker = null;
	private ControlLockScreenPassword passwordLocker = null;

	private boolean mLockByPassword = false;
	private ControlLockStatusBar mControlLockStatusBar = null;

	private int mPasswordQuality;
	private int mPasswordLength;
	private int mPasswordMinUpperCase;
	private boolean useDefaultQuality = true;
	private Context mContext = null;

	public MDMLockHandler(PolicyData data, boolean lockByPassword)
	{
		
		mLockByPassword = lockByPassword;
		mContext = data.getContext();
		
		mControlLockStatusBar = new ControlLockStatusBar(mContext);
		if (mLockByPassword == false)
		{
			locker = new ControlLockScreen(data);
		}
		else
		{
			passwordLocker = new ControlLockScreenPassword(data);
		}
	}

	public void lockStatusBar()
	{
		if (null != mContext)
		{
			if (null != mControlLockStatusBar)
				mControlLockStatusBar.preventStatusBarExpansion();

		}
	}

	public void unLockStatusBar()
	{
		if (null != mControlLockStatusBar)
			mControlLockStatusBar.openStatusBarExpansion();
	}

	public void lockFullSceen()
	{
		if (null != mContext)
			ControlLockStatusBar.fullSceen(mContext);
	}

	public boolean lockSceenNow()
	{
		locker.lockNow();
		if (mLockByPassword == false)
		{
			if (null != locker)
			{
				return locker.lockNow();
			}
			else
			{
				Logs.showTrace("not new MDMLockHandler");
				return false;
			}
		}
		else
		{
			Logs.showTrace("please set password");
			return false;
		}
	}

	public void lockSceenNow(String password)
	{
		if (null != passwordLocker)
		{
			if (useDefaultQuality == true)
			{
				// passwordLocker.setLockPasswordPolicyConfigure(PasswordQualityValues.PASSWORD_QUALITY_NUMERIC,
				// 5, 0);
			}
			if(null == password)
				passwordLocker.resetPassword("");
			else
				passwordLocker.resetPassword(password);
			passwordLocker.lockNow();
		}
	}

	public void setLockPasswordPolicyConfigure(int passwordQuality, int passwordLength, int passwordMinUpperCase)
	{
		if (mLockByPassword == true)
		{
			useDefaultQuality = false;
			passwordLocker.setLockPasswordPolicyConfigure(passwordQuality, passwordLength, passwordMinUpperCase);

		}
	}

}
