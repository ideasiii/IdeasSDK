
package sdk.ideas.mdm.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ReturnIntentAction;
import sdk.ideas.mdm.MDMType;

public class ApplicationHandler extends BaseHandler
{
	private Context mContext = null;
	private PackageReceiver receiver = null;
	private ArrayList<AppData> installingPackage = null;
	private ArrayList<AppData> uninstallingPackage = null;

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Logs.showTrace("requestCode: "+String.valueOf(requestCode));
		if (resultCode != Activity.RESULT_OK)
		{
			AppData installAppData = ArrayListUtility.findEqualForAppDataClass(installingPackage, requestCode);
			AppData uninstallAppData = ArrayListUtility.findEqualForAppDataClass(uninstallingPackage, requestCode);
			
			// for install
			if (null == uninstallAppData)
			{
				InstallApp.installApplication(mContext, installAppData.downloadPath, installAppData.appID);
			}
			// for uninstall
			else if(null == installAppData)
			{
				UninstallApp.unInstallApplication(mContext, uninstallAppData.packageName, uninstallAppData.appID);
			}
		}

	}

	public ApplicationHandler(Context context)
	{
		super(context);
		mContext = context;
		installingPackage = new ArrayList<AppData>();
		uninstallingPackage = new ArrayList<AppData>();
		listenPackageAction();
	}

	/**
	 * use thread to install
	 */
	public void installApplicationThread(String url, String apkName, int appID)
	{

		Thread install = new Thread(new InstallAppRunnable(url, apkName, appID));
		install.start();
	}

	/**
	 * Installs an application to the device this method need use Thread run or
	 * will cause block main thread error
	 * 
	 * @param url
	 *            download
	 * @param apkName
	 *            fileName
	 */
	public void installApplication(String url, String apkName, int appID)
	{
		boolean anyError = true;
		try
		{
			InstallApp.installApplicationWithDownload(mContext, url, MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH,
					apkName, installingPackage, appID);
			anyError = false;
		}
		catch (MalformedURLException e)
		{
			setResponseMessage(ResponseCode.ERR_MALFORMED_URL_EXCEPTION, e.toString());
		}
		catch (ProtocolException e)
		{
			setResponseMessage(ResponseCode.ERR_PROTOCOL_EXCEPTION, e.toString());
		}
		catch (IOException e)
		{
			setResponseMessage(ResponseCode.ERR_IO_EXCEPTION, e.toString());
		}
		catch (Exception e)
		{
			setResponseMessage(ResponseCode.ERR_UNKNOWN, e.toString());
		}
		finally
		{
			if (anyError == true)
			{
				returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
						ResponseCode.METHOD_APPLICATION_INSTALL_SYSTEM);
			}
			else
			{

			}
		}

	}

	/**
	 * Uninstall an application from the device
	 * 
	 * @param url
	 *            - Application package name should be passed in as a String
	 */
	public void unInstallApplication(String packageName, int appID)// Specific
																	// package
																	// Name
	{
		uninstallingPackage.add(new AppData(packageName, "",  appID));

		if (UninstallApp.unInstallApplication(mContext, packageName,  appID) == false)
		{
			ArrayListUtility.findEqualAndRemoveForAppDataClass(uninstallingPackage, packageName);

			setResponseMessage(ResponseCode.ERR_PACKAGE_NOT_FIND, "not find the package which need to uninstall");

			returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
					ResponseCode.METHOD_APPLICATION_UNINSTALL_SYSTEM);
		}
		else
		{
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
		receiver.setOnReceiverListener(new ReturnIntentAction()
		{
			@Override
			public void returnIntentAction(HashMap<String, String> action)
			{
				String appAction = action.get("Action");
				String packageName = action.get("PackageName");
				if (null != listener)
				{
					if (appAction.equals("android.intent.action.PACKAGE_ADDED"))
					{
						if (ArrayListUtility.findEqualAndRemoveForAppDataClass(installingPackage, packageName))
						{
							setResponseMessage(ResponseCode.ERR_SUCCESS, packageName);
							returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_INSTALL_SYSTEM);
							// 需寫刪除 安裝成功的 apk 檔案

						}
						else
						{
							setResponseMessage(ResponseCode.ERR_SUCCESS, packageName);
							returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_INSTALL_USER);
						}
					}
					else if (appAction.equals("android.intent.action.PACKAGE_REMOVED"))
					{
						if (ArrayListUtility.findEqualAndRemoveForAppDataClass(uninstallingPackage, packageName))
						{
							setResponseMessage(ResponseCode.ERR_SUCCESS, packageName);
							returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_UNINSTALL_SYSTEM);
						}
						else
						{
							setResponseMessage(ResponseCode.ERR_SUCCESS, packageName);
							returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_UNINSTALL_USER);
						}
					}
				}
			}
		});

	}

	public void stopListenAppAction()
	{
		if (null != receiver)
			mContext.unregisterReceiver(receiver);
	}

	private class InstallAppRunnable implements Runnable
	{
		private String uRLPath = null;
		private String fileName = null;
		private int appID;

		@Override
		public void run()
		{
			installApplication(uRLPath, fileName, appID);
		}

		public InstallAppRunnable(String uRLPath, String fileName, int appID)
		{
			this.fileName = fileName;
			this.uRLPath = uRLPath;
			this.appID = appID;
		}

	}

	private static boolean isAppInstalled(Context mContext, String packageName)
	{
		PackageManager pm = mContext.getPackageManager();
		boolean installed = false;
		try
		{
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			installed = true;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			installed = false;
		}
		return installed;
	}

	/**
	 * important test
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
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

	public static class AppData
	{
		public String downloadPath = "";
		public String packageName = "";
		public int appID;

		public AppData(String packageName, String downloadPath, int appID)
		{
			this.downloadPath = downloadPath;
			this.packageName = packageName;
			this.appID = appID;
		}
	}

}
