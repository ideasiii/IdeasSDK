package sdk.ideas.mdm.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.applist.ApplicationList;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class UninstallApp
{
/*
	public static boolean unInstallApplication(Context context, String packageName)
	{
		if (null != packageName && ApplicationList.checkPackageExist(context, packageName) == true)
		{
			String oriPackageName = new String(packageName);
			
			if (!packageName.contains("package:"))
			{
				packageName = "package:" + packageName;
			}
			Uri packageURI = Uri.parse(packageName.toString());
			
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
			
			uninstallIntent.putExtra("packageName", oriPackageName);
			uninstallIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
			uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
			
			((Activity) context).startActivityForResult(uninstallIntent, MDMType.REQUEST_CODE_UNINSTALL_APP);
			
			return true;
		}
		return false;
	}
	*/
	public static boolean unInstallApplication(Context context, String packageName, int appID)
	{
		if (null != packageName && ApplicationList.checkPackageExist(context, packageName) == true)
		{
			String oriPackageName = new String(packageName);
			
			if (!packageName.contains("package:"))
			{
				packageName = "package:" + packageName;
			}
			Uri packageURI = Uri.parse(packageName.toString());
			
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
			
			uninstallIntent.putExtra("packageName", oriPackageName);
			uninstallIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
			uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
			
			Logs.showTrace("uninstall appID: "+ String.valueOf(appID));
			
			((Activity) context).startActivityForResult(uninstallIntent, appID);
			
			return true;
		}
		return false;
	}

}
