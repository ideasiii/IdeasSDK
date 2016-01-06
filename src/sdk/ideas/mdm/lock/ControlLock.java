package sdk.ideas.mdm.lock;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.admin.IdeasSDKDeviceAdminReceiver;

public class ControlLock
{
	public  DevicePolicyManager devicePolicyManager = null;
	private Context mContext = null;
	private ComponentName lockAdmin = null;
	private Intent intent = null;

	public ControlLock(Context context)
	{
		if(null!=context)
		{
			mContext = context;
			devicePolicyManager = (DevicePolicyManager)mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
			lockAdmin = new ComponentName(mContext, IdeasSDKDeviceAdminReceiver.class);

			try
			{
				intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, lockAdmin);

				((Activity) mContext).startActivityForResult(intent, 1);
			}
			catch (ActivityNotFoundException e)
			{
				Logs.showTrace(e.getMessage());
			}
		}
		
		
	}
	
	public boolean lockNow()
	{
		if (null != devicePolicyManager && null != lockAdmin)
		{
			if (devicePolicyManager.isAdminActive(lockAdmin))
			{
				devicePolicyManager.lockNow();
				return true;
			}
			else
			{
				Logs.showTrace("fail to lock sceen");
				return false;
			}
		}
		else
		{
			Logs.showTrace("fail to lock sceen cause no new");
			return false;
		}
		
	}
	
	
	

}
