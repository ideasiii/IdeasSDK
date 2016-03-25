package sdk.ideas.ctrl.wifi;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.content.IntentFilter;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ReturnIntentAction;

public class WifiHandler extends BaseHandler implements ListenReceiverAction
{
	private static WifiAdmin mWifiAdmin = null;
	private static WifiReceiver mWifiRecevier = null;
	private static IntentFilter intentFilter = null;
	
	private static boolean isListenReceiverOn = false;
	private static boolean isWifiFlag = false;
	
	private HashMap<String, String> message = null;

	public WifiHandler(Context context)
	{
		super(context);
		message = new HashMap<String, String>();
		if (null == mWifiAdmin)
		{
			mWifiAdmin = new WifiAdmin(mContext);
		}

		if (null == intentFilter)
		{
			intentFilter = new IntentFilter();
			intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		}
		if (null == mWifiRecevier)
		{
			mWifiRecevier = new WifiReceiver();
			mWifiRecevier.setOnReceiverListener(new ReturnIntentAction()
			{
				@Override
				public void returnIntentAction(HashMap<String, String> action)
				{
					if (action.get("message").equals("disconnected"))
					{
						if (isWifiFlag == false)
						{
							Logs.showTrace("open wifi");
							mWifiAdmin.setWifiEnable();
							isWifiFlag = true;
						}
					}
					else
					{
						if (isWifiFlag == true)
						{
							isWifiFlag = false;
						}
					}
				}
			});
		}

	}

	public ArrayList<String> getScanResult()
	{
		mWifiAdmin.startScan();
		return mWifiAdmin.lookUpScanResult();
	}

	/**
	 * WifiLock Allows an application to keep the Wi-Fi radio awake lockType:
	 * WifiManager.WIFI_MODE_FULL, WifiManager.WIFI_MODE_FULL_HIGH_PERF,
	 * WifiManager.WIFI_MODE_SCAN_ONLY
	 */
	public void createWIFILockAndLockNow(String lockName, int lockType)
	{
		try
		{
			if (null == lockName)
			{
				message.put("message", "input String null OR input type error");
				super.callBackMessage(ResponseCode.ERR_ILLEGAL_ARGUMENT_EXCEPTION,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_WIFI_LOCK, message);
			}
			else
			{
				mWifiAdmin.creatWifiLock(lockName, lockType);
				mWifiAdmin.acquireWifiLock();

				message.put("message", "success");
				super.callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_WIFI_LOCK, message);

			}
		}
		catch (IllegalArgumentException e)
		{
			message.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_ILLEGAL_ARGUMENT_EXCEPTION,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_WIFI_LOCK, message);
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			super.callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_WIFI_LOCK,message);
		}
		finally
		{
			message.clear();
		}
	}

	public void releaseWIFILock()
	{

		mWifiAdmin.releaseWifiLock();
	}

	public void saveWIFIConfig(String ssid, String password, int type, boolean linkThisSSIDNow)
	{
		if (null == ssid || (type <= 0) || (type > 3))
		{
			message.put("message", "input String null OR input type error");
			super.callBackMessage(ResponseCode.ERR_ILLEGAL_ARGUMENT_EXCEPTION,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_SAVE_WIFI_CONFIG, message);
			message.clear();
		}
		else
		{
			boolean success = false;
			try
			{
				success = mWifiAdmin.addNetwork(mWifiAdmin.createWifiInfo(ssid, password, type), linkThisSSIDNow);
				if (success == true)
				{
					if(linkThisSSIDNow == false)
					{
					message.put("message", "success");
					super.callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_SAVE_WIFI_CONFIG, message);
					}
					else
					{
						Logs.showTrace("link = true and success");
					}
				}
				else
				{
					if(linkThisSSIDNow == false)
					{
						message.put("message", "save wificonfig operation fail to execute");
						super.callBackMessage(ResponseCode.ERR_WIFI_SAVE_WIFICONFIG_OPERATION_EXECUTE_FAIL,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_SAVE_WIFI_CONFIG, message);
					}
					else
					{
					//	message.put("message", "linl=k wificonfig operation fail to execute");
					//	super.callBackMessage(ResponseCode.ERR_WIFI_LINK_WIFICONFIG_OPERATION_EXECUTE_FAIL, message);
					//	super.returnRespose(CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_SAVE_WIFI_CONFIG);
					}
					
				}

			}
			catch (Exception e)
			{
				message.put("message", e.toString());
				super.callBackMessage(ResponseCode.ERR_UNKNOWN,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_SAVE_WIFI_CONFIG, message);
			}
			finally
			{
				message.clear();
			}

		}
	}

	public void removeWIFIConfig(String ssid)
	{
		if (null == ssid)
		{
			message.put("message", "input String null");
			super.callBackMessage(ResponseCode.ERR_ILLEGAL_ARGUMENT_EXCEPTION,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_REMOVE_WIFI_CONFIG, message);
			message.clear();
		}
		else
		{
			boolean success = false;
			try
			{
				success = mWifiAdmin.removeWifiConfigBySSID(ssid);
				if (success == true)
				{
					message.put("message", "success");
					super.callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_REMOVE_WIFI_CONFIG, message);
				}
				else
				{
					message.put("message", "not found need to remove ssid");
					super.callBackMessage(ResponseCode.ERR_WIFI_SSID_NOT_FOUND,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_REMOVE_WIFI_CONFIG, message);
					Logs.showTrace("removeWIFIConfig : false");
				}
			}
			catch (Exception e)
			{
				message.put("message", e.toString());
				super.callBackMessage(ResponseCode.ERR_UNKNOWN,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_REMOVE_WIFI_CONFIG, message);
			}
			finally
			{
				message.clear();
			}
		}
	}

	public void disconnectWIFI(String ssid)
	{
		if (null == ssid)
		{
			message.put("message", "input String null");
			super.callBackMessage(ResponseCode.ERR_ILLEGAL_ARGUMENT_EXCEPTION,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_DISCONNECT_SSID, message);
			message.clear();
			return;
		}
		else
		{
			try
			{
				boolean success = false;

				success = mWifiAdmin.disconnectWifi(mWifiAdmin.isExsits(ssid).networkId);
				if (success == true)
				{
					message.put("message", "success");
					super.callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_DISCONNECT_SSID, message);
				}
				else
				{
					/////
					Logs.showTrace("disconnectWIFI: false");
				}

			}
			catch(NullPointerException e)
			{
				message.put("message", e.toString());
				super.callBackMessage(ResponseCode.ERR_WIFI_SSID_NOT_FOUND,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_DISCONNECT_SSID, message);
			}
			catch (Exception e)
			{
				message.put("message", e.toString());
				super.callBackMessage(ResponseCode.ERR_UNKNOWN,CtrlType.MSG_RESPONSE_WIFI_HANDLER, ResponseCode.METHOD_DISCONNECT_SSID, message);
			}
			finally
			{
				message.clear();
			}
		}
	}

	@Override
	public void startListenAction()
	{
		if (isListenReceiverOn == false)
		{
			mContext.registerReceiver(mWifiRecevier, intentFilter);
			isListenReceiverOn = true;
		}
	}

	@Override
	public void stopListenAction()
	{
		if (isListenReceiverOn == true)
		{
			mContext.unregisterReceiver(mWifiRecevier);
			isListenReceiverOn = false;
		}
	}

}
