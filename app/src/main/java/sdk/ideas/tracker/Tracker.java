package sdk.ideas.tracker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Common;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.GenerateUUID;
import sdk.ideas.common.Logs;
import sdk.ideas.common.Protocol;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.StringUtility;
import sdk.ideas.module.CmpClient;
import sdk.ideas.module.DeviceHandler;
import sdk.ideas.module.DeviceHandler.AccountData;

public class Tracker extends BaseHandler
{
	// inside use handle message
	private final int TAG_INIT = 1025;
	private final int TAG_STARTTRACKER = 1027;
	private final int TAG_TRACKER = 1028;
	private static final int MSG_RESPONSE = 1026;

	private DeviceHandler deviceHandler = null;
	private boolean availableTracker = false;

	// MAC + Phone + APP ID +Email(facebook first)
	private String ID = "";
	// private
	private HashMap<String, String> startTrackerParm = null;

	private Handler privateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			HashMap<String, String> message = new HashMap<String, String>();
			if (MSG_RESPONSE == msg.what)
			{
				switch (msg.arg2)
				{
				case TAG_INIT:
					Logs.showTrace("***INIT DATA:"+((String) msg.obj) );
					
					setTrackerIPAndPort(msg.arg1, (String) msg.obj);
					sendStartTrackerData();
					break;

				case TAG_STARTTRACKER:
					if (msg.arg1 == ResponseCode.ERR_SUCCESS)
					{
						availableTracker = true;
						message.put("message", "success");
						callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
								ResponseCode.METHOLD_START_TRACKER, message);
					}
					// CMPClient ERROR
					else if (msg.arg1 <= ResponseCode.ERR_MAX)
					{
						message.put("message", "error in transfer data to server ");
						callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
								ResponseCode.METHOLD_START_TRACKER, message);
					}
					else
					{
						message.put("message", (String) msg.obj);
						callBackMessage(msg.arg1, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
								ResponseCode.METHOLD_START_TRACKER, message);
					}
					break;

				case TAG_TRACKER:
					if (msg.arg1 == ResponseCode.ERR_SUCCESS)
					{
						message.put("message", "success");
						callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
								ResponseCode.METHOLD_TRACKER, message);
					}
					// CMPClient ERROR
					else if (msg.arg1 <= ResponseCode.ERR_MAX)
					{
						// message.put("message", "error in transfer data to
						// server ");
						// callBackMessage(ResponseCode.ERR_IO_EXCEPTION,
						// CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
						// ResponseCode.METHOLD_TRACKER, message);
						// debug use
						message.put("message", (String) msg.obj);
						callBackMessage(msg.arg1, CtrlType.MSG_RESPONSE_TRACKER_HANDLER, ResponseCode.METHOLD_TRACKER,
								message);

					}
					else
					{
						message.put("message", (String) msg.obj);
						callBackMessage(msg.arg1, CtrlType.MSG_RESPONSE_TRACKER_HANDLER, ResponseCode.METHOLD_TRACKER,
								message);
					}
					break;

				}

			}
			message.clear();
		}
	};

	public Tracker(Context context)
	{
		super(context);

	}

	/**
	 * 
	 * @param app_id
	 *            : User register APP from MORE console will get APP ID.
	 */
	public void startTracker(final String app_id)
	{
		HashMap<String, String> message = new HashMap<String, String>();
		try
		{
			startTracker(app_id, null, null, null);

		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
					ResponseCode.METHOLD_START_TRACKER, message);
		}
		finally
		{
			message.clear();
		}
	}

	public void startTracker(final String app_id, final String fb_id, final String fb_name, final String fb_email)
	{/*
		 * if(super.getAppIDVaild() == false) { message.put("message",
		 * "app id is not vaild");
		 * callBackMessage(ResponseCode.ERR_SDK_APP_ID_INVAILD,
		 * CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
		 * ResponseCode.METHOLD_START_TRACKER, message); return; }
		 */

		// add in sdk tracker
		// sdkTrackerMessage("tracker","startTracker");

		/*
		 * if (permissonCheck == false) { message.put("message",
		 * "use android.Manifest.permission denied, check permisson is existed in android manifest"
		 * ); callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION,
		 * CtrlType.MSG_RESPONSE_TRACKER_HANDLER, ResponseCode.METHOLD_TRACKER,
		 * message); return; }
		 */

		/*
		 * if(availableTracker == true) { message.clear();
		 * message.put("message", "already call this method ");
		 * callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION,
		 * CtrlType.MSG_RESPONSE_TRACKER_HANDLER, ResponseCode.METHOLD_TRACKER,
		 * message); return; }
		 */

		startTrackerParm = new HashMap<String, String>();

		if (StringUtility.isValid(app_id))
		{
			startTrackerParm.put("APP_ID", app_id);
		}
		else
		{
			// 沒有APP ID就勉強用package name
			startTrackerParm.put("APP_ID", mContext.getPackageName());
		}

		if (StringUtility.isValid(fb_id))
		{
			startTrackerParm.put("FB_ID", fb_id);
		}

		if (StringUtility.isValid(fb_name))
		{
			startTrackerParm.put("FB_NAME", fb_name);
		}

		if (StringUtility.isValid(fb_email))
		{
			startTrackerParm.put("FB_EMAIL", fb_email);
		}

		init();
	}

	/**
	 * 
	 * @param parm
	 *            : HashMap
	 */
	public void track(HashMap<String, String> parm)
	{
		HashMap<String, String> message = new HashMap<String, String>();

		/*
		 * if(super.getAppIDVaild() == false) { message.put("message",
		 * "app id is not vaild");
		 * callBackMessage(ResponseCode.ERR_SDK_APP_ID_INVAILD,
		 * CtrlType.MSG_RESPONSE_TRACKER_HANDLER, ResponseCode.METHOLD_TRACKER,
		 * message); return; }
		 */

		try
		{

			if (availableTracker == false)
			{
				message.put("message", "can not start tracker cause startTarcker fail or stopTracker run");
				callBackMessage(ResponseCode.ERR_NOT_INIT, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
						ResponseCode.METHOLD_TRACKER, message);
				return;
			}
			else if (null == parm)
			{
				message.put("message", "tracker parm is null");
				callBackMessage(ResponseCode.ERR_ILLEGAL_ARGUMENT_EXCEPTION, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
						ResponseCode.METHOLD_TRACKER, message);
				return;
			}

			// add in sdk tracker
			// super.sdkTrackerMessage("tracker", "tracker");

			parm.put("ID", this.ID);

			if (DeviceHandler.lat != -1.0 && DeviceHandler.lng != -1.0)
			{
				parm.put("LOCATION", (String.valueOf(DeviceHandler.lat) + "," + String.valueOf(DeviceHandler.lng)));
			}
			if (!StringUtility.isValid(parm.get("TYPE")))
			{
				parm.put("TYPE", "0");
			}

			parm.values().removeAll(Collections.singleton(""));
			parm.values().removeAll(Collections.singleton(null));

			JSONObject jsonParm = new JSONObject(parm);

			this.sendEvent(jsonParm.toString(), TAG_TRACKER);

			parm.clear();
			parm = null;
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
					ResponseCode.METHOLD_TRACKER, message);
		}
		finally
		{
			message.clear();
		}

	}

	public void stopTracker()
	{
		HashMap<String, String> message = new HashMap<String, String>();
		/*
		 * if(super.getAppIDVaild() == false) { message.put("message",
		 * "app id is not vaild");
		 * callBackMessage(ResponseCode.ERR_SDK_APP_ID_INVAILD,
		 * CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
		 * ResponseCode.METHOLD_STOP_TRACKER, message); return; }
		 */

		// add in sdk tracker
		// super.sdkTrackerMessage("tracker", "stopTracker");

		availableTracker = false;

		message.put("message", "success");
		callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
				ResponseCode.METHOLD_STOP_TRACKER, message);
	}

	private void sendStartTrackerData()
	{
		HashMap<String, String> message = new HashMap<String, String>();
		try
		{
			getDeviceInfo();

			startTrackerParm.values().removeAll(Collections.singleton(""));
			startTrackerParm.values().removeAll(Collections.singleton(null));

			// debug using START
			// Logs.showTrace("start tracker data: " + startTrackerParm);
			// debug using END

			JSONObject jsonParm = new JSONObject(startTrackerParm);

			this.sendEvent(jsonParm.toString(), TAG_STARTTRACKER);

			startTrackerParm.clear();
			startTrackerParm = null;
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
					ResponseCode.METHOLD_START_TRACKER, message);
		}
		finally
		{
			message.clear();
		}
	}

	private void getDeviceInfo()
	{

		deviceHandler = new DeviceHandler(mContext);

		deviceHandler.getLocation();

		// joe fix null bug in 2016/03/02 begin
		String macAddress = deviceHandler.getMacAddress();
		if (macAddress == null)
		{
			startTrackerParm.put("MAC", "020000000000");
		}
		else
		{
			startTrackerParm.put("MAC", macAddress.replaceAll(":", ""));
		}

		String androidVersion = deviceHandler.getAndroidVersion();
		if (androidVersion == null)
		{
			startTrackerParm.put("OS", "");
		}
		else
		{
			startTrackerParm.put("OS", androidVersion);
		}

		String phone = deviceHandler.getPhoneNumber();
		if (phone == null)
		{
			startTrackerParm.put("PHONE", "");
		}
		else
		{
			startTrackerParm.put("PHONE", phone);
		}
		// joe fix null bug in 2016/03/02 end

		SparseArray<AccountData> listAccount = new SparseArray<AccountData>();

		int num = deviceHandler.getAccounts(listAccount);

		String mailForID = "";
		boolean fbAccountExist = false;
		for (int i = 0; i < num; i++)
		{
			if (listAccount.valueAt(i).strType.contains("facebook"))
			{
				startTrackerParm.put("FB_ACCOUNT", listAccount.valueAt(i).strAccount);
				fbAccountExist = true;
				mailForID = listAccount.valueAt(i).strAccount;
			}
			else if (listAccount.valueAt(i).strAccount.contains("gmail")
					&& listAccount.valueAt(i).strType.contains("google"))
			{
				startTrackerParm.put("G_ACCOUNT", listAccount.valueAt(i).strAccount);
				if (fbAccountExist == false)
				{
					mailForID = listAccount.valueAt(i).strAccount;
				}
			}
			else if (listAccount.valueAt(i).strType.contains("twitter"))
			{
				startTrackerParm.put("T_ACCOUNT", listAccount.valueAt(i).strAccount);
			}
		}

		// joe fix MAC BUG 2017/01/03 START

		// will get MAC = 020000000000 cause by Android 6.0, 6.0.1 and might
		// later
		// version
		if (!startTrackerParm.get("MAC").equals("020000000000"))
		{
			ID = (startTrackerParm.get("MAC") + startTrackerParm.get("PHONE") + startTrackerParm.get("APP_ID")
					+ mailForID);
		}
		else if (android.os.Build.VERSION.SDK_INT >= 9)
		{
			ID = (android.os.Build.SERIAL + startTrackerParm.get("PHONE") + startTrackerParm.get("APP_ID") + mailForID);
		}
		else
		{
			try
			{
				String iii_sdk_key = getSharedPreferencesValue("III_SDK_KEY");
				if (null == iii_sdk_key)
				{
					iii_sdk_key = GenerateUUID.uuIDRandom().toUpperCase(Locale.UK);
					saveInSharedPref("III_SDK_KEY", iii_sdk_key);

				}
				ID = (iii_sdk_key + startTrackerParm.get("PHONE") + startTrackerParm.get("APP_ID") + mailForID);

			}
			catch (Exception e)
			{
				Log.d("III Tracker", "GenerateUUID Exception: " + e.toString());
			}

		}
		// joe fix MAC BUG 2017/01/03 END

		startTrackerParm.put("ID", ID);

	}

	private void init()
	{
		Thread t = new Thread(new sendSocketRunnable(TAG_INIT));
		t.start();
	}

	private void setTrackerIPAndPort(final int nReturnCode, final String strContent)
	{
		HashMap<String, String> message = new HashMap<String, String>();
		if (ResponseCode.ERR_SUCCESS == nReturnCode && strContent.length() > 0)
		{
			try
			{
				JSONObject dataArray = new JSONObject(strContent);
				JSONObject startTrackerData = null, trackerData = null;

				if (((JSONObject) dataArray.getJSONArray("server").get(0)).get("id").equals(0))
				{
					startTrackerData = ((JSONObject) dataArray.getJSONArray("server").get(0));
					trackerData = ((JSONObject) dataArray.getJSONArray("server").get(1));
				}
				else
				{
					startTrackerData = ((JSONObject) dataArray.getJSONArray("server").get(1));
					trackerData = ((JSONObject) dataArray.getJSONArray("server").get(0));
				}

				// debug use START
				// show INIT startTracker and Tracker IP and Port
				// Logs.showTrace(startTrackerData.toString());
				// Logs.showTrace(trackerData.toString());
				// debug use END
				Common.URL_TRACKER_STARTTRACKER = startTrackerData.getString("ip");
				Common.PORT_TRACKER_STARTTRACKER = startTrackerData.getInt("port");

				Common.URL_TRACKER_TRACKER = trackerData.getString("ip");
				Common.PORT_TRACKER_TRACKER = trackerData.getInt("port");
			}
			catch (JSONException e)
			{
				message.put("message", "Start Tracker(INIT) Fail: " + e.toString());
				callBackMessage(ResponseCode.ERR_NOT_INIT, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
						ResponseCode.METHOLD_START_TRACKER, message);
			}
			catch (Exception e)
			{
				message.put("message", "Start Tracker(INIT) Fail: " + e.toString());
				callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
						ResponseCode.METHOLD_START_TRACKER, message);
			}

		}
		else
		{
			//joe fix bug 2017/01/05 START
			//message.put("message", "Start Tracker Fail: Can't connect Server");
			//callBackMessage(ResponseCode.ERR_NOT_INIT, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
			//		ResponseCode.METHOLD_START_TRACKER, message);
			//joe fix bug 2017/01/05 END
		}

	}

	private void sendEvent(String jsonString, final int nTag)
	{
		Thread t = new Thread(new sendSocketRunnable(jsonString, nTag));
		t.start();
	}

	private int sendSocketData(String parm, HashMap<String, String> respData, CmpClient.Response response,
			final int mnfag)
	{
		if (StringUtility.isValid(parm) == false && mnfag != TAG_INIT)
		{
			return -1;
		}
		try
		{
			if (mnfag == TAG_TRACKER)
			{
				CmpClient.accessLogRequest(Common.URL_TRACKER_TRACKER, Common.PORT_TRACKER_TRACKER,
						Protocol.TYPE_MOBILE_TRACKER, parm, respData, response);
			}
			else if (mnfag == TAG_INIT)
			{
				// joe fix bug 2017/01/04 START
				String ip = Common.HOST_SERVICE_INIT;
				int port = Common.PORT_SERVICE_INIT;

				if (!CmpClient.isReachableByTcp(ip, port, 3000))
				{
					ip = Common.HOST_SERVICE_INIT_BACKUP;
					port = Common.PORT_SERVICE_INIT_BACKUP;
				}
				//Logs.showTrace("[Tracker]INIT IP:" + ip + " Port:" + String.valueOf(port));
				// joe fix bug 2017/01/04 END

				CmpClient.init(ip, port, Protocol.TYPE_MOBILE_TRACKER, respData, response);

				// Logs.showTrace("init OK");
			}
			else if (mnfag == TAG_STARTTRACKER)
			{
				CmpClient.SignUpRequest(Common.URL_TRACKER_STARTTRACKER, Common.PORT_TRACKER_STARTTRACKER,
						Protocol.TYPE_MOBILE_TRACKER, parm, respData, response);
			}
		}
		catch (Exception e)
		{
			Logs.showTrace("Exception:" + e.toString());
		}
		return response.mnCode;
	}

	class sendSocketRunnable implements Runnable
	{

		private int mnTag = 0;
		private String parm = null;

		@Override
		public void run()
		{
			HashMap<String, String> respData = new HashMap<String, String>();
			CmpClient.Response response = new CmpClient.Response();
			sendSocketData(parm, respData, response, mnTag);
			Common.postMessage(privateHandler, MSG_RESPONSE, response.mnCode, mnTag, response.mstrContent);
		}

		public sendSocketRunnable(String parm, final int nTag)
		{
			this.mnTag = nTag;
			this.parm = parm;
		}

		public sendSocketRunnable(final int nTag)
		{
			this.mnTag = nTag;
		}

	}

	public String getSharedPreferencesValue(String key)
	{
		SharedPreferences prefs = getSharedPreferences();

		return prefs.getString(key, null);
	}

	public void releaseSharedPreferences()
	{
		Editor editor = getSharedPreferences().edit();
		editor.clear();
		editor.apply();
	}

	public SharedPreferences getSharedPreferences()
	{
		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public void saveInSharedPref(String key, String value)
	{
		Logs.showTrace("Save Pref: Key: " + key + " value: " + value);
		Editor editor = getSharedPreferences().edit();
		editor.putString(key, value);

		if (!editor.commit())
		{
			Logs.showError("error to write SharedPref");
		}
	}

}
