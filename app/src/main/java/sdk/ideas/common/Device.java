package sdk.ideas.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class Device
{
	private Context			theContext		= null;
	private WindowManager	windowManager	= null;
	private Configuration	config			= null;

	public Device(Context context)
	{
		if (null != context)
		{
			theContext = context;
			windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			config = context.getResources().getConfiguration();
		}
	}

	protected void finalize()
	{
		windowManager = null;
		config = null;
	}

	public int getDisplayWidth()
	{
		int nWidth;
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		nWidth = metrics.widthPixels;
		metrics = null;
		return nWidth;
	}

	public int getDisplayHeight()
	{
		int nHeight;
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		nHeight = metrics.heightPixels;
		metrics = null;
		return nHeight;
	}

	public int getDeviceWidth()
	{
		DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
		int dpWidth = (int) (displayMetrics.widthPixels / displayMetrics.density + 0.5);
		return dpWidth;
	}

	public int getDeviceHeight()
	{
		DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
		int dpHeight = (int) (displayMetrics.heightPixels / displayMetrics.density + 0.5);
		return dpHeight;
	}

	public float getScaleSize()
	{
		float nDeviceWidth = (float) getDeviceWidth();
		float nDisplayWidth = (float) getDisplayWidth();
		float nScale = nDisplayWidth / nDeviceWidth;
		if (0 >= nScale)
		{
			nScale = 1;
		}
		return nScale;
	}

	public int getOrientation()
	{
		if (getDisplayWidth() > getDisplayHeight())
		{
			return Configuration.ORIENTATION_LANDSCAPE;
		}
		return Configuration.ORIENTATION_PORTRAIT;
	}

	/**
	 * Check display size Android Level 9 and up
	 */
	public boolean isLargeDisplaySize()
	{
		if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE)
		{
			return true;
		}
		return false;
	}

	/**
	 * get device physic size phone inche is less 6 inches
	 */
	public double getDisplayIncheSize()
	{
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		double screenInches = Math.sqrt(x + y);
		dm = null;
		return screenInches;
	}

	/**
	 * get device type, phone or tablet
	 */
	public int getDeviceType()
	{
		if (config.smallestScreenWidthDp >= Type.SMALLEST_SCREEN_WIDTH_DP)
		{
			return Type.DEVICE_TABLET;
		}
		return Type.DEVICE_PHONE;
	}

	
	public boolean checkInstallation(String packageName)
	{
		try
		{
			theContext.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e)
		{
			return false;
		}
	}

	public void installApp(Activity activity, String strAppPackageName)
	{
		Uri uri = Uri.parse("market://details?id=" + strAppPackageName);
		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		activity.startActivity(it);
	}

	public int getAndroidSdkVersion()
	{
		return android.os.Build.VERSION.SDK_INT;
	}

	public String getAndroidReleaseVersion()
	{
		return android.os.Build.VERSION.RELEASE;
	}

}
