package sdk.ideas.mdm.app;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.MDMType;
import sdk.ideas.mdm.app.ApplicationHandler.ReturnApplicationAction;

public class InstallApp
{

	public static void installApplication(Context mContext, String uRLPath, String savePath, String fileName,
			ArrayList<String> installpackage) throws MalformedURLException, ProtocolException, IOException
	{

		IOFileHandler.urlDownloader(uRLPath, savePath, fileName);

		installpackage.add(InstallApp.getPackageName(mContext, savePath, fileName));
		
		//for test
		for(int i=0;i<installpackage.size();i++)
			Logs.showTrace(installpackage.get(i));
		
		
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(
				Uri.fromFile(new File(IOFileHandler.getExternalStorageDirectory() + "/" + savePath + fileName)),
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag
														// android returned
														// a intent error!
		((Activity) mContext).startActivityForResult(intent, MDMType.REQUEST_CODE_INSTALL_APP);
	}

	public static String getPackageName(Context mContext, String savePath, String fileName)
	{
		String apkPath = IOFileHandler.getExternalStorageDirectory() + "/" + savePath + fileName;
		PackageManager pm = mContext.getPackageManager();

		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		return info.packageName;
	}

	public static class InstallAppRunnable implements Runnable
	{
		private String uRLPath = null;
		private String savePath = null;
		private String fileName = null;
		private Context mContext = null;
		private ArrayList<String> installPackage = null;
		private ReturnApplicationAction listener = null;

		@Override
		public void run()
		{
			try
			{
				IOFileHandler.urlDownloader(uRLPath, savePath, fileName);

				installPackage.add(InstallApp.getPackageName(mContext, savePath, fileName));

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(
						Uri.fromFile(new File(IOFileHandler.getExternalStorageDirectory() + "/" + savePath + fileName)),
						"application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this
																// flag
																// android
																// returned
																// a intent
																// error!
				((Activity) mContext).startActivityForResult(intent, MDMType.REQUEST_CODE_INSTALL_APP);
			}
			catch (MalformedURLException e)
			{
				if (null != listener)
					listener.returnApplicationDownloadResult(e.toString());
			}
			catch (ProtocolException e)
			{
				if (null != listener)
					listener.returnApplicationDownloadResult(e.toString());
			}
			catch (IOException e)
			{
				if (null != listener)
					listener.returnApplicationDownloadResult(e.toString());
			}

		}

		public InstallAppRunnable(Context mContext, String uRLPath, String savePath, String fileName,
				ReturnApplicationAction listener, ArrayList<String> installPackage)
		{
			this.fileName = fileName;
			this.mContext = mContext;
			this.savePath = savePath;
			this.uRLPath = uRLPath;
			this.installPackage = installPackage;
			this.listener = listener;
		}

	}

}
