package sdk.ideas.ctrl.lock;

import java.util.HashMap;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.ctrl.admin.DeviceAdminHandler.PolicyData;

public class LockHandler extends BaseHandler
{
	/*
	 * private int mPasswordQuality; private int mPasswordLength; private int
	 * mPasswordMinUpperCase;
	 */
	// private boolean useDefaultQuality = true;

	private String lastPassword = "";
	private HashMap<String, String> message = null;

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
			callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_LOCK_HANDLER,
					ResponseCode.METHOD_LOCK_STATUS_BAR, message);
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_LOCK_HANDLER,
					ResponseCode.METHOD_LOCK_STATUS_BAR, message);

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
			callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_LOCK_HANDLER,
					ResponseCode.METHOD_UNLOCK_STATUS_BAR, message);
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_LOCK_HANDLER,
					ResponseCode.METHOD_UNLOCK_STATUS_BAR, message);
		}
		finally
		{
			message.clear();
		}
	}

	/*
	 * public void lockFullSceen() { if (null != mContext) {
	 * ControlLockStatusBar.fullSceen(mContext); } }
	 */

	public void setSceenLockPassword(String password)
	{
		/*
		 * if (useDefaultQuality == true) { //
		 * passwordLocker.setLockPasswordPolicyConfigure(PasswordQualityValues.
		 * PASSWORD_QUALITY_NUMERIC, // 5, 0); }
		 */

		try
		{
			
			if (isNumeric(password) == true)
			{
				// number but not PIN(4 number)
				if (password.length() != 4)
				{
					message.put("message", "PIN　must and only have 4 number");
					callBackMessage(ResponseCode.ERR_PASSWORD_FORMAT_NOT_SUPPORT, CtrlType.MSG_RESPONSE_LOCK_HANDLER,
							ResponseCode.METHOD_RESET_SCREEN_LOCK_PASSWORD, message);
					return;
				}
				
			}
			if (ControlLockScreenPassword.resetPassword(password) == true)
			{
				message.put("message", "success");
				callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_LOCK_HANDLER,
						ResponseCode.METHOD_RESET_SCREEN_LOCK_PASSWORD, message);

			}
			else
			{
				message.put("message", "fail cause device admin inactive");
				callBackMessage(ResponseCode.ERR_ADMIN_POLICY_INACTIVE, CtrlType.MSG_RESPONSE_LOCK_HANDLER,
						ResponseCode.METHOD_RESET_SCREEN_LOCK_PASSWORD, message);

			}

			this.lastPassword = password;

		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_POLICY, CtrlType.MSG_RESPONSE_LOCK_HANDLER,
					ResponseCode.METHOD_RESET_SCREEN_LOCK_PASSWORD, message);

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
				callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_LOCK_HANDLER,
						ResponseCode.METHOD_LOCK_SCREEN_NOW, message);
			}
			else
			{
				message.put("message", "fail cause device admin inactive");
				callBackMessage(ResponseCode.ERR_ADMIN_POLICY_INACTIVE, CtrlType.MSG_RESPONSE_LOCK_HANDLER,
						ResponseCode.METHOD_LOCK_SCREEN_NOW, message);
			}

		}
		catch (Exception e)
		{
			message.put("message",
					"The calling device admin must have requested DeviceAdminInfo. USES_POLICY_FORCE_LOCK to be able to call this method; if it has not, a security exception will be thrown.");
			super.callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_LOCK_HANDLER,
					ResponseCode.METHOD_LOCK_SCREEN_NOW, message);
		}
		finally
		{
			message.clear();
		}
	}

	/*
	 * private void setLockPasswordPolicyConfigure(int passwordQuality, int
	 * passwordLength, int passwordMinUpperCase) { useDefaultQuality = false;
	 * ControlLockScreenPassword.setLockPasswordPolicyConfigure(passwordQuality,
	 * passwordLength, passwordMinUpperCase); }
	 */

	private static boolean isNumeric(String str)
	{
		if(null == str)
			return false;
		if(str.length() == 0)
			return false;
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
