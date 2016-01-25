package sdk.ideas.mdm.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import sdk.ideas.common.Logs;
import sdk.ideas.tracker.Tracker;
import sdk.ideas.tracker.Tracker.TransferMessage;

public class PackageReceiver extends BroadcastReceiver 
{
	private boolean isMDMInstall = false;
	private ReturnAppAction	listener = null;
	public PackageReceiver()
	{
		
	}
	public PackageReceiver(boolean isMDMInstall)
	{
		this.isMDMInstall = isMDMInstall;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{

		Logs.showTrace(" test for application install/uninstall");
		String installedPackageName = intent.getData().getEncodedSchemeSpecificPart();
		Logs.showTrace(installedPackageName);
	
		listener.returnAppActionResult(intent.getAction(),installedPackageName);
	}
	
	public void setOnPackageReceiverListener(PackageReceiver.ReturnAppAction listener)
	{
		this.listener = listener;
	}
	
	
	
	public interface ReturnAppAction
	{
		void returnAppActionResult(String appAction,String packageName);
		
	}	
	
}
