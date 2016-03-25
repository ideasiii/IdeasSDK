package sdk.ideas.ctrl.admin;

import java.util.HashMap;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;

public class DeviceAdminHandler extends BaseHandler
{
	private static DevicePolicyManager mDevicePolicyManager = null;
	private static ComponentName mComponentAdmin = null;
	private Intent intent = null;
	private static PolicyData policyData = null;
	private HashMap<String, String> message = null;

	public DeviceAdminHandler(Context context)
	{
		super(context);
		mDevicePolicyManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mComponentAdmin = new ComponentName(mContext, DeviceAdministratorReceiver.class);
		message = new HashMap<String, String>();
		policyData = new PolicyData(mContext, mDevicePolicyManager, mComponentAdmin);
	}

	public void createPolicy(String addAdminExtraAppText)
	{
		if (null != mDevicePolicyManager && null != mComponentAdmin && null != mContext)
		{
			try
			{
				intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentAdmin);

				if (null != addAdminExtraAppText)
				{
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, addAdminExtraAppText);
				}
				((Activity) mContext).startActivityForResult(intent, CtrlType.REQUEST_CODE_ENABLE_ADMIN);

			}
			catch (Exception e)
			{
				message.put("message", e.toString());
				super.callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_DEVICE_ADMIN_HANDLER,
						ResponseCode.METHOD_ADMIN_CREATE_POLICY, message);
				message.clear();
			}

		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// callback to developer
		if (resultCode == Activity.RESULT_OK)
		{
			message.put("message", "success");
			super.callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_DEVICE_ADMIN_HANDLER,
					ResponseCode.METHOD_ADMIN_CREATE_POLICY, message);
		}
		else
		{
			message.put("message", "cancelled by user");
			super.callBackMessage(ResponseCode.ERR_ADMIN_POLICY_INACTIVE, CtrlType.MSG_RESPONSE_DEVICE_ADMIN_HANDLER,
					ResponseCode.METHOD_ADMIN_CREATE_POLICY, message);
		}
		message.clear();

	}

	public PolicyData getPolicyData()
	{
		return policyData;
	}

	public boolean isActive()
	{
		if (null != mDevicePolicyManager && null != mComponentAdmin)
		{
			return mDevicePolicyManager.isAdminActive(mComponentAdmin);
		}
		return false;
	}

	public void removePolicy()
	{
		try
		{
			if (isActive() == true)
			{
				mDevicePolicyManager.removeActiveAdmin(mComponentAdmin);

				message.put("message", "success");
				super.callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_DEVICE_ADMIN_HANDLER,
						ResponseCode.METHOD_ADMIN_REMOVE_POLICY, message);
			}
			else
			{
				//
			}
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_DEVICE_ADMIN_HANDLER,
					ResponseCode.METHOD_ADMIN_REMOVE_POLICY, message);
		}
		finally
		{
			message.clear();
		}
	}

	public class PolicyData
	{
		private DevicePolicyManager mDevicePolicyManager = null;
		private ComponentName mComponentAdmin = null;
		private Context mContext = null;

		public PolicyData(Context context, DevicePolicyManager devicePolicyManager, ComponentName componentAdmin)
		{
			mDevicePolicyManager = devicePolicyManager;
			mContext = context;
			mComponentAdmin = componentAdmin;

		}

		public DevicePolicyManager getManager()
		{
			return mDevicePolicyManager;
		}

		public ComponentName getComponentName()
		{
			return mComponentAdmin;
		}

		public Context getContext()
		{
			return mContext;
		}
	}

}
