package sdk.ideas.mdm.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import sdk.ideas.common.Logs;


public class PackageReceiver extends BroadcastReceiver 
{
	private ReturnPackageAction	listener = null;
	public PackageReceiver()
	{
		
	}
	

	@Override
	public void onReceive(Context context, Intent intent)
	{

		String installedPackageName = intent.getData().getEncodedSchemeSpecificPart();
		
		Logs.showTrace(installedPackageName);
		
		if (null != listener)
		{
			listener.returnPackageActionResult(intent.getAction(), installedPackageName);
		}
	}
	public void setOnPackageReceiverListener(PackageReceiver.ReturnPackageAction listener)
	{
		this.listener = listener;
	}
	
	
	
	interface ReturnPackageAction
	{
		void returnPackageActionResult(String appAction,String packageName);
		
	}	
	
}
