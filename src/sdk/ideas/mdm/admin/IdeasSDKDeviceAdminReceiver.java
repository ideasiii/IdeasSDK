package sdk.ideas.mdm.admin;


import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import sdk.ideas.common.Logs;


public  class IdeasSDKDeviceAdminReceiver extends DeviceAdminReceiver
{
	

    @Override
    public void onEnabled(Context context, Intent intent) 
    {
    	super.onEnabled(context, intent);
    	Logs.showTrace("admin_receiver_status_enabled");
    }


	/**
	 * Called when this application is no longer the device administrator.
	 */
	@Override
	public void onDisabled(Context context, Intent intent)
	{
		super.onDisabled(context, intent);
		Logs.showTrace("admin_receiver_status_disable");
	}
	
	@Override
	public void onPasswordChanged(Context context, Intent intent)
	{
		super.onPasswordChanged(context, intent);
		Logs.showTrace("onPasswordChanged");
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent)
	{
		super.onPasswordFailed(context, intent);
		Logs.showTrace("onPasswordFailed");
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent)
	{
		super.onPasswordSucceeded(context, intent);
		Logs.showTrace("onPasswordSucceeded");
	}
	
	
	
}
