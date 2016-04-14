package sdk.ideas.module;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;

public class MetaHandler
{
	/**
	 * read Application Meta-data
	 */
	public static String getMetaDataFromApplication(Context context, String name)
	{
		try
		{
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			return appInfo.metaData.getString(name);
		}
		catch (NameNotFoundException e)
		{
			return null;
		}
		catch (Exception e)
		{
			return null;
		}

	}

	/**
	 * read BroadCast Receiver Meta-data
	 */
	public static String readMetaDataFromBroadCastReceiver(ComponentName cn, Context context, String name)
	{
		try
		{
			ActivityInfo info = context.getPackageManager().getReceiverInfo(cn, PackageManager.GET_META_DATA);
			return info.metaData.getString(name);
		}
		catch (NameNotFoundException e)
		{
			return null;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * read Service Meta-data
	 */
	public static String readMetaDataFromService(ComponentName cn, Context context, String name)
	{
		try
		{
			ServiceInfo info = context.getPackageManager().getServiceInfo(cn, PackageManager.GET_META_DATA);
			return info.metaData.getString(name);
		}
		catch (NameNotFoundException e)
		{
			return null;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * read Activity Meta-data
	 */
	public static String readMetaDataFromActivity(Context context, String name)
	{
		try
		{
			ActivityInfo info = context.getPackageManager().getActivityInfo(((Activity) context).getComponentName(),
					PackageManager.GET_META_DATA);
			return info.metaData.getString(name);

		}
		catch (NameNotFoundException e)
		{
			return null;
		}
		catch (Exception e)
		{
			return null;
		}

	}

}
