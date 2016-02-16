package sdk.ideas.mdm.app;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.mdm.app.ApplicationHandler.AppData;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class InstallApp
{

	public static void installApplication(Context mContext, String dataSavePath, int appID)
	{
		Intent installIntent = new Intent(Intent.ACTION_VIEW);

		installIntent.setDataAndType(Uri.fromFile(new File(dataSavePath)), "application/vnd.android.package-archive");

		installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
		installIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
		installIntent.putExtra("packageName", dataSavePath);
		installIntent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME,
				((Activity) mContext).getApplicationInfo().packageName);
		((Activity) mContext).startActivityForResult(installIntent, appID);

	}

	public static void installApplicationWithDownload(Context mContext, String uRLPath, String savePath,
			String fileName, ArrayList<AppData> installpackage, int appID)
					throws MalformedURLException, ProtocolException, IOException
	{

		IOFileHandler.urlDownloader(uRLPath, savePath, fileName);

		// to get install package name, file name is not really package name
		String installPackageName = InstallApp.getPackageName(mContext, savePath, fileName);
		installpackage.add(new AppData(installPackageName,
				IOFileHandler.getExternalStorageDirectory() + "/" + savePath + fileName, appID));

		// for test
		// for (int i = 0; i < installpackage.size(); i++)
		// Logs.showTrace(installpackage.get(i));

		InstallApp.installApplication(mContext, IOFileHandler.getExternalStorageDirectory() + "/" + savePath + fileName,
				appID);

	}

	public static String getPackageName(Context mContext, String savePath, String fileName)
	{
		String apkPath = IOFileHandler.getExternalStorageDirectory() + "/" + savePath + fileName;
		PackageManager pm = mContext.getPackageManager();

		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		return info.packageName;
	}

}
