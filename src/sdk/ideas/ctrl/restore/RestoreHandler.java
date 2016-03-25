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

	private String appDownloadServerURL = "http://54.199.198.94:8080/app/android/";
	private String profileDownloadServerURL = "http://54.199.198.94:8080/mdm/profile/";
	
	private String temporaryDownloadPath = "Download/";

	private RecordHandler mRecordHandler = null;
	
	private static int installSimulationID = 1000;
	private static int uninstallSimulationID = Integer.MAX_VALUE -1;

	private HashMap<String,String> message = null;

	private boolean restoreAppError = false;
	private boolean restoreFileError = false;
	
	private boolean isOtherAppHandlerDeploy = false;
	

	public RestoreHandler(Context context)
	{
		super(context);
		
		mRecordHandler = new RecordHandler(context);
		
		message = new HashMap<String,String>();
		
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
	}
	
	public void setLocalProfileReadPath(boolean isReadInInternalMem, String sdCardFileListfileName,String appListFileName, String externalProfilesReadPath)
	{
		readLocalProfile = true;
		this.isReadInInternal = isReadInInternalMem;
		if(isReadInInternalMem == false)
		{
			this.externalProfilesReadPath = externalProfilesReadPath;
		}
		this.sdCardFileListProfilefileName = sdCardFileListfileName;
		this.appListProfileFileName = appListFileName;
	}
	
	public void setServerProfileReadPath(String profileDownloadServerURL,String sdCardFileListfileName,String appListFileName)
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
					if(result != ResponseCode.ERR_SUCCESS)
					{
						restoreAppError = true;
						callBackMessage(result, CtrlType.MSG_RESPONSE_RESTORE_HANDLER, ResponseCode.METHOD_RESTORE_APPLICATION, message);
					}
				}
			});
			
			if (readLocalProfile == true)
			{
				if(this.isReadInInternal == true)
				{
				data = ArrayListUtility.ArrayListDifference(
						IOFileHandler.readFromInternalFile(mContext, this.appListProfileFileName), nowInstalledAppList);
			
				}
				else
				{
					data = ArrayListUtility.ArrayListDifference(
							IOFileHandler.readFromExteralFile(externalProfilesReadPath, appListProfileFileName), nowInstalledAppList);
				}
			}
			else
			{
				IOFileHandler.urlDownloader(this.profileDownloadServerURL+ this.appListProfileFileName,
						temporaryDownloadPath, this.appListProfileFileName);

				ArrayList<String> serverProfileAppList = IOFileHandler.readFromExteralFile(
						temporaryDownloadPath, this.appListProfileFileName);

				IOFileHandler.deleteFile(IOFileHandler.getExternalStorageDirectory() + "/"
						+ temporaryDownloadPath +this.appListProfileFileName);

				data = ArrayListUtility.ArrayListDifference(serverProfileAppList, nowInstalledAppList);

			}
			
			ArrayList<String> arrayNeedUninstall = new ArrayList<String>(data.new_b);
			ArrayList<String> arrayNeedInstall = new ArrayList<String>(data.new_a);

			if (null != arrayNeedUninstall && null != arrayNeedInstall)
			{
				/* for using debugging  
				Logs.showTrace("need to uninstall list");
				for (int i = 0; i < arrayNeedUninstall.size(); i++)
					Logs.showTrace(arrayNeedUninstall.get(i));
				Logs.showTrace("need to install list");
				for (int i = 0; i < arrayNeedInstall.size(); i++)
					Logs.showTrace(arrayNeedInstall.get(i));
				 */
				
				for (int i = 0; i < arrayNeedUninstall.size(); i++)
				{
					mApplicationHandler.unInstallApplication(arrayNeedUninstall.get(i), uninstallSimulationID--);
				}

				for (int i = 0; i < arrayNeedInstall.size(); i++)
				{
					mApplicationHandler.installApplication(appDownloadServerURL + arrayNeedInstall.get(i) + ".apk",
							temporaryDownloadPath, arrayNeedInstall.get(i) + ".apk", installSimulationID++);
				}
			}
		}
		catch(SecurityException e)
		{
			restoreAppError = true;
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION, CtrlType.MSG_RESPONSE_RESTORE_HANDLER, ResponseCode.METHOD_RESTORE_APPLICATION, message);
		}
		catch (FileNotFoundException e)
		{
			restoreAppError = true;
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION,CtrlType.MSG_RESPONSE_RESTORE_HANDLER, ResponseCode.METHOD_RESTORE_APPLICATION, message);
			
		}
		catch (IOException e)
		{
			restoreAppError = true;
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_IO_EXCEPTION,CtrlType.MSG_RESPONSE_RESTORE_HANDLER, ResponseCode.METHOD_RESTORE_APPLICATION, message);
		}
		catch(Exception e)
		{
			restoreAppError = true;
			message.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_UNKNOWN,CtrlType.MSG_RESPONSE_RESTORE_HANDLER, ResponseCode.METHOD_RESTORE_APPLICATION, message);
		}
		finally
		{
			message.clear();
		}
		
		if(restoreAppError == false)
		{
			message.put("message","success");
			callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_RESTORE_HANDLER, ResponseCode.METHOD_RESTORE_APPLICATION, message);
			message.clear();
			
			//close self create mApplicationHandler
			if(isOtherAppHandlerDeploy == false)
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
		catch(SecurityException e)
		{
			restoreFileError = true;
			message.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION,CtrlType.MSG_RESPONSE_RESTORE_HANDLER, ResponseCode.METHOD_RESTORE_SDCARD_FILE, message);
		
		}
		catch (FileNotFoundException e)
		{
			restoreFileError = true;
			message.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION,CtrlType.MSG_RESPONSE_RESTORE_HANDLER, ResponseCode.METHOD_RESTORE_SDCARD_FILE, message);
			
		}
		catch (IOException e)
		{
			restoreFileError = true;
			message.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_IO_EXCEPTION,CtrlType.MSG_RESPONSE_RESTORE_HANDLER, ResponseCode.METHOD_RESTORE_SDCARD_FILE, message);
		}
		catch (Exception e)
		{
			restoreFileError = true;
			message.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_RESTORE_HANDLER, ResponseCode.METHOD_RESTORE_SDCARD_FILE, message);
			
		}
		finally
		{
			message.clear();
		}
		if(restoreFileError ==false)
		{
			message.put("message", "success");
			super.callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_RESTORE_HANDLER, ResponseCode.METHOD_RESTORE_SDCARD_FILE, message);
			message.clear();
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

}
