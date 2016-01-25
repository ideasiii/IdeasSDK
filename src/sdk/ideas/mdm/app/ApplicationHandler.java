
package sdk.ideas.mdm.app;

import java.io.IOException;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.mdm.MDMType;
import sdk.ideas.mdm.app.InstallApp.InstallAppRunnable;

public class ApplicationHandler
{
	private Context mContext = null;
	private PackageReceiver receiver = null;
	public ApplicationHandler(Context context)
	{
		mContext = context;
		
	}

	/**
	 * use thread to intstall
	 * */
	public void installApplicationThread(String url, String apkName)
	{
		Thread install = new Thread(new InstallAppRunnable(mContext, url, MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, apkName));
		install.start();

	}
	
	
	/**
	 * Installs an application to the device
	 * this method need use Thread run or will cause block main thread error 
	 * 
	 * @param url
	 */
	public void installApplication(String url, String apkName)
	{

		InstallApp.installApplication(mContext, url, MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, apkName);

	}

	/**
	 * Uninstalls an application from the device
	 * 
	 * @param url
	 *            - Application package name should be passed in as a String
	 */
	public void unInstallApplication(String packageName)// Specific package Name
	{

		UninstallApp.unInstallApplication(mContext, packageName);
	}
	
	public void listenAppActionInit()
	{
		IntentFilter filter = new IntentFilter();

		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);

		filter.addDataScheme("package");
		receiver = new PackageReceiver();
		mContext.registerReceiver(receiver, filter);
	}
	public PackageReceiver getPackageReceiver()
	{
		return this.receiver;
	} 

	public void stopListenAppAction()
	{
		if (null != receiver)
			mContext.unregisterReceiver(receiver);
	}

	private void test()
	{
		ArrayList<String> tmp = IOFileHandler.readFromInternalFile(mContext, MDMType.INIT_LOCAL_MDM_APP_PATH);
		try
		{
			IOFileHandler.writeToExternalFile(null, "app_init.txt", tmp);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try
		{
			IOFileHandler.writeToExternalFile(null, "sdcard_file_path_record.txt",
					IOFileHandler.readFromInternalFile(mContext, MDMType.INIT_LOCAL_MDM_SDCARD_PATH));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
