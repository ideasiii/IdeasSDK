package sdk.ideas.mdm.restore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import android.content.Context;
import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.OnCallbackResult;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ArrayListUtility.ReturnColectionData;
import sdk.ideas.mdm.MDMType;
import sdk.ideas.mdm.app.ApplicationHandler;
import sdk.ideas.mdm.applist.ApplicationList;
import sdk.ideas.mdm.record.RecordHandler;

public class RestoreHandler
{
	private Context mContext = null;

	ArrayList<String> arrayNeedUninstall = null;
	ArrayList<String> arrayNeedInstall = null;
	private ApplicationHandler appHandler = null;
	private boolean mReadLocalInit = false;
	private boolean recordSystemApplication = false;

	public RestoreHandler(Context context, boolean readLocalInit)
	{
		this.mContext = context;
		this.mReadLocalInit = readLocalInit;

	}

	public void restore()
	{
		// for app to restore
		Thread restoreApp = null;

		restoreApp = new Thread(new Restore());

		restoreApp.start();
	}

	private void restoreFile()
	{

		ArrayList<String> fileList = null;
		ReturnColectionData data = null;

		RecordHandler recordData = new RecordHandler(mContext, false);

		// sendRestoreResultMessage("start download data profile from cloud
		// server");
		try
		{
			recordData.recordInitFileListPathInSDCard(false, true);
			fileList = IOFileHandler.readFromExteralFile(MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, "fileList.txt");

		}
		catch (Exception e)
		{
			Logs.showTrace(e.toString());
		}

		if (mReadLocalInit == true)
		{
			if (null != fileList)
			{
				try
				{
					data = ArrayListUtility.ArrayListDifference(
							IOFileHandler.readFromInternalFile(mContext, MDMType.INIT_LOCAL_MDM_SDCARD_PATH), fileList);
				}
				catch (FileNotFoundException e)
				{
					// sendRestoreResultMessage(e.toString());
				}
				catch (IOException e)
				{
					// sendRestoreResultMessage(e.toString());
				}
			}
			else
			{
				Logs.showTrace("65");
			}
		}
		else
		{
			try
			{
				IOFileHandler.urlDownloader(MDMType.URL_MDM_PROFILE + MDMType.INIT_SERVER_MDM_SDCARD_PATH,
						MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, MDMType.INIT_SERVER_MDM_SDCARD_PATH);
			}
			catch (MalformedURLException e)
			{
				// sendRestoreResultMessage(e.toString());
			}
			catch (ProtocolException e)
			{
				// sendRestoreResultMessage(e.toString());
			}
			catch (IOException e)
			{
				// sendRestoreResultMessage(e.toString());
			}
			ArrayList<String> initSDCardFile = null;

			try
			{
				initSDCardFile = IOFileHandler.readFromExteralFile(MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH,
						MDMType.INIT_SERVER_MDM_SDCARD_PATH);

				data = ArrayListUtility.ArrayListDifference(initSDCardFile, fileList);
			}
			catch (Exception e)
			{
				Logs.showTrace(e.toString());
			}
		}
		// sendRestoreResultMessage("download success");

		ArrayList<String> needToDeleteFileList = new ArrayList<String>(data.new_b);

		// delete filelist
		needToDeleteFileList.add(IOFileHandler.getExternalStorageDirectory() + "/"
				+ MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH + "fileList.txt");

		if (mReadLocalInit == false)
		{
			needToDeleteFileList.add(IOFileHandler.getExternalStorageDirectory() + "/"
					+ MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH + MDMType.INIT_SERVER_MDM_SDCARD_PATH);
		}
		// sendRestoreResultMessage("need to delete append data number: " +
		// String.valueOf(needToDeleteFileList.size()));
		// sendRestoreResultMessage("start to delete");
		if (true == IOFileHandler.deleteFileList(needToDeleteFileList))
		{
			Logs.showTrace("delete append file successful");
		}
		// sendRestoreResultMessage("file restore success");
	}

	private void restoreApp()
	{

		if (mReadLocalInit == true)
		{

			ReturnColectionData data = null;
			try
			{
				data = ArrayListUtility.ArrayListDifference(
						IOFileHandler.readFromInternalFile(mContext, MDMType.INIT_LOCAL_MDM_APP_PATH),
						ArrayListUtility.AppInfoConvertToArrayListString(
								ApplicationList.getInstalledApps(mContext, recordSystemApplication)));
			}
			catch (FileNotFoundException e)
			{
				// sendRestoreResultMessage(e.toString());
			}
			catch (IOException e)
			{
				// sendRestoreResultMessage(e.toString());
			}

			arrayNeedUninstall = new ArrayList<String>(data.new_b);
			arrayNeedInstall = new ArrayList<String>(data.new_a);
		}
		else
		{
			try
			{
				// sendRestoreResultMessage("start download app profile from
				// cloud server");
				IOFileHandler.urlDownloader(MDMType.URL_MDM_PROFILE + MDMType.INIT_SERVER_MDM_APP_PATH,
						MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, MDMType.INIT_SERVER_MDM_APP_PATH);

				ArrayList<String> initAppList = IOFileHandler.readFromExteralFile(
						MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, MDMType.INIT_SERVER_MDM_APP_PATH);

				IOFileHandler.deleteFile(IOFileHandler.getExternalStorageDirectory() + "/"
						+ MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH + MDMType.INIT_SERVER_MDM_APP_PATH);

				ReturnColectionData data = ArrayListUtility.ArrayListDifference(initAppList,
						ArrayListUtility.AppInfoConvertToArrayListString(
								ApplicationList.getInstalledApps(mContext, recordSystemApplication)));

				arrayNeedUninstall = new ArrayList<String>(data.new_b);
				arrayNeedInstall = new ArrayList<String>(data.new_a);
			}
			catch (Exception e)
			{
				Logs.showTrace(e.toString());
			}
		}
		// sendRestoreResultMessage("download success");
		if (null != arrayNeedUninstall && null != arrayNeedInstall)
		{
			// sendRestoreResultMessage("need to uninstall list");
			Logs.showTrace("need to uninstall list");
			for (int i = 0; i < arrayNeedUninstall.size(); i++)
			{
				// sendRestoreResultMessage(arrayNeedUninstall.get(i));
				Logs.showTrace(arrayNeedUninstall.get(i));
			}
			// sendRestoreResultMessage("start to delete app");
			for (int i = 0; i < arrayNeedUninstall.size(); i++)
			{
				appHandler.unInstallApplication(arrayNeedUninstall.get(i));
			}

			// sendRestoreResultMessage("need to install list");
			Logs.showTrace("need to install list");

			for (int i = 0; i < arrayNeedInstall.size(); i++)
			{
				// sendRestoreResultMessage(arrayNeedInstall.get(i));
				Logs.showTrace(arrayNeedInstall.get(i));
			}
			// sendRestoreResultMessage("start to install app");
			for (int i = 0; i < arrayNeedInstall.size(); i++)
			{
				// sendRestoreResultMessage("now start to download " +
				// arrayNeedInstall.get(i) + " and install");
				appHandler.installApplication(MDMType.URL_MDM_APP_DOWNLOAD + arrayNeedInstall.get(i) + ".apk",
						arrayNeedInstall.get(i) + ".apk");
			}
			// sendRestoreResultMessage("app restore success");
		}
	}

	public void listenAppAction()
	{

		Logs.showTrace("registerReceiver");
		
		appHandler.setOnCallbackResultListener(new OnCallbackResult()
		{

			@Override
			public void onCallbackResult(int result, int what, int from, String message)
			{
				if(what == MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER)
				{
					switch (from)
					{
					case ResponseCode.METHOD_APPLICATION_INSTALL_SYSTEM:
						if (null != arrayNeedInstall && arrayNeedInstall.size() != 0)
						{
							if (ArrayListUtility.findContainAndRemove(arrayNeedInstall, message))
							{
								IOFileHandler.deleteFile(IOFileHandler.getExternalStorageDirectory() + "/"
										+ MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH + message + ".apk");
								Logs.showTrace(String.valueOf(arrayNeedInstall.size()));
							}
						}
						break;
					
					case ResponseCode.METHOD_APPLICATION_UNINSTALL_SYSTEM:
						if (null != arrayNeedUninstall && arrayNeedUninstall.size() != 0)
						{
							if (ArrayListUtility.findContainAndRemove(arrayNeedInstall, message))
							{
								Logs.showTrace(String.valueOf(arrayNeedUninstall.size()));
							}

						}
						break;
						
						
						case ResponseCode.METHOD_APPLICATION_INSTALL_USER:
						
						break;
					case ResponseCode.METHOD_APPLICATION_UNINSTALL_USER:

						
						break;
					default:
						break;

					}
				
				}
				if (null != arrayNeedInstall && null != arrayNeedUninstall)
				{
					if (arrayNeedInstall.size() == 0 && arrayNeedUninstall.size() == 0)
					{
						stopListenAppAction();
						
						//sendRestoreResultMessage("0000XX");
					}
				}
				
				
			}
			
			
			
			
			
		});
		/*
		
		
		appHandler.setOnAppLicationListener(new ReturnApplicationAction()
		{

			@Override
			public void returnApplicationActionResult(String appAction, String packageName, boolean isYourAction)
			{

				Logs.showTrace(appAction + "  " + packageName + "  " + String.valueOf(isYourAction));

				if (appAction.equals("android.intent.action.PACKAGE_ADDED"))
				{
					Logs.showTrace(appAction);
					if (null != arrayNeedInstall && arrayNeedInstall.size() != 0)
					{
						if (ArrayListUtility.findContainAndRemove(arrayNeedInstall, packageName))
						{
							IOFileHandler.deleteFile(IOFileHandler.getExternalStorageDirectory() + "/"
									+ MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH + packageName + ".apk");

							Logs.showTrace(String.valueOf(arrayNeedInstall.size()));

						}
					}

				}
				else if (appAction.equals("android.intent.action.PACKAGE_REMOVED"))
				{
					if (null != arrayNeedUninstall && arrayNeedUninstall.size() != 0)
					{
						if (ArrayListUtility.findContainAndRemove(arrayNeedInstall, packageName))
						{
							Logs.showTrace(String.valueOf(arrayNeedUninstall.size()));
						}

					}

				}
				if (null != arrayNeedInstall && null != arrayNeedUninstall)
				{
					if (arrayNeedInstall.size() == 0 && arrayNeedUninstall.size() == 0)
					{
						stopListenAppAction();
						sendRestoreResultMessage("0000XX");
					}
				}

			}

		});
*/
	}

	public void stopListenAppAction()
	{
		appHandler.stopListenAppAction();
	}

	private class Restore implements Runnable
	{

		@Override
		public void run()
		{
			restoreFile();
			listenAppAction();
			restoreApp();
		}

		public Restore()
		{

		}

	}

}
