package sdk.ideas.tracker;

import java.util.Collections;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Common;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.PermissionTable;
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
	private HashMap<String, String> message = new HashMap<String, String>();
	private HashMap<String, String> startTrackerParm = null;

	private Handler privateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			message.clear();
			if (MSG_RESPONSE == msg.what)
			{
				switch (msg.arg2)
				{
				case TAG_INIT:
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
						message.put("message", "error in transfer data to server ");
						callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
								ResponseCode.METHOLD_TRACKER, message);
						// debug use
						// message.put("message", (String) msg.obj);
						// callBackMessage(msg.arg1,
						// CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
						// ResponseCode.METHOLD_TRACKER,
						// message);

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
		message = new HashMap<String, String>();
		super.permissionCheck(PermissionTable.TRACKER);
	}

	/**
	 * 
	 * @param app_id
	 *            : User register APP from MORE console will get APP ID.
	 */
	public void startTracker(final String app_id)
	{
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
	{
		if (permissonCheck == false)
		{
			message.put("message",
					"use android.Manifest.permission denied, check permisson is existed in android manifest");
			callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
					ResponseCode.METHOLD_TRACKER, message);
			return;
		}
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
		availableTracker = false;
		message.put("message", "success");
		callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
				ResponseCode.METHOLD_STOP_TRACKER, message);
	}

	private void sendStartTrackerData()
	{
		try
		{
			getDeviceInfo();

			startTrackerParm.values().removeAll(Collections.singleton(""));
			startTrackerParm.values().removeAll(Collections.singleton(null));

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
			startTrackerParm.put("MAC", "");
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

		ID = (startTrackerParm.get("MAC") + startTrackerParm.get("PHONE") + startTrackerParm.get("APP_ID") + mailForID);
		startTrackerParm.put("ID", ID);

	}

	private void init()
	{
		Thread t = new Thread(new sendSocketRunnable(TAG_INIT));
		t.start();
	}

	private void setTrackerIPAndPort(final int nReturnCode, final String strContent)
	{
		message.clear();

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

				// debug use
				Logs.showTrace(startTrackerData.toString());
				Logs.showTrace(trackerData.toString());

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
			message.put("message", "Start Tracker Fail: Can't connect Server");
			callBackMessage(ResponseCode.ERR_NOT_INIT, CtrlType.MSG_RESPONSE_TRACKER_HANDLER,
					ResponseCode.METHOLD_START_TRACKER, message);
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
				CmpClient.init(Common.HOST_SERVICE_INIT, Common.PORT_SERVICE_INIT, Protocol.TYPE_MOBILE_TRACKER,
						respData, response);
				Logs.showTrace("init OK");
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

}
