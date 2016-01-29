package sdk.ideas.mdm.record;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import android.content.Context;
import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.MDMType;
import sdk.ideas.mdm.applist.ApplicationList;
import sdk.ideas.mdm.applist.ApplicationList.AppInfo;

public class RecordHandler
{
	private Context mContext = null;
	private boolean recordSystemApplication = false;
	private boolean readLocalInit = true;
	private ReturnRecordAction listener = null;
	
	public RecordHandler(Context context, boolean readProfileFromServer)
	{
		this.mContext = context;
		readLocalInit = (!readProfileFromServer);
	}
	
	
	
	public void localRecord( boolean isInit)
	{
		recordApplication();
		try
		{
			recordInitFileListPathInSDCard(isInit,false);
		}
		catch (InterruptedException e)
		{
			if (null != listener)
				listener.returnRecordActionResult(e.toString());
		}
		Logs.showTrace("done");
	}
	
	 
	
	public void recordSystemApplication(boolean recordSysApp)
	{
		recordSystemApplication = recordSysApp;
	}
	

	/**
	 * record for installed App information
	 */
	private void recordApplication()
	{
		if (readLocalInit == true)
		{
			ArrayList<AppInfo> applist = ApplicationList.getInstalledApps(mContext, recordSystemApplication);
			for (int i = 0; i < applist.size(); i++)
				applist.get(i).print();
			try
			{
				IOFileHandler.writeToInternalFile(mContext, MDMType.INIT_LOCAL_MDM_APP_PATH,
						ArrayListUtility.AppInfoConvertToArrayListString(applist));
			}
			catch (FileNotFoundException e)
			{
				if (null != listener)
					listener.returnRecordActionResult(e.toString());
			}
			catch (IOException e)
			{
				if (null != listener)
					listener.returnRecordActionResult(e.toString());
			}
		}
	
	}
	
	
	/**
	 * call it while the first time run
	 * @throws InterruptedException 
	 * 
	 * */
	public void recordInitFileListPathInSDCard(boolean isInit, boolean waitForResult) throws InterruptedException
	{
		Thread recordManager = new Thread(new RecordFileData(this.mContext, isInit, listener));
		recordManager.start();
		if (waitForResult == true)
		{
			recordManager.join();
		}
	}
	public void setOnRecordAction(ReturnRecordAction listener)
	{
		this.listener = listener;
	}
	
	interface ReturnRecordAction
	{
		void returnRecordActionResult(String result);
	}
	
	
	
	
	
}
