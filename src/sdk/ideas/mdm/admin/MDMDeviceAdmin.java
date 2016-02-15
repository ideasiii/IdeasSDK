package sdk.ideas.mdm.admin;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.MDMType;

public class MDMDeviceAdmin
{
	private static DevicePolicyManager mDevicePolicyManager = null;
	private static ComponentName mComponentAdmin = null;
	private Context mContext = null;
	private Intent intent = null;
	private static PolicyData data = null;

	public MDMDeviceAdmin(Context context)
	{
		mContext = context;

		mDevicePolicyManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mComponentAdmin = new ComponentName(mContext, IdeasSDKDeviceAdminReceiver.class);
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
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, addAdminExtraAppText);

				((Activity) mContext).startActivityForResult(intent, MDMType.REQUEST_CODE_ENABLE_ADMIN);
			}
			catch (ActivityNotFoundException e)
			{
				Logs.showTrace(e.getMessage());
			}
			data = new PolicyData(mContext, mDevicePolicyManager, mComponentAdmin);
		}
	}

	public PolicyData getPolicyData()
	{
		return data;
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
		
		mDevicePolicyManager.removeActiveAdmin(mComponentAdmin);
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
