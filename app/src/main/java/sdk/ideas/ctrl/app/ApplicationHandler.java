
package sdk.ideas.ctrl.app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.NetworkOnMainThreadException;
import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CommonClass.AppData;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ReturnIntentAction;

@SuppressLint("NewApi")
public class ApplicationHandler extends BaseHandler implements ListenReceiverAction
{
	private Context mContext = null;
	private PackageReceiver receiver = null;
	private ArrayList<AppData> installingPackage = null;
	private ArrayList<AppData> uninstallingPackage = null;
	private IntentFilter filter = null;
	private String defaultDownloadApkSavePath = "Download/";
	private boolean isRegisterReceiver = false;

	
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

	public void downloadApplicationFile(String uRLPath, String savePath, String fileName, int appID)
	{
		if (null == savePath)
		{
			savePath = this.defaultDownloadApkSavePath;
		}
		else
		{
			this.defaultDownloadApkSavePath = savePath;
		}
		Thread t = new Thread(new DownloadRunnable(uRLPath, savePath, fileName, appID));
		t.start();
	}

	public void installApplication(String savePath, String fileName, int appID)
	{
		if (null == savePath)
		{
			savePath = this.defaultDownloadApkSavePath;
		}
		else
		{
			this.defaultDownloadApkSavePath = savePath;
		}
		HashMap<String, String> message = new HashMap<String, String>();
		try
		{
			InstallApp.installApplication(mContext, savePath, fileName, installingPackage, appID);
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
					ResponseCode.METHOD_APPLICATION_INSTALL_SYSTEM, message);
		}
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
	 * use thread to download and install
	 */
	public void installApplicationThread(String url, String savePath, String apkName, int appID)
	{

		Thread install = new Thread(new InstallAppRunnable(url, savePath, apkName, appID));
		install.start();
	}

	/**
	 * Installs an application to the device this method need use Thread run or
	 * will cause block main thread error because of downloading App File
	 * 
	 * @param url
	 *            download
	 * @param apkName
	 *            fileName
	 */
	private void installApplicationWithDownload(String url, String savePath, String apkName, int appID)
	{

		downloadApp(url, savePath, apkName, appID);
		installApplication(savePath, apkName, appID);
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

		uninstallingPackage.add(new AppData(packageName, "", "", appID));

		if (UninstallApp.unInstallApplication(mContext, packageName, appID) == false)
		{
			ArrayListUtility.findEqualAndRemoveForAppDataClass(uninstallingPackage, packageName);

			message.put("message", "not find the package which need to uninstall");
			super.callBackMessage(ResponseCode.ERR_PACKAGE_NOT_FIND, CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
					ResponseCode.METHOD_APPLICATION_UNINSTALL_SYSTEM, message);
		}
		else
		{
		}
	}

	@Override
	public void startListenAction()
	{
		mContext.registerReceiver(receiver, filter);
		isRegisterReceiver = true;
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
						if (null != installed)
						{

							callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_INSTALL_SYSTEM, message);

							IOFileHandler.deleteFile(IOFileHandler.getExternalStorageDirectory() + "/"
									+ defaultDownloadApkSavePath + installed.fileName);

							ArrayListUtility.findEqualAndRemoveForAppDataClass(installingPackage, packageName);

						}
						else
						{
							callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_INSTALL_USER, message);
						}
					}
					else if (appAction.equals("android.intent.action.PACKAGE_REMOVED"))
					{
						if (ArrayListUtility.findEqualAndRemoveForAppDataClass(uninstallingPackage, packageName))
						{
							callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_UNINSTALL_SYSTEM, message);
						}
						else
						{
							callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_UNINSTALL_USER, message);
						}
					}
				}
			}
		});
	}

	@Override
	public void stopListenAction()
	{
		if (null != receiver && isRegisterReceiver == true)
		{
			mContext.unregisterReceiver(receiver);
			isRegisterReceiver = false;
		}
	}

	private class InstallAppRunnable implements Runnable
	{
		private String uRLPath = null;
		private String fileName = null;
		private String savePath = null;
		private int appID;

		@Override
		public void run()
		{
			installApplicationWithDownload(uRLPath, savePath, fileName, appID);
		}

		public InstallAppRunnable(String uRLPath, String savePath, String fileName, int appID)
		{
			this.fileName = fileName;
			this.uRLPath = uRLPath;
			this.appID = appID;
			this.savePath = savePath;
		}

	}

	private void downloadApp(String uRLPath, String savePath, String fileName, int appID)
	{
		int errorType = ResponseCode.ERR_SUCCESS;
		boolean anyError = true;
		HashMap<String, String> message = new HashMap<String, String>();
		String errorMessage = "";

		try
		{
			// start to download! state = 0;
			message.put("downloadState", String.valueOf(0));
			message.put("message", "success!");
			message.put("uRLPath", uRLPath);
			message.put("savePath", savePath);
			message.put("fileName", fileName);
			message.put("appID", String.valueOf(appID));
			callBackMessage(errorType, CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
					ResponseCode.METHOD_APPLICATION_DOWNLOAD_APP, message);
			message.clear();
			// downloading apk
			IOFileHandler.urlDownloader(uRLPath, savePath, fileName);

			anyError = false;
			// finish download! state = 1;
			message.put("message", "success!");
			message.put("uRLPath", uRLPath);
			message.put("savePath", savePath);
			message.put("fileName", fileName);
			message.put("appID", String.valueOf(appID));
			message.put("downloadState", String.valueOf(1));
			callBackMessage(errorType, CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
					ResponseCode.METHOD_APPLICATION_DOWNLOAD_APP, message);
		}
		catch (MalformedURLException e)
		{
			errorMessage = e.toString();
			errorType = ResponseCode.ERR_MALFORMED_URL_EXCEPTION;
		}
		catch (ProtocolException e)
		{
			errorMessage = e.toString();
			errorType = ResponseCode.ERR_PROTOCOL_EXCEPTION;
		}
		catch (IOException e)
		{
			errorMessage = e.toString();
			errorType = ResponseCode.ERR_IO_EXCEPTION;
		}
		finally
		{
			if (anyError)
			{
				message.put("message", errorMessage);
				// error while downloading state = -1
				message.put("downloadState", "-1");
				message.put("uRLPath", uRLPath);
				message.put("savePath", savePath);
				message.put("fileName", fileName);
				message.put("appID", String.valueOf(appID));
				callBackMessage(errorType, CtrlType.MSG_RESPONSE_APPLICATION_HANDLER,
						ResponseCode.METHOD_APPLICATION_DOWNLOAD_APP, message);
			}
		}

	}

	private class DownloadRunnable implements Runnable
	{
		private String uRLPath = null;
		private String savePath = null;
		private String fileName = null;
		private int appID = -1;

		@Override
		public void run()
		{
			downloadApp(uRLPath, savePath, fileName, appID);
		}

		public DownloadRunnable(String uRLPath, String savePath, String fileName, int appID)
		{
			this.uRLPath = uRLPath;
			this.savePath = savePath;
			this.fileName = fileName;
			this.appID = appID;
		}

	}

}
