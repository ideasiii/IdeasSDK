
package sdk.ideas.mdm.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.mdm.MDMType;
import sdk.ideas.mdm.app.InstallApp.InstallAppRunnable;
import sdk.ideas.mdm.app.PackageReceiver.ReturnPackageAction;

public class ApplicationHandler
{
	private Context mContext = null;
	private PackageReceiver receiver = null;
	private ArrayList<String> installingPackage = null;
	private ArrayList<String> uninstallingPackage = null;
	private ReturnApplicationAction listener = null;
	public ApplicationHandler(Context context)
	{
		mContext = context;
		installingPackage = new ArrayList<String>();
		uninstallingPackage = new ArrayList<String>();
		listenPackageAction();
	}

	/**
	 * use thread to intstall
	 * */
	public void installApplicationThread(String url, String apkName)
	{
		Thread install = new Thread(new InstallAppRunnable(mContext, url, MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, apkName,listener,installingPackage));
		install.start();

	}
	
	
	/**
	 * Installs an application to the device
	 * this method need use Thread run or will cause block main thread error 
	 * 
	 * @param url download  
	 * @param apkName fileName 
	 */
	public void installApplication(String url, String apkName)
	{

		try
		{
			InstallApp.installApplication(mContext, url, MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, apkName, installingPackage);
		}
		catch (MalformedURLException e)
		{
			if(null!= listener)
				listener.returnApplicationDownloadResult(e.toString());
		}
		catch (ProtocolException e)
		{
			if(null!= listener)
				listener.returnApplicationDownloadResult(e.toString());
		}
		catch (IOException e)
		{
			if(null!= listener)
				listener.returnApplicationDownloadResult(e.toString());
		}

	}

	/**
	 * Uninstalls an application from the device
	 * 
	 * @param url
	 *            - Application package name should be passed in as a String
	 */
	public void unInstallApplication(String packageName)// Specific package Name
	{
		uninstallingPackage.add(packageName);
		if(UninstallApp.unInstallApplication(mContext, packageName)==false)
		{
			ArrayListUtility.findContainAndRemove(uninstallingPackage, packageName);
			if(null!= listener)
				listener.returnApplicationActionResult("NOT FIND PACKAGE", packageName, true);
		}
	}
	
	public void listenPackageAction()
	{
		IntentFilter filter = new IntentFilter();

		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);

		filter.addDataScheme("package");
		receiver = new PackageReceiver();
		mContext.registerReceiver(receiver, filter);
		receiver.setOnPackageReceiverListener(new ReturnPackageAction()
		{
			@Override
			public void returnPackageActionResult(String appAction, String packageName)
			{
				if (null != listener)
				{
					if (appAction.equals("android.intent.action.PACKAGE_ADDED"))
					{
						if (ArrayListUtility.findContainAndRemove(installingPackage, packageName))
						{
							listener.returnApplicationActionResult(appAction, packageName, true);
						}
						else
						{
							listener.returnApplicationActionResult(appAction, packageName, false);
						}
					}
					else if (appAction.equals("android.intent.action.PACKAGE_REMOVED"))
					{
						if (ArrayListUtility.findContainAndRemove(uninstallingPackage, packageName))
						{
							listener.returnApplicationActionResult(appAction, packageName, true);
						}
						else
						{
							listener.returnApplicationActionResult(appAction, packageName, false);
						}
					}
				}
			}
		});

	}
	
	public void setOnAppLicationListener(ApplicationHandler.ReturnApplicationAction listener)
	{
		this.listener  =listener;
	}
	
	
	

	public void stopListenAppAction()
	{
		if (null != receiver)
			mContext.unregisterReceiver(receiver);
	}
	
	public static interface ReturnApplicationAction
	{
		void returnApplicationActionResult(String appAction, String packageName, boolean isYourAction);
		void returnApplicationDownloadResult(String message);
	}
	

	
	
	
	/** important test
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * */
	private void test() throws FileNotFoundException, IOException
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
