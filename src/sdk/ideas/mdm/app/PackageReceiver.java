package sdk.ideas.mdm.app;

import android.content.Context;
import android.content.Intent;
import sdk.ideas.common.BaseReceiver;
import sdk.ideas.common.Logs;


public class PackageReceiver extends BaseReceiver 
{
	public PackageReceiver()
	{
		super();
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{

		String installedPackageName = intent.getData().getEncodedSchemeSpecificPart();
		
		Logs.showTrace(installedPackageName);
		
		if (null != listener)
		{
			actionData.put("Action",intent.getAction());
			actionData.put("PackageName",installedPackageName);
			listener.returnIntentAction(actionData);
		}
	}

	
}
