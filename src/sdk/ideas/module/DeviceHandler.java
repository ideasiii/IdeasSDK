package sdk.ideas.module;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.util.SparseArray;
import android.view.Display;
import android.view.WindowManager;
import sdk.ideas.common.Logs;

@SuppressLint("NewApi")
public class DeviceHandler
{
	private Context			theContext	= null;
	private LocationManager	manager		= null;
	public static double	lat			= 0.0;
	public static double	lng			= 0.0;

	public static class AccountData
	{
		public String	strAccount;
		public String	strType;
	}

	public static class TeleData
	{
		// ������X
		public String	lineNumber;
		// ��� IMEI
		public String	imei;
		// ��� IMSI
		public String	imsi;
		// ������C���A
		public String	roamingStatus;
		// �q�H������O
		public String	country;
		// �q�H���q�N��
		public String	operator;
		// �q�H���q�W��
		public String	operatorName;
		// ��ʺ�������
		public String	networkType;
		// ��ʳq�T����
		public String	phoneType;
	}

	public DeviceHandler(Context context)
	{
		theContext = context;
	}

	public String getIMEI()
	{
		if (null == theContext)
			return null;
		// Context context = null;
		TelephonyManager telephonyManager = (TelephonyManager) theContext.getSystemService(Context.TELEPHONY_SERVICE);
		String strDeviceId = telephonyManager.getDeviceId();
		return strDeviceId;
	}

	public String getMacAddress()
	{
		if (null == theContext)
			return null;
		WifiManager wifiManager = (WifiManager) theContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getMacAddress();
	}

	public String getPhoneNumber()
	{
		if (null == theContext)
			return null;
		TelephonyManager telManager = (TelephonyManager) theContext.getSystemService(Context.TELEPHONY_SERVICE);
		return telManager.getLine1Number();
	}

	public void getTelecomInfo(TeleData teleData)
	{
		if (null == theContext || null == teleData)
			return;
		TelephonyManager telManager = (TelephonyManager) theContext.getSystemService(Context.TELEPHONY_SERVICE);

		// ������X
		teleData.lineNumber = telManager.getLine1Number();

		// ��� IMEI
		teleData.imei = telManager.getDeviceId();

		// ��� IMSI
		teleData.imsi = telManager.getSubscriberId();

		// ������C���A
		teleData.roamingStatus = telManager.isNetworkRoaming() ? "���C��" : "�D���C";

		// �q�H������O
		teleData.country = telManager.getNetworkCountryIso();

		// �q�H���q�N��
		teleData.operator = telManager.getNetworkOperator();

		// �q�H���q�W��
		teleData.operatorName = telManager.getNetworkOperatorName();

		// ��ʺ�������
		String[] networkTypeArray = { "UNKNOWN", "GPRS", "EDGE", "UMTS", "CDMA", "EVDO 0", "EVDO A", "1xRTT", "HSDPA",
				"HSUPA", "HSPA" };
		teleData.networkType = networkTypeArray[telManager.getNetworkType()];

		// ��ʳq�T����
		String[] phoneTypeArray = { "NONE", "GSM", "CDMA" };
		teleData.phoneType = phoneTypeArray[telManager.getPhoneType()];
	}

	public String getLocalIpAddress()
	{
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();

					// for getting IPV4 format
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		catch (Exception ex)
		{
			Logs.showTrace("IP Address" + ex.toString());
		}
		return null;
	}

	public int getAndroidSdkVersion()
	{
		int sdkInt;
		try
		{
			sdkInt = android.os.Build.VERSION.SDK_INT;
		}
		catch (NumberFormatException nfe)
		{
			sdkInt = 10000;
		}
		// Logs.showTrace("Android SDK: " + String.valueOf(sdkInt));
		return sdkInt;
	}

	public String getAndroidVersion()
	{
		String sdkRelease;
		try
		{
			sdkRelease = android.os.Build.VERSION.RELEASE;
		}
		catch (NumberFormatException nfe)
		{
			sdkRelease = "null";
		}
		// Logs.showTrace("Android" + String.valueOf(sdkRelease));
		return "Android" + sdkRelease;
	}

	public int getWidth()
	{
		int width = 0;
		width = getScreenResolution(theContext).x;
		return width;
	}

	public int getHeight()
	{
		int height = 0;
		height = getScreenResolution(theContext).y;
		return height;
	}

	public Point getScreenResolution(Context context)
	{
		WindowManager wm = (WindowManager) theContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public int getAccounts(SparseArray<AccountData> listAccount)
	{
		if (null == theContext || null == listAccount)
			return 0;
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(theContext).getAccounts();
		AccountData data = null;
		for (Account account : accounts)
		{
			// Logs.showTrace(account.name+" :-> "+account.type);
			if (emailPattern.matcher(account.name).matches())
			{
				data = new AccountData();
				data.strAccount = account.name;
				data.strType = account.type;
				listAccount.put(listAccount.size(), data);

				data = null;
			}
		}
		// return accounts.length;
		return listAccount.size();
	}

	public void getLocation()
	{
		manager = (LocationManager) theContext.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);

		// �]�m���ݭn����ޤ�V���
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);

		// �]�m���\���͸�O
		criteria.setCostAllowed(true);

		// �n�D�C�ӹq
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = manager.getBestProvider(criteria, false);

		Logs.showTrace("Location Provider:" + provider);
		Location location = manager.getLastKnownLocation(provider);

		// �Ĥ@����o�]�ƪ���m
		updateLocation(location);

		// ���n��ơA��ť��ƴ��
		manager.requestLocationUpdates(provider, 5000, 10, locationListener);
	}

	private final LocationListener locationListener = new LocationListener()
	{

		@Override
		public void onLocationChanged(Location location)
		{
			updateLocation(location);
			// Logs.showTrace(String.valueOf(DeviceHandler.lat)+"
			// "+String.valueOf(DeviceHandler.lng));
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider)
		{
			Logs.showTrace("Provider now is enabled..");
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			updateLocation(null);
			Logs.showTrace("Location Provider now is disabled..");
			final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			theContext.startActivity(intent);
		}

	};

	private void updateLocation(Location location)
	{
		if (location != null)
		{
			DeviceHandler.lat = location.getLatitude();
			DeviceHandler.lng = location.getLongitude();

		}
		else
		{
			Logs.showTrace("Location: location have some problem about GPS");
		}

		// Logs.showTrace("Location:" + latLng);
	}

}
