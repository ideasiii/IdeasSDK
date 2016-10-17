package sdk.ideas.ctrl.restore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.OnCallbackResult;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ArrayListUtility.ReturnColectionData;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.ctrl.app.ApplicationHandler;
import sdk.ideas.ctrl.record.RecordHandler;

public class RestoreHandler extends BaseHandler
{

	private ApplicationHandler mApplicationHandler = null;

	private boolean readLocalProfile = false;
	private boolean isReadInInternal = true;

	private String externalProfilesReadPath = "";

	private String sdCardFileListProfilefileName = "";
	private String appListProfileFileName = "";

	private String appDownloadServerURL = "http://54.199.198.94/app/android/";
	private String profileDownloadServerURL = "http://54.199.198.94/mdm/profile/";

	private String temporaryDownloadPath = "Download/";

	private RecordHandler mRecordHandler = null;

	private static int installSimulationID = Short.MAX_VALUE - 5000;
	private static int uninstallSimulationID = Short.MAX_VALUE - 3000;

	private boolean restoreAppError = false;
	private boolean restoreFileError = false;

	private boolean isOtherAppHandlerDeploy = false;

	private ArrayList<AppEvent> appEventList = new ArrayList<AppEvent>();

	private synchronized boolean removeOrAddAppEvent(boolean isRemove, AppEvent e, boolean isInstall,
			String packageName)
	{
		if (isRemove)
		{
			for (int i = 0; i < appEventList.size(); i++)
			{
				if (appEventList.get(i).packageName.equals(packageName) && appEventList.get(i).isInstall == isInstall)
				{
					appEventList.remove(i);
					break;
				}

			}

		}
		// add
		else
		{
			appEventList.add(e);
		}
		if (appEventList.size() == 0)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	public RestoreHandler(Context context)
	{
		super(context);

		mRecordHandler = new RecordHandler(context);

	}

	public void setRestoreFlag(boolean restoreAppFlag, boolean restoreFileFlag)
	{
		if (null != mRecordHandler)
		{
			mRecordHandler.setRecordFlag(restoreAppFlag, restoreFileFlag);
		}
	}

	public void setTemporaryDownloadPathLocation(String temporaryDownloadPath)
	{
		this.temporaryDownloadPath = temporaryDownloadPath;
	}

	public void setAppDownloadServerURL(String appDownloadServerURL)
	{
		this.appDownloadServerURL = appDownloadServerURL;
		Logs.showTrace("[RestoreHandler]now appDownloadServerURL: " + this.appDownloadServerURL);
	}

	public void setLocalProfileReadPath(boolean isReadInInternalMem, String sdCardFileListfileName,
			String appListFileName, String externalProfilesReadPath)
	{
		readLocalProfile = true;
		this.isReadInInternal = isReadInInternalMem;
		if (isReadInInternalMem == false)
		{
			this.externalProfilesReadPath = externalProfilesReadPath;
		}
		this.sdCardFileListProfilefileName = sdCardFileListfileName;
		this.appListProfileFileName = appListFileName;
	}

	public void setServerProfileReadPath(String profileDownloadServerURL, String sdCardFileListfileName,
			String appListFileName)
	{
		readLocalProfile = false;

		this.profileDownloadServerURL = profileDownloadServerURL;

		this.sdCardFileListProfilefileName = sdCardFileListfileName;
		this.appListProfileFileName = appListFileName;
	}

	public void setOnApplicationHandler(ApplicationHandler mApplicationHandler)
	{
		this.mApplicationHandler = mApplicationHandler;
		isOtherAppHandlerDeploy = true;
	}

	public void restore()
	{
		Thread restoreRunnable = null;
		restoreRunnable = new Thread(new Restore());
		restoreRunnable.start();
	}

	private void setOnRecordHandlerAndRestore()
	{
		if (null != mRecordHandler)
		{
			mRecordHandler.setOnCallbackResultListener(new OnCallbackResult()
			{
				@Override
				public void onCallbackResult(int result, int what, int from, HashMap<String, String> message)
				{
					// write some callback judgment, such as handing information
					if (result == ResponseCode.ERR_SUCCESS)
					{
						if (from == ResponseCode.METHOD_RECORD_APPLICATION)
						{
							restoreApp(mRecordHandler.getAppData());
						}
						else if (from == ResponseCode.METHOD_RECORD_SDCARD_FILE)
						{
							restoreFile(mRecordHandler.getFilePathData());
						}
					}
					else
					{
						// do not handle error message in restore handler, just
						// push it out of restore handler
						callBackMessage(result, what, from, message);
					}
				}
			});
			mRecordHandler.record();
		}
	}

	private void restoreApp(ArrayList<String> nowInstalledAppList)
	{
		ReturnColectionData data = null;
		HashMap<String, String> messageResponse = new HashMap<String, String>();
		try
		{
			if (null == mApplicationHandler)
			{
				mApplicationHandler = new ApplicationHandler(mContext);
				mApplicationHandler.startListenAction();
			}

			mApplicationHandler.setOnCallbackResultListener(new OnCallbackResult()
			{
				@Override
				public void onCallbackResult(int result, int what, int from, HashMap<String, String> message)
				{
					switch (from)
					{

					case ResponseCode.METHOD_APPLICATION_DOWNLOAD_APP:
						if (null != message.get("downloadSate"))
						{
							int downloadState = Integer.valueOf(message.get("downloadSate"));

							if (downloadState == -1)
							{
								// handler download
								Logs.showTrace("[RestoreHandler]download ERROR:" + message.get("packageName") + " ERROR"
										+ message.get("message"));
								removeOrAddAppEvent(true, null, true, message.get("packageName"));

							}
						}

						break;
					case ResponseCode.METHOD_APPLICATION_INSTALL_SYSTEM:

						if (removeOrAddAppEvent(true, null, true, message.get("packageName")))
						{
							HashMap<String, String> messageResponse = new HashMap<String, String>();
							messageResponse.put("message", "success");
							callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
									ResponseCode.METHOD_RESTORE_APPLICATION, messageResponse);
						}

						break;

					case ResponseCode.METHOD_APPLICATION_UNINSTALL_SYSTEM:

						if (removeOrAddAppEvent(true, null, false, message.get("packageName")))
						{
							HashMap<String, String> messageResponse = new HashMap<String, String>();
							messageResponse.put("message", "success");
							callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
									ResponseCode.METHOD_RESTORE_APPLICATION, messageResponse);
						}
						break;

					}

					if (result != ResponseCode.ERR_SUCCESS)
					{
						restoreAppError = true;
						callBackMessage(result, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
								ResponseCode.METHOD_RESTORE_APPLICATION, message);
					}
				}
			});

			if (readLocalProfile == true)
			{
				if (this.isReadInInternal == true)
				{
					data = ArrayListUtility.ArrayListDifference(
							IOFileHandler.readFromInternalFile(mContext, this.appListProfileFileName),
							nowInstalledAppList);

				}
				else
				{
					data = ArrayListUtility.ArrayListDifference(
							IOFileHandler.readFromExteralFile(externalProfilesReadPath, appListProfileFileName),
							nowInstalledAppList);
				}
			}
			else
			{
				IOFileHandler.urlDownloader(this.profileDownloadServerURL + this.appListProfileFileName,
						temporaryDownloadPath, this.appListProfileFileName);

				ArrayList<String> serverProfileAppList = IOFileHandler.readFromExteralFile(temporaryDownloadPath,
						this.appListProfileFileName);

				IOFileHandler.deleteFile(IOFileHandler.getExternalStorageDirectory() + "/" + temporaryDownloadPath
						+ this.appListProfileFileName);

				data = ArrayListUtility.ArrayListDifference(serverProfileAppList, nowInstalledAppList);

			}

			ArrayList<String> arrayNeedUninstall = new ArrayList<String>(data.new_b);
			ArrayList<String> arrayNeedInstall = new ArrayList<String>(data.new_a);

			if (null != arrayNeedUninstall && null != arrayNeedInstall)
			{
				/*
				 * for using debugging Logs.showTrace("need to uninstall list");
				 * for (int i = 0; i < arrayNeedUninstall.size(); i++)
				 * Logs.showTrace(arrayNeedUninstall.get(i)); Logs.showTrace(
				 * "need to install list"); for (int i = 0; i <
				 * arrayNeedInstall.size(); i++)
				 * Logs.showTrace(arrayNeedInstall.get(i));
				 */
				if(arrayNeedUninstall.size() == 0 && arrayNeedInstall.size() == 0)
				{
					callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
							ResponseCode.METHOD_RESTORE_APPLICATION, messageResponse);
				}

				for (int i = 0; i < arrayNeedUninstall.size(); i++)
				{
					int appID = uninstallSimulationID--;
					removeOrAddAppEvent(false, new AppEvent(arrayNeedUninstall.get(i), appID, false), false, null);
					mApplicationHandler.unInstallApplication(arrayNeedUninstall.get(i), appID);
				}

				for (int i = 0; i < arrayNeedInstall.size(); i++)
				{
					// mApplicationHandler.installApplication(appDownloadServerURL
					// + arrayNeedInstall.get(i) + ".apk",
					// temporaryDownloadPath, arrayNeedInstall.get(i) + ".apk",
					// installSimulationID++);

					int appID = installSimulationID++;
					removeOrAddAppEvent(false, new AppEvent(arrayNeedInstall.get(i), appID, true), false, null);
					mApplicationHandler.installApplicationThread(
							appDownloadServerURL + arrayNeedInstall.get(i) + ".apk", temporaryDownloadPath,
							arrayNeedInstall.get(i) + ".apk", appID);

				}
			}
		}
		catch (SecurityException e)
		{
			restoreAppError = true;
			messageResponse.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
					ResponseCode.METHOD_RESTORE_APPLICATION, messageResponse);
		}
		catch (FileNotFoundException e)
		{
			restoreAppError = true;
			messageResponse.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
					ResponseCode.METHOD_RESTORE_APPLICATION, messageResponse);

		}
		catch (IOException e)
		{
			restoreAppError = true;
			messageResponse.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
					ResponseCode.METHOD_RESTORE_APPLICATION, messageResponse);
		}
		catch (Exception e)
		{
			restoreAppError = true;
			messageResponse.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
					ResponseCode.METHOD_RESTORE_APPLICATION, messageResponse);
		}
		finally
		{
			messageResponse.clear();
		}

		if (restoreAppError == false)
		{
			messageResponse.put("message", "success");
			// callBackMessage(ResponseCode.ERR_SUCCESS,
			// CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
			// ResponseCode.METHOD_RESTORE_APPLICATION, message);
			messageResponse.clear();

			// close self create mApplicationHandler
			if (isOtherAppHandlerDeploy == false)
			{
				mApplicationHandler.stopListenAction();
				mApplicationHandler = null;
			}
		}
	}

	// restore file first
	private void restoreFile(ArrayList<String> nowSDCardFileListed)
	{
		ReturnColectionData data = null;
		HashMap<String, String> messageResponse = new HashMap<String, String>();
		try
		{
			if (readLocalProfile == true)
			{
				if (null != nowSDCardFileListed)
				{
					if (this.isReadInInternal == true)
					{
						data = ArrayListUtility.ArrayListDifference(
								IOFileHandler.readFromInternalFile(mContext, this.sdCardFileListProfilefileName),
								nowSDCardFileListed);
					}
					else
					{
						data = ArrayListUtility.ArrayListDifference(IOFileHandler
								.readFromExteralFile(this.externalProfilesReadPath, this.sdCardFileListProfilefileName),
								nowSDCardFileListed);
					}

				}
			}
			// start download sdcard file profile from cloud server and read it
			else
			{
				IOFileHandler.urlDownloader(this.profileDownloadServerURL + this.sdCardFileListProfilefileName,
						this.temporaryDownloadPath, this.sdCardFileListProfilefileName);

				data = ArrayListUtility.ArrayListDifference(IOFileHandler.readFromExteralFile(
						this.temporaryDownloadPath, this.sdCardFileListProfilefileName), nowSDCardFileListed);

			}

			ArrayList<String> needToDeleteFileList = new ArrayList<String>(data.new_b);

			if (readLocalProfile == false)
			{
				needToDeleteFileList.add(IOFileHandler.getExternalStorageDirectory() + "/" + this.temporaryDownloadPath
						+ this.sdCardFileListProfilefileName);
			}

			if (true == IOFileHandler.deleteFileList(needToDeleteFileList))
			{
				Logs.showTrace("delete append file successful");
			}
		}
		catch (SecurityException e)
		{
			restoreFileError = true;
			messageResponse.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
					ResponseCode.METHOD_RESTORE_SDCARD_FILE, messageResponse);

		}
		catch (FileNotFoundException e)
		{
			restoreFileError = true;
			messageResponse.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
					ResponseCode.METHOD_RESTORE_SDCARD_FILE, messageResponse);

		}
		catch (IOException e)
		{
			restoreFileError = true;
			messageResponse.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
					ResponseCode.METHOD_RESTORE_SDCARD_FILE, messageResponse);
		}
		catch (Exception e)
		{
			restoreFileError = true;
			messageResponse.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
					ResponseCode.METHOD_RESTORE_SDCARD_FILE, messageResponse);

		}
		finally
		{
			messageResponse.clear();
		}
		if (restoreFileError == false)
		{
			messageResponse.put("message", "success");
			super.callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_RESTORE_HANDLER,
					ResponseCode.METHOD_RESTORE_SDCARD_FILE, messageResponse);
			messageResponse.clear();
		}
	}

	private class Restore implements Runnable
	{
		@Override
		public void run()
		{
			setOnRecordHandlerAndRestore();
		}

		public Restore()
		{
		}

	}

	private class AppEvent
	{
		public String packageName = null;
		public int state = 0;
		public boolean isInstall = true;

		public AppEvent(String packageName, int state, boolean isInstall)
		{
			this.packageName = packageName;
			this.isInstall = isInstall;
			this.state = state;
		}

	}

}
