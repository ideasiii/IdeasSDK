package sdk.ideas.ctrl.wifi;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

public class WifiAdmin
{
	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	private List<ScanResult> mWifiList;
	private List<WifiConfiguration> mWifiConfiguration;
	WifiLock mWifiLock;

	public WifiAdmin(Context context)
	{
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiInfo = mWifiManager.getConnectionInfo();
		
		
		if (isConnected(mWifiManager, mWifiInfo) == false)
		{
			mWifiManager.setWifiEnabled(true);
		}
	}

	
	public void setWifiEnable()
	{
		mWifiManager.setWifiEnabled(true);
	}
	
	
	private static boolean isConnected(WifiManager wifiMgr, WifiInfo wifiInfo)
	{
		if (wifiMgr.isWifiEnabled())
		{ 
			// WiFi adapter is ON
			wifiInfo = wifiMgr.getConnectionInfo();
			if (wifiInfo.getNetworkId() == -1)
			{
				return false; // Not connected to an access-Point
			}
			return true; // Connected to an Access Point
		}
		else
		{
			return false; // WiFi adapter is OFF
		}
	}

	public int checkState()
	{
		return mWifiManager.getWifiState();
	}

	public void acquireWifiLock()
	{
		mWifiLock.acquire();
	}

	public void releaseWifiLock()
	{
		if (mWifiLock.isHeld())
		{
			mWifiLock.release();
		}
	}
/**
 * lockType: WifiManager.WIFI_MODE_FULL, WifiManager.WIFI_MODE_FULL_HIGH_PERF, WifiManager.WIFI_MODE_SCAN_ONLY
 * 
 * */
	
	public void creatWifiLock(String lockName, int lockType)
	{
		mWifiLock = mWifiManager.createWifiLock(lockType, lockName);
	}

	public List<WifiConfiguration> getConfiguration()
	{
		return mWifiConfiguration;
	}

	public void connectConfiguration(int index)
	{
		if (index > mWifiConfiguration.size())
		{
			return;
		}
		mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
	}

	public void startScan()
	{
		mWifiManager.startScan();
		mWifiList = mWifiManager.getScanResults();
		mWifiConfiguration = mWifiManager.getConfiguredNetworks();
	}

	public List<ScanResult> getWifiList()
	{
		return mWifiList;
	}

	public ArrayList<String> lookUpScanResult()
	{
		ArrayList<String > scanResult = new ArrayList<String >();
		for (int i = 0; i < mWifiList.size(); i++)
		{
			scanResult.add((mWifiList.get(i)).toString());
	
		}
		return scanResult;
	}

	public String getMacAddress()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	public String getBSSID()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	public int getIPAddress()
	{
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	public int getNetworkId()
	{
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	public String getWifiInfo()
	{
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}


	

	
	
	public boolean removeWifiConfigBySSID(String sSID) throws Exception 
	{
		WifiConfiguration tempConfig = isExsits(sSID);
		if (null != tempConfig)
		{
			if (mWifiManager.removeNetwork(tempConfig.networkId) == true && mWifiManager.saveConfiguration() == true)
			{
				return true;
			}
			return false;
		}
		return false;
		
	}
	
	
	
	public boolean addNetwork(WifiConfiguration wcg, boolean linkNow) throws Exception 
	{
		int wcgID = mWifiManager.addNetwork(wcg);
		boolean saveConfig = mWifiManager.saveConfiguration();
		
		if(linkNow == true)
		{
			mWifiManager.disconnect();
			mWifiManager.enableNetwork(wcgID, true);
			
			return mWifiManager.reconnect();
		}
		return saveConfig;
	}

	


	public WifiConfiguration createWifiInfo(String sSID, String password, int type) throws Exception
	{
		WifiConfiguration config = new WifiConfiguration();
		
		config.SSID = "\"" + sSID + "\"";

		removeWifiConfigBySSID(sSID);
		
		
		switch (type)
		{
		case 1: // WIFICIPHER_NOPASS
		{
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
			break;
		}
		case 2: // WIFICIPHER_WEP
		{
			config.hiddenSSID = true;
			config.status = WifiConfiguration.Status.DISABLED;
			config.priority = 40;
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

			config.wepKeys[0] = "\"" + password + "\""; // This is the WEP
														// Password
			config.wepTxKeyIndex = 0;
			break;
		}
		case 3: // WIFICIPHER_WPA
		{
			config.preSharedKey = "\"" + password + "\"";
			config.hiddenSSID = true;
			config.status = WifiConfiguration.Status.ENABLED;

			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

		}
			break;
		}
		return config;
	}

	public boolean disconnectWifi(int netId)
	{
		if(mWifiManager.disableNetwork(netId) == true && mWifiManager.disconnect() == true)
			return true;
		return false;
	}
	
	
	public WifiConfiguration isExsits(String SSID) throws Exception
	{
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		
		//if (null != existingConfigs)
		//{
			for (WifiConfiguration existingConfig : existingConfigs)
			{
				if (existingConfig.SSID.equals("\"" + SSID + "\""))
				{
					return existingConfig;
				}
			}
		//}
		return null;
	}

}