package sdk.ideas.common;

import android.util.Log;

public abstract class CommonClass
{
	
	public static class AppData
	{
		public String downloadPath = "";
		public String packageName = "";
		public int appID;
		public String fileName = "";

		public AppData(String packageName, String downloadPath, String fileName, int appID)
		{
			this.downloadPath = downloadPath;
			this.packageName = packageName;
			this.appID = appID;
			this.fileName = fileName;
		}
	}
	public static class AppInfo
	{
		public String appName = "";
		public String appPackageName = "";
		public String appVersionName = "";
		public int appVersionCode = 0;

		public AppInfo(String appName, String appPackageName, String appVersionName, int appVersionCode)
		{
			this.appName = appName;
			this.appPackageName = appPackageName;
			this.appVersionName = appVersionName;
			this.appVersionCode = appVersionCode;
		}

		public void print()
		{
			Log.d("app info", " app: " + appName + " | package: " + appPackageName + " | version: " + appVersionName
					+ " | version code:  " + appVersionCode);
		}
	}
	
	public static class BluetoothDeviceLinkableDevice 
	{
		public String address = null;
		public String name = null;
		
		public BluetoothDeviceLinkableDevice(String name,String address)
		{
			this.address = address;
			this.name = name;
		}
		
		
		
		
	}

}
