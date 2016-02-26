package sdk.ideas.ctrl.record;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.os.Environment;
import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ArrayListUtility.FileData;
import sdk.ideas.ctrl.applist.ApplicationList;
import sdk.ideas.ctrl.applist.ApplicationList.AppInfo;

public class RecordHandler extends BaseHandler
{
	private boolean isRecordSystemApp = false;

	private HashMap<String, String> message = null;

	private boolean isWriteFile = false;

	private ArrayList<String> filePathData = null;
	private ArrayList<String> appData = null;

	private boolean isWriteInternalMemory = true;

	private String externalPath = null;
	private String appFileName = null;
	private String filePathName = null;

	private boolean isAppRecordOK = true;
	private boolean isFilePathRecordOK = true;
	
	//default all true
	private boolean restoreAppFlag = true;
	private boolean restoreFileFlag = true;

	private ArrayList<String> particularPathScan = null;

	public RecordHandler(Context context)
	{
		super(context);
		message = new HashMap<String, String>();
	}

	public void setWritePathAndFileName(boolean isWriteInternalMemory, String externalPath, String appFileName,
			String filePathName)
	{
		this.isWriteInternalMemory = isWriteInternalMemory;

		if (isWriteInternalMemory == false)
		{
			this.externalPath = externalPath;
		}
		this.appFileName = appFileName;
		this.filePathName = filePathName;
		this.isWriteFile = true;

	}
	
	public void setRecordFlag(boolean restoreAppFlag ,boolean restoreFileFlag)
	{
		this.restoreAppFlag = restoreAppFlag;
		this.restoreFileFlag = restoreFileFlag;
	}

	public void setParticularRecordPath(ArrayList<String> particularPathScan, boolean isAbsolutePath)
	{

		if (isAbsolutePath == false)
		{
			this.particularPathScan = new ArrayList<String>();

			for (int i = 0; i < particularPathScan.size(); i++)
			{
				this.particularPathScan
						.add(IOFileHandler.getExternalStorageDirectory() + "/" + particularPathScan.get(i));
			}
		}
		else
		{
			this.particularPathScan = particularPathScan;
		}
	}

	public void record()
	{
		if (restoreFileFlag == true)
		{
			recordFilePathInSDCard(true);
			if (isFilePathRecordOK)
			{
				message.put("message", "success");
				setResponseMessage(ResponseCode.ERR_SUCCESS, message);
				returnRespose(CtrlType.MSG_RESPONSE_RECORD_HANDLER, ResponseCode.METHOD_RECORD_SDCARD_FILE);
				message.clear();
			}
		}
		if (restoreAppFlag == true)
		{
			appData = recordApplication();
			if (isAppRecordOK)
			{
				message.put("message", "success");
				setResponseMessage(ResponseCode.ERR_SUCCESS, message);
				returnRespose(CtrlType.MSG_RESPONSE_RECORD_HANDLER, ResponseCode.METHOD_RECORD_APPLICATION);
				message.clear();
			}
		}
	}

	public ArrayList<String> getAppData()
	{
		return appData;
	}

	public ArrayList<String> getFilePathData()
	{
		return filePathData;
	}

	public void setRecordSystemApplication(boolean isRecordSystemApp)
	{
		this.isRecordSystemApp = isRecordSystemApp;
	}

	/**
	 * record for installed App information
	 * 
	 */
	private ArrayList<String> recordApplication()
	{
		ArrayList<AppInfo> appList = ApplicationList.getInstalledApps(mContext, isRecordSystemApp);
		ArrayList<String> appListString = ArrayListUtility.AppInfoConvertToArrayListString(appList);

		// using for debugging
		// for (int i = 0; i < applist.size(); i++)
		// applist.get(i).print();

		if (isWriteFile == true)
		{
			try
			{
				if (isWriteInternalMemory == true)
				{
					IOFileHandler.writeToInternalFile(mContext, appFileName, appListString);
				}
				else
				{
					
					IOFileHandler.writeToExternalFile(externalPath, appFileName, appListString);
				}
			}
			catch (FileNotFoundException e)
			{
				this.isAppRecordOK = false;
				message.put("message", e.toString());
				setResponseMessage(ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION, message);
				returnRespose(CtrlType.MSG_RESPONSE_RECORD_HANDLER, ResponseCode.METHOD_RECORD_APPLICATION);
			}
			// for incase
			catch (Exception e)
			{
				this.isAppRecordOK = false;
				message.put("message", e.toString());
				setResponseMessage(ResponseCode.ERR_UNKNOWN, message);
				returnRespose(CtrlType.MSG_RESPONSE_RECORD_HANDLER, ResponseCode.METHOD_RECORD_APPLICATION);
			}
			finally
			{

				message.clear();
			}
		}
		return appListString;

	}

	private void recordFilePathInSDCard(boolean isWaitForResult)
	{
		Thread recordManager = null;
		if (null == particularPathScan)
		{
			recordManager = new Thread(new RecordFileData());
		}
		else
		{
			recordManager = new Thread(new RecordFileData(particularPathScan));
		}
		recordManager.start();
		try
		{
			if (isWaitForResult == true)
			{
				recordManager.join();
			}
		}
		catch (InterruptedException e)
		{
			this.isFilePathRecordOK = false;
			message.put("message", e.toString());
			setResponseMessage(ResponseCode.ERR_INTERRUPTED_EXCEPTION, message);
			returnRespose(CtrlType.MSG_RESPONSE_RECORD_HANDLER, ResponseCode.METHOD_RECORD_SDCARD_FILE);
			message.clear();
		}
	}

	public class RecordFileData implements Runnable
	{
		private ArrayList<String> particularlyPath = null;
		private ArrayList<FileData> fileData = null;
		private boolean scanParticularPath = false;

		public RecordFileData()
		{
			fileData = new ArrayList<FileData>();
		}

		public RecordFileData(ArrayList<String> particularlyPath)
		{
			fileData = new ArrayList<FileData>();
			setScanPath(particularlyPath);
		}

		@Override
		public void run()
		{
			try
			{
				if (scanParticularPath == true)
				{
					fileData = recordFilePath(particularlyPath);
				}
				else
				{
					fileData = recordAllFilePath();
				}
				filePathData = ArrayListUtility.FileDataConvertToArrayListString(fileData);
				if (isWriteFile == true)
				{

					if (isWriteInternalMemory == true)
					{
						IOFileHandler.writeToInternalFile(mContext, filePathName, filePathData);
					}
					else
					{
						if (restoreAppFlag == true)
						{
							filePathData.add(Environment.getExternalStorageDirectory().toString() + "/" + externalPath
									+ appFileName);
						}
						filePathData.add(Environment.getExternalStorageDirectory().toString() + "/" + externalPath
								+ filePathName);
						IOFileHandler.writeToExternalFile(externalPath, filePathName, filePathData);
					}

				}
			}
			catch (FileNotFoundException e)
			{
				isFilePathRecordOK = false;
				message.put("message", e.toString());
				setResponseMessage(ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION, message);
				returnRespose(CtrlType.MSG_RESPONSE_RECORD_HANDLER, ResponseCode.METHOD_RECORD_SDCARD_FILE);
			}
			catch (IOException e)
			{
				isFilePathRecordOK = false;
				message.put("message", e.toString());
				setResponseMessage(ResponseCode.ERR_IO_EXCEPTION, message);
				returnRespose(CtrlType.MSG_RESPONSE_RECORD_HANDLER, ResponseCode.METHOD_RECORD_SDCARD_FILE);
			}
			// in case
			catch (Exception e)
			{
				isFilePathRecordOK = false;
				message.put("message", e.toString());
				setResponseMessage(ResponseCode.ERR_UNKNOWN, message);
				returnRespose(CtrlType.MSG_RESPONSE_RECORD_HANDLER, ResponseCode.METHOD_RECORD_SDCARD_FILE);
			}
			finally
			{
				message.clear();
			}
		}

		public void setScanPath(ArrayList<String> particularlyPath)
		{
			if (null != particularlyPath)
			{
				this.particularlyPath = particularlyPath;
				scanParticularPath = true;
			}
		}
	}

	/**
	 * record all file data info
	 */
	private ArrayList<FileData> recordAllFilePath()
	{

		ArrayList<FileData> datas = new ArrayList<FileData>();
		if (IOFileHandler.isExternalStorageReadable())
		{
			String path = Environment.getExternalStorageDirectory().toString();
			scanDIR(datas, path);
			// Logs.showTrace("total data numbers" +
			// String.valueOf(datas.size()));
		}
		else
		{
			isFilePathRecordOK = false;
			message.put("message", "external storage is unavailable to read");
			setResponseMessage(ResponseCode.ERR_UNREADABLE_EXTERNAL_STORAGE, message);
			returnRespose(CtrlType.MSG_RESPONSE_RECORD_HANDLER, ResponseCode.METHOD_RECORD_SDCARD_FILE);
			message.clear();
		}
		return datas;
	}

	private ArrayList<FileData> recordFilePath(ArrayList<String> particularlyPath)
	{
		ArrayList<FileData> datas = new ArrayList<FileData>();
		if (IOFileHandler.isExternalStorageReadable())
		{
			for (int i = 0; i < particularlyPath.size(); i++)
			{
				scanDIR(datas, particularlyPath.get(i));
			}
		}
		else
		{
			isFilePathRecordOK = false;
			message.put("message", "external storage is unavailable to read");
			setResponseMessage(ResponseCode.ERR_UNREADABLE_EXTERNAL_STORAGE, message);
			returnRespose(CtrlType.MSG_RESPONSE_RECORD_HANDLER, ResponseCode.METHOD_RECORD_SDCARD_FILE);
			message.clear();
		}
		return datas;
	}

	private void scanDIR(ArrayList<FileData> datas, String path)
	{
		File f = new File(path);
		File file[] = f.listFiles();
		for (int i = 0; i < file.length; i++)
		{
			if (file[i].isDirectory() == true)
			{
				datas.add(new FileData(file[i].getName(), file[i].getPath(), true));
				scanDIR(datas, file[i].getPath());
			}
			else
			{
				datas.add(new FileData(file[i].getName(), file[i].getPath(), false));
			}
		}
	}

}
