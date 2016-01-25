package sdk.ideas.mdm.record;

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
	
	public RecordHandler(Context context, boolean readProfileFromServer)
	{
		this.mContext = context;
		readLocalInit = (!readProfileFromServer);
	}
	
	
	
	public void localRecord( boolean isInit)
	{
		recordApplication();
		recordInitFileListPathInSDCard(isInit);
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
			IOFileHandler.writeToInternalFile(mContext, MDMType.INIT_LOCAL_MDM_APP_PATH,
					ArrayListUtility.AppInfoConvertToArrayListString(applist));
		}
	
	}
	
	
	/**
	 * call it while the first time run
	 * 
	 * */
	private void recordInitFileListPathInSDCard(boolean isInit)
	{
		Thread recordManager = new Thread(new RecordFileData(this.mContext, isInit));
		recordManager.start();
	}
	
	
	
}
