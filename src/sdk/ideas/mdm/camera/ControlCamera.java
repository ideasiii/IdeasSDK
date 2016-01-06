package sdk.ideas.mdm.camera;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import sdk.ideas.common.Logs;
// import sdk.ideas.common.Logs;
import sdk.ideas.mdm.admin.IdeasSDKDeviceAdminReceiver;

public class ControlCamera
{
	public  DevicePolicyManager devicePolicyManager = null;
	private Context mContext = null;
	private ComponentName cameraAdmin = null;
	private Intent intent = null;
	private boolean mAdminActive = false;

	public ControlCamera(Context context)
	{
		if (null != context)
		{
			this.mContext = context;
			devicePolicyManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
			cameraAdmin = new ComponentName(mContext, IdeasSDKDeviceAdminReceiver.class);

			try
			{
				intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cameraAdmin);
				
				((Activity) mContext).startActivityForResult(intent, 1);
			}
			catch (ActivityNotFoundException e)
			{
				Logs.showTrace(e.getMessage());
			}
			 
		}
	}
	public void setCamera(boolean isDisable)
	{
		if (null != devicePolicyManager && null!= cameraAdmin)
		{
			mAdminActive = devicePolicyManager.isAdminActive(cameraAdmin);
			
			if (mAdminActive)
			{
				if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				{
					devicePolicyManager.setCameraDisabled(cameraAdmin, isDisable);
				}
				else
				{
					Logs.showTrace("fail to set camera cause smart phone build version lower than 14");
				}
			}
			else
			{
				Logs.showTrace("fail to set camera");
			}
		}
		else
		{
			Logs.showTrace("fail to set camera cause no new");
		}
	}

	
}
