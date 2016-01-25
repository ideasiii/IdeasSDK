package sdk.ideas.mdm.applist;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import sdk.ideas.common.Logs;

public class ApplicationList
{
	public static String[] getApplicationListasArray(Context mContext)
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

	public static ArrayList<AppInfo> getInstalledApps(Context mContext,boolean getSysPackages)
	{
		ArrayList<AppInfo> res = new ArrayList<AppInfo>();
		List<PackageInfo> packs = mContext.getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++)
		{
			PackageInfo p = packs.get(i);
			ApplicationInfo a = p.applicationInfo;
			// if ((!getSysPackages) && (p.versionName == null)) {
			if ((!getSysPackages) && ((a.flags & ApplicationInfo.FLAG_SYSTEM) == 1))
			{
				continue;
			}

			// newInfo.icon =
			// encodeImage(p.applicationInfo.loadIcon(context.getPackageManager()));
			res.add(new AppInfo(p.applicationInfo.loadLabel(mContext.getPackageManager()).toString(), p.packageName,
					p.versionName, p.versionCode));
		}
		return res;
	}

	public static String getAppNameFromPackage(Context mContext,String packageName)
	{
		boolean getSysPackages = true;
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
			}
		}
		return appName;
	}
	/*
	 * public String encodeImage(Drawable drawable) { Bitmap bitmap =
	 * ((BitmapDrawable) drawable).getBitmap(); // Bitmap bitmap = //
	 * ((BitmapDrawable)context.getResources().getDrawable(R.drawable.dot)).
	 * getBitmap(); ByteArrayOutputStream baos = new ByteArrayOutputStream();
	 * bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm is the //
	 * bitmap object byte[] b = baos.toByteArray(); String encodedImage =
	 * Base64.encodeToString(b, Base64.NO_WRAP);
	 * 
	 * // ByteArrayOutputStream stream = new ByteArrayOutputStream(); //
	 * bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); byte[] //
	 * bitMapData = stream.toByteArray();
	 * 
	 * // Log.e("BEFORE JSON : ", encodedImage); return encodedImage; }
	 */

	/**
	 * Creates a webclip on the device home screen
	 * 
	 * @param url
	 *            - Url should be passed in as a String - Title(Web app title)
	 *            should be passed in as a String
	 */
	/*
	 * public void createWebAppBookmark(String url, String title) { final Intent
	 * in = new Intent(); final Intent shortcutIntent = new
	 * Intent(Intent.ACTION_VIEW, Uri.parse(url)); long urlHash =
	 * url.hashCode(); long uniqueId = (urlHash << 32) |
	 * shortcutIntent.hashCode();
	 * shortcutIntent.putExtra(Browser.EXTRA_APPLICATION_ID,
	 * Long.toString(uniqueId)); in.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
	 * shortcutIntent); in.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
	 * in.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
	 * Intent.ShortcutIconResource.fromContext(context,
	 * R.drawable.ic_bookmark));
	 * in.setAction(context.getResources().getString(R.string.
	 * application_package_launcher_action)); // or
	 * in.setAction(Intent.ACTION_CREATE_SHORTCUT);
	 * 
	 * context.sendBroadcast(in); }
	 */

	public static class AppInfo
	{
		public String appName        = "";
		public String appPackageName = "";
		public String appVersionName = "";
		public int    appVersionCode = 0;

		public AppInfo(String appName, String appPackageName, String appVersionName, int appVersionCode)
		{
			this.appName = appName;
			this.appPackageName = appPackageName;
			this.appVersionName = appVersionName;
			this.appVersionCode = appVersionCode;
		}
		
		
		// public String icon = null;
		public void print()
		{
			Logs.showTrace("app: " +appName + " package: " + appPackageName + " version: " + appVersionName + "version code:  " + appVersionCode);
		}
	}

}
