package sdk.ideas.ctrl.applist;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class ApplicationList
{
	public static String[] getApplicationListasArray(Context mContext)
	{
		if (null != mContext)
		{
			PackageManager pm = mContext.getPackageManager();
			List<ApplicationInfo> apps = pm.getInstalledApplications(0);
			String applicationNames[] = new String[apps.size()];
			for (int j = 0; j < apps.size(); j++)
			{
				applicationNames[j] = apps.get(j).packageName;
			}
			return applicationNames;
		}
		return null;
	}

	public static ArrayList<AppInfo> getInstalledApps(Context mContext, boolean getSysPackages)
	{
		if (null != mContext)
		{
			ArrayList<AppInfo> res = new ArrayList<AppInfo>();
			List<PackageInfo> packs = mContext.getPackageManager().getInstalledPackages(0);
			for (int i = 0; i < packs.size(); i++)
			{
				PackageInfo mPackageInfo = packs.get(i);
				ApplicationInfo mApplicationInfo = mPackageInfo.applicationInfo;
				if ((!getSysPackages) && ((mApplicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1))
				{
					continue;
				}

				res.add(new AppInfo(mPackageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString(), mPackageInfo.packageName,
						mPackageInfo.versionName, mPackageInfo.versionCode));
			}
			return res;
		}
		return null;
	}

	public static boolean checkPackageExist(Context mContext, String packageName)
	{
		if (null != mContext && null != packageName)
		{
			boolean getSysPackages = false;
			List<PackageInfo> packs = mContext.getPackageManager().getInstalledPackages(0);
			for (int i = 0; i < packs.size(); i++)
			{
				PackageInfo p = packs.get(i);
				if ((!getSysPackages) && (p.versionName == null))
				{
					continue;
				}

				if (packageName.equals(p.packageName))
				{
					return true;
				}
			}
			return false;
		}
		return false;

	}

	public static String getAppNameFromPackage(Context mContext, String packageName)
	{
		if (null != mContext && null != packageName)
		{
			boolean getSysPackages = true;
			boolean isFound = false;
			String appName = "";
			List<PackageInfo> packs = mContext.getPackageManager().getInstalledPackages(0);
			for (int i = 0; i < packs.size(); i++)
			{
				PackageInfo p = packs.get(i);
				if ((!getSysPackages) && (p.versionName == null))
				{
					continue;
				}

				if (packageName.equals(p.packageName))
				{
					appName = p.applicationInfo.loadLabel(mContext.getPackageManager()).toString();
					isFound = true;
				}
			}
			if(isFound == true)
				return appName;
			else
				return null;
		}
		return null;
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

}
