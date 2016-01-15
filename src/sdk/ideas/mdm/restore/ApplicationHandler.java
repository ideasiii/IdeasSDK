
package sdk.ideas.mdm.restore;

import java.util.ArrayList;
import java.util.Collection;
import android.content.Context;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.MDMType;
import sdk.ideas.mdm.applist.ApplicationList;
import sdk.ideas.mdm.applist.ApplicationList.AppInfo;

public class ApplicationHandler
{
	private Context mContext = null;
	
	
	public ApplicationHandler(Context context)
	{
		mContext = context;
	}

	public void record(boolean recordSystemApplication, boolean isInit)
	{
		recordApplication(recordSystemApplication);
		recordInitFilePathInSDCard(isInit);
		Logs.showTrace("done");
	}
	
	
	
	
	/**
	 * record for installed App information
	 */
	private void recordApplication(boolean recordSystemApplication)
	{
		ApplicationList applicationList = new ApplicationList(mContext);
		ArrayList<AppInfo> applist = applicationList.getInstalledApps(recordSystemApplication);
		
		IOFileHandler.writeToInternalFile(mContext, MDMType.MDM_APP_INIT, IOFileHandler.AppInfoConvertToArrayListString(applist));
	
		/*	
		ArrayList<String > test = IOFileHandler.readFromInternalFile( mContext, MDMType.MDM_APP_INIT);
		for (int i = 0; i < test.size(); i++)
		{
			Logs.showTrace(test.get(i));
		}*/
	}
	
	
	/**
	 * call it while the first time run
	 * 
	 * */
	private void recordInitFilePathInSDCard(boolean isInit)
	{
		Thread recordManager = new Thread(new RecordAllDataInfo(this.mContext, isInit));
		recordManager.start();
	}
	
	/**
	 * call it while admin install new app or delete app
	 * 
	 * */
	

	
	public void updateRecordFilePathInSDCard()
	{
		
	}
	


	/**
	 * 
	 * */
	public void restore()
	{
		//for app to restore
		restoreApp(false);
		
	}
	
	private void restoreApp(boolean recordSystemApplication)
	{
		Collection<String > oldAppList = IOFileHandler.readFromInternalFile( mContext, MDMType.MDM_APP_INIT);
		Collection<String > reInstallList = new ArrayList<String >(oldAppList);
		
		ApplicationList applicationList = new ApplicationList(mContext);
		Collection<String> newApplist = IOFileHandler.AppInfoConvertToArrayListString(applicationList.getInstalledApps(recordSystemApplication));
		Collection<String > unInstallList = new ArrayList<String >(newApplist);
		
		//for re install
		reInstallList.removeAll(newApplist);
		
		//for uninstall
		unInstallList.removeAll(oldAppList);
		
		ArrayList<String> arrayUninstall = new ArrayList<String>( unInstallList); 
		
		for(int i=0;i<arrayUninstall.size();i++)
		{
			Logs.showTrace(arrayUninstall.get(i));
			unInstallApplication(arrayUninstall.get(i));
		}		
	}
	

	/**
	 * Installs an application to the device
	 * 
	 * @param url
	 *            - APK Url should be passed in as a String
	 */
	public void installApplication(String url,String path,String apkName)
	{
		InstallApp updator = new InstallApp();
		
		updator.setContext(mContext);
		updator.execute(url,path,apkName);
	}

	/**
	 * Uninstalls an application from the device
	 * 
	 * @param url
	 *            - Application package name should be passed in as a String
	 */
	public void unInstallApplication(String packageName)// Specific package Name
	{
		
		UninstallApp.unInstallApplication(mContext, packageName);
	}

}
