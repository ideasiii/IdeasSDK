package sdk.ideas.ctrl.lock;

import java.util.HashMap;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.ctrl.admin.DeviceAdminHandler.PolicyData;

public class LockHandler extends BaseHandler
{
	/*
	 * private int mPasswordQuality; private int mPasswordLength; private int mPasswordMinUpperCase;
	 */
	// private boolean useDefaultQuality = true;

	private String					lastPassword	= "";
	private HashMap<String, String>	message			= null;

	public LockHandler(PolicyData data)
	{
		super(data.getContext());
		ControlLockStatusBar.controlLockStatusBarInit(mContext);
		ControlLockScreenPassword.setControlLockScreenPassword(data);
		message = new HashMap<String, String>();
	}

	@SuppressWarnings("unused")
	private void lockStatusBar()
	{
		try
		{
			ControlLockStatusBar.preventStatusBarExpansion();
			message.put("message", "success");
			super.setResponseMessage(ResponseCode.ERR_SUCCESS, message);
			super.returnRespose(CtrlType.MSG_RESPONSE_LOCK_HANDLER, ResponseCode.METHOD_LOCK_STATUS_BAR);
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			super.setResponseMessage(ResponseCode.ERR_UNKNOWN, message);
			super.returnRespose(CtrlType.MSG_RESPONSE_LOCK_HANDLER, ResponseCode.METHOD_LOCK_STATUS_BAR);

		}
		finally
		{
			message.clear();
		}

	}

	@SuppressWarnings("unused")
	private void unLockStatusBar()
	{
		try
		{
			ControlLockStatusBar.unLockStatusBarExpansion();
			message.put("message", "success");
			super.setResponseMessage(ResponseCode.ERR_SUCCESS, message);
			super.returnRespose(CtrlType.MSG_RESPONSE_LOCK_HANDLER, ResponseCode.METHOD_UNLOCK_STATUS_BAR);
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			super.setResponseMessage(ResponseCode.ERR_UNKNOWN, message);
			super.returnRespose(CtrlType.MSG_RESPONSE_LOCK_HANDLER, ResponseCode.METHOD_UNLOCK_STATUS_BAR);
		}
		finally
		{
			message.clear();
		}
	}

	/*
	 * public void lockFullSceen() { if (null != mContext) { ControlLockStatusBar.fullSceen(mContext); } }
	 */

	public void setSceenLockPassword(String password)
	{
		/*
		 * if (useDefaultQuality == true) { // passwordLocker.setLockPasswordPolicyConfigure(PasswordQualityValues.PASSWORD_QUALITY_NUMERIC, // 5, 0); }
		 */

		try
		{
			if (isNumeric(lastPassword) == true)
			{
				if (null == password || "" == password)
				{
					ControlLockScreenPassword.resetPassword("abcdef");
				}
			}

			if (ControlLockScreenPassword.resetPassword(password) == true)
			{
				message.put("message", "success");
				super.setResponseMessage(ResponseCode.ERR_SUCCESS, message);
				super.returnRespose(CtrlType.MSG_RESPONSE_LOCK_HANDLER, ResponseCode.METHOD_RESET_SCREEN_LOCK_PASSWORD);
			}
			else
			{
				message.put("message", "fail cause device admin inactive");
				super.setResponseMessage(ResponseCode.ERR_ADMIN_POLICY_INACTIVE, message);
				super.returnRespose(CtrlType.MSG_RESPONSE_LOCK_HANDLER, ResponseCode.METHOD_RESET_SCREEN_LOCK_PASSWORD);
			}

			this.lastPassword = password;

		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			super.setResponseMessage(ResponseCode.ERR_NO_SPECIFY_USE_POLICY, message);
			super.returnRespose(CtrlType.MSG_RESPONSE_LOCK_HANDLER, ResponseCode.METHOD_RESET_SCREEN_LOCK_PASSWORD);
		}
		finally
		{
			message.clear();
		}
	}

	public void lockSceenNow()
	{
		try
		{
			if (ControlLockScreenPassword.lockNow() == true)
			{
				message.put("message", "success");
				super.setResponseMessage(ResponseCode.ERR_SUCCESS, message);
				super.returnRespose(CtrlType.MSG_RESPONSE_LOCK_HANDLER, ResponseCode.METHOD_LOCK_SCREEN_NOW);
			}
			else
			{
				message.put("message", "fail cause device admin inactive");
				super.setResponseMessage(ResponseCode.ERR_ADMIN_POLICY_INACTIVE, message);
				super.returnRespose(CtrlType.MSG_RESPONSE_LOCK_HANDLER, ResponseCode.METHOD_LOCK_SCREEN_NOW);
			}

		}
		catch (Exception e)
		{
			message.put("message",
					"The calling device admin must have requested DeviceAdminInfo. USES_POLICY_FORCE_LOCK to be able to call this method; if it has not, a security exception will be thrown.");
			super.setResponseMessage(ResponseCode.ERR_UNKNOWN, message);
			super.returnRespose(CtrlType.MSG_RESPONSE_LOCK_HANDLER, ResponseCode.METHOD_LOCK_SCREEN_NOW);
		}
		finally
		{
			message.clear();
		}
	}

	/*
	 * private void setLockPasswordPolicyConfigure(int passwordQuality, int passwordLength, int passwordMinUpperCase) { useDefaultQuality = false;
	 * ControlLockScreenPassword.setLockPasswordPolicyConfigure(passwordQuality, passwordLength, passwordMinUpperCase); }
	 */

	private static boolean isNumeric(String str)
	{
		try
		{
			Double.parseDouble(str);
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

}
