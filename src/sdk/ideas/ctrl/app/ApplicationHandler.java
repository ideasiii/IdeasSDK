
package sdk.ideas.ctrl.app;

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
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ReturnIntentAction;

public class ApplicationHandler extends BaseHandler implements ListenReceiverAction
{
	private Context mContext = null;
	private PackageReceiver receiver = null;
	private ArrayList<AppData> installingPackage = null;
	private ArrayList<AppData> uninstallingPackage = null;
	private IntentFilter filter = null;
	private String defaultDownloadApkSavePath = "Download/";
	

	public ApplicationHandler(Context context)
	{
		super(context);
		mContext = context;
		installingPackage = new ArrayList<AppData>();
		uninstallingPackage = new ArrayList<AppData>();
		
		receiver = new PackageReceiver();
		
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addDataScheme("package");
		
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Logs.showTrace("requestCode: " + String.valueOf(requestCode));
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
			else if (null == installAppData)
			{
				UninstallApp.unInstallApplication(mContext, uninstallAppData.packageName, uninstallAppData.appID);
			}
		}

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
	public void installApplication(String url,String savePath, String apkName, int appID)
	{
		if(null ==savePath)
			savePath = this.defaultDownloadApkSavePath;
		else
			this.defaultDownloadApkSavePath = savePath;
		boolean anyError = true;
		HashMap<String, String> message = new HashMap<String, String>();
		try
		{
			InstallApp.installApplicationWithDownload(mContext, url, savePath,
					apkName, installingPackage, appID);
			anyError = false;
		}
		catch (MalformedURLException e)
		{
			message.put("message", e.toString());
			setResponseMessage(ResponseCode.ERR_MALFORMED_URL_EXCEPTION, message);
		}
		catch (ProtocolException e)
		{
			message.put("message", e.toString());
			setResponseMessage(ResponseCode.ERR_PROTOCOL_EXCEPTION, message);
		}
		catch (IOException e)
		{
			message.put("message", e.toString());
			setResponseMessage(ResponseCode.ERR_IO_EXCEPTION, message);
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			setResponseMessage(ResponseCode.ERR_UNKNOWN, message);
		}
		finally
		{
			if (anyError == true)
			{
				returnRespose( CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
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
		HashMap<String, String> message = new HashMap<String, String>();

		uninstallingPackage.add(new AppData(packageName, "","", appID));

		if (UninstallApp.unInstallApplication(mContext, packageName, appID) == false)
		{
			ArrayListUtility.findEqualAndRemoveForAppDataClass(uninstallingPackage, packageName);

			message.put("message", "not find the package which need to uninstall");
			setResponseMessage(ResponseCode.ERR_PACKAGE_NOT_FIND, message);
			returnRespose(CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
					ResponseCode.METHOD_APPLICATION_UNINSTALL_SYSTEM);
		}
		else
		{
		}
	}

	@Override
	public void startListenAction()
	{
		
		
		mContext.registerReceiver(receiver, filter);
		receiver.setOnReceiverListener(new ReturnIntentAction()
		{
			@Override
			public void returnIntentAction(HashMap<String, String> action)
			{
				HashMap<String, String> message = new HashMap<String, String>();

				String appAction = action.get("Action");
				String packageName = action.get("PackageName");
				message.put("packageName", packageName);
				if (null != listener)
				{
					if (appAction.equals("android.intent.action.PACKAGE_ADDED"))
					{
						AppData installed = ArrayListUtility.findEqualForAppDataClass(installingPackage, packageName);
						if (null != installed )
						{
							
							setResponseMessage(ResponseCode.ERR_SUCCESS, message);
							returnRespose(CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_INSTALL_SYSTEM);
							// 需寫刪除 安裝成功的 apk 檔案
							IOFileHandler.deleteFile(IOFileHandler.getExternalStorageDirectory() + "/"
									+ defaultDownloadApkSavePath + installed.fileName);
							
							ArrayListUtility.findEqualAndRemoveForAppDataClass(installingPackage, packageName);

						}
						else
						{
							setResponseMessage(ResponseCode.ERR_SUCCESS, message);
							returnRespose(CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_INSTALL_USER);
						}
					}
					else if (appAction.equals("android.intent.action.PACKAGE_REMOVED"))
					{
						if (ArrayListUtility.findEqualAndRemoveForAppDataClass(uninstallingPackage, packageName))
						{
							setResponseMessage(ResponseCode.ERR_SUCCESS, message);
							returnRespose(CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_UNINSTALL_SYSTEM);
						}
						else
						{
							setResponseMessage(ResponseCode.ERR_SUCCESS, message);
							returnRespose(CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_UNINSTALL_USER);
						}
					}
				}
			}
		});
	}

	@Override
	public void stopListenAction()
	{
		if (null != receiver)
		{
			mContext.unregisterReceiver(receiver);
		}
	}

	private class InstallAppRunnable implements Runnable
	{
		private String uRLPath = null;
		private String fileName = null;
		private int appID;

		@Override
		public void run()
		{
			installApplication(uRLPath, null ,fileName, appID);
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
/*
	/**
	 * important test
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 *
	private void test() throws FileNotFoundException, IOException
	{
		ArrayList<String> tmp = IOFileHandler.readFromInternalFile(mContext, MDMType.INIT_LOCAL_MDM_APP_PATH);
		try
		{
			IOFileHandler.writeToExternalFile(null, "app_init.txt", tmp);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		try
		{
			IOFileHandler.writeToExternalFile(null, "sdcard_file_path_record.txt",
					IOFileHandler.readFromInternalFile(mContext, MDMType.INIT_LOCAL_MDM_SDCARD_PATH));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}*/

	public static class AppData
	{
		public String downloadPath = "";
		public String packageName = "";
		public int appID;
		public String fileName = "";

		public AppData(String packageName, String downloadPath,String fileName, int appID)
		{
			this.downloadPath = downloadPath;
			this.packageName = packageName;
			this.appID = appID;
			this.fileName = fileName;
		}
	}

}
