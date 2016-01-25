package sdk.ideas.mdm.restore;

import java.util.ArrayList;
import android.content.Context;

import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ArrayListUtility.ReturnColectionData;
import sdk.ideas.mdm.MDMType;
import sdk.ideas.mdm.app.ApplicationHandler;
import sdk.ideas.mdm.app.PackageReceiver.ReturnAppAction;
import sdk.ideas.mdm.applist.ApplicationList;
import sdk.ideas.mdm.record.RecordFileData;

public class Restore implements Runnable
{
	private Context mContext = null;
	private boolean mReadLocalInit = false;
	private boolean recordSystemApplication = false;
	private ApplicationHandler appHandler  = null;
	ArrayList<String> arrayNeedUninstall = null;
	ArrayList<String> arrayNeedInstall = null;
	
	public Restore(Context context, boolean readLocalInit)
	{
		this.mContext = context;
		this.mReadLocalInit = readLocalInit;
		appHandler = new ApplicationHandler(mContext);
	}
	@Override
	public void run()
	{
		restoreFile();
		listenAppAction();
		restoreApp();
		
	}

	public void restoreFile()
	{

		ArrayList<String> fileList = null;
		ReturnColectionData data = null;

		Thread fileListThread = new Thread(new RecordFileData(mContext, false));
		fileListThread.start();

		try
		{
			fileListThread.join();
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
				data = ArrayListUtility.ArrayListDifference(
						IOFileHandler.readFromInternalFile(mContext, MDMType.INIT_LOCAL_MDM_SDCARD_PATH), fileList);
			}
			else
			{
				Logs.showTrace("65");
			}
		}
		else
		{
			IOFileHandler.urlDownloader(MDMType.URL_MDM_PROFILE + MDMType.INIT_SERVER_MDM_SDCARD_PATH,
					MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, MDMType.INIT_SERVER_MDM_SDCARD_PATH);
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

		ArrayList<String> needToDeleteFileList = new ArrayList<String>(data.new_b);
		
		//delete filelist 
		needToDeleteFileList.add(IOFileHandler.getExternalStorageDirectory() + "/"
				+ MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH + "fileList.txt");

		if (mReadLocalInit == false)
		{
			needToDeleteFileList.add(IOFileHandler.getExternalStorageDirectory() + "/"
					+ MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH + MDMType.INIT_SERVER_MDM_SDCARD_PATH);
		}

		if (true == IOFileHandler.deleteFileList(needToDeleteFileList))
		{
			Logs.showTrace("delete successful");
		}
	}

	public void restoreApp()
	{
	
		if (mReadLocalInit == true)
		{

			ReturnColectionData data = ArrayListUtility.ArrayListDifference(
					IOFileHandler.readFromInternalFile(mContext, MDMType.INIT_LOCAL_MDM_APP_PATH),
					ArrayListUtility.AppInfoConvertToArrayListString(
							ApplicationList.getInstalledApps(mContext, recordSystemApplication)));

			arrayNeedUninstall = new ArrayList<String>(data.new_b);
			arrayNeedInstall = new ArrayList<String>(data.new_a);
		}
		else
		{
			try
			{
				IOFileHandler.urlDownloader(MDMType.URL_MDM_PROFILE + MDMType.INIT_SERVER_MDM_APP_PATH,
						MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, MDMType.INIT_SERVER_MDM_APP_PATH);

				ArrayList<String> initAppList = IOFileHandler.readFromExteralFile(
						MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, MDMType.INIT_SERVER_MDM_APP_PATH);
				
				IOFileHandler.deleteFile(IOFileHandler.getExternalStorageDirectory()
						+"/"+ MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH + MDMType.INIT_SERVER_MDM_APP_PATH);

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

		if (null != arrayNeedUninstall && null != arrayNeedInstall)
		{
			Logs.showTrace("need to uninstall list");
			for (int i = 0; i < arrayNeedUninstall.size(); i++)
			{
				Logs.showTrace(arrayNeedUninstall.get(i));
			}
			for (int i = 0; i < arrayNeedUninstall.size(); i++)
			{
				appHandler.unInstallApplication(arrayNeedUninstall.get(i));
			}

			Logs.showTrace("need to install list");
			for (int i = 0; i < arrayNeedInstall.size(); i++)
			{
				Logs.showTrace(arrayNeedInstall.get(i));
			}

			for (int i = 0; i < arrayNeedInstall.size(); i++)
			{
				appHandler.installApplication(MDMType.URL_MDM_APP_DOWNLOAD + arrayNeedInstall.get(i) + ".apk",
						arrayNeedInstall.get(i) + ".apk");
			}
		}
	}
	
	public void listenAppAction()
	{
		
		
		appHandler.listenAppActionInit();
		Logs.showTrace("registerReceiver");
		appHandler.getPackageReceiver().setOnPackageReceiverListener(new ReturnAppAction()
		{
			
			@Override
			public void returnAppActionResult(String appAction, String packageName)
			{
				
				if (appAction.equals("android.intent.action.PACKAGE_ADDED"))
				{
					Logs.showTrace(appAction);
					if (null != arrayNeedInstall && arrayNeedInstall.size() != 0)
					{
						if (arrayNeedInstall.contains(packageName))
						{
							IOFileHandler.deleteFile(IOFileHandler.getExternalStorageDirectory() + "/"
									+ MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH + packageName + ".apk");
							
							ArrayListUtility.findContain(arrayNeedInstall,packageName);
							Logs.showTrace(String.valueOf(arrayNeedInstall.size()));
							if (arrayNeedInstall.size() == 0)
							{
								stopListenAppAction();
							}
						}
					}
					
				}
			}
	
		});
	}
	public void stopListenAppAction()
	{
		appHandler.stopListenAppAction();
	}
	
	
}
