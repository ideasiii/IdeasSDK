package sdk.ideas.mdm.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import sdk.ideas.mdm.MDMType;
import sdk.ideas.mdm.applist.ApplicationList;

public class UninstallApp
{

	public static boolean unInstallApplication(Context context, String packageName)
	{
		if (null != packageName && ApplicationList.checkPackageExist(context, packageName) == true)
		{
			if (!packageName.contains("package:"))
			{
				packageName = "package:" + packageName;
			}
			Uri packageURI = Uri.parse(packageName.toString());
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
			uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			((Activity) context).startActivityForResult(uninstallIntent, MDMType.REQUEST_CODE_UNINSTALL_APP);
			return true;
		}
		return false;
	}

}
