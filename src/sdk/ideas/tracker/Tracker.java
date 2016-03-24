package sdk.ideas.tracker;

import java.util.Collections;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import sdk.ideas.common.Common;
import sdk.ideas.common.Logs;
import sdk.ideas.common.Protocol;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.StringUtility;
import sdk.ideas.module.CmpClient;
import sdk.ideas.module.DeviceHandler;
import sdk.ideas.module.DeviceHandler.AccountData;

public class Tracker
{
	private TransferMessage			transferMessage				= null;
	private final int				TAG_APPSENSOR_INIT			= 1025;
	private final int				TAG_APPSENSOR_STARTTRACKER	= 1027;
	private final int				TAG_APPSENSOR_TRACKER		= 1028;

	public static final int			MSG_RESPONSE				= 1026;

	private Context					parentContext				= null;
	private DeviceHandler			deviceHandler				= null;
	private boolean					availableTracker			= false;

	// MAC + Phone + APP ID +Email(facebook first)
	private String					ID							= "";

	private HashMap<String, String>	startTrackerParm			= null;

	private Handler					theHandler					= new Handler()
																{
																	@Override
																	public void handleMessage(Message msg)
																	{
																		if (MSG_RESPONSE == msg.what)
																		{
																			switch(msg.arg2)
																			{

																			case TAG_APPSENSOR_INIT:
																				initHandle(msg.arg1, (String) msg.obj);
																				updateDbServerData();
																				break;

																			case TAG_APPSENSOR_STARTTRACKER:
																				if (msg.arg1 == ResponseCode.ERR_SUCCESS)
																				{
																					availableTracker = true;
																					callBack(ResponseCode.ERR_SUCCESS,
																							ResponseCode.METHOLD_START_TRACKER,
																							"success");
																				}
																				// CMPClient ERROR
																				else if (msg.arg1 <= ResponseCode.ERR_MAX)
																				{
																					callBack(
																							ResponseCode.ERR_IO_EXCEPTION,
																							ResponseCode.METHOLD_START_TRACKER,
																							"error in transfer data to server ");
																				}
																				else
																				{
																					callBack(msg.arg1,
																							ResponseCode.METHOLD_START_TRACKER,
																							(String) msg.obj);
																				}
																				break;

																			case TAG_APPSENSOR_TRACKER:
																				if (msg.arg1 == ResponseCode.ERR_SUCCESS)
																				{
																					callBack(ResponseCode.ERR_SUCCESS,
																							ResponseCode.METHOLD_TRACKER,
																							"success");
																				}
																				// CMPClient ERROR
																				else if (msg.arg1 <= ResponseCode.ERR_MAX)
																				{
																					callBack(
																							ResponseCode.ERR_IO_EXCEPTION,
																							ResponseCode.METHOLD_TRACKER,
																							"error in transfer data to server ");
																				}
																				else
																				{
																					callBack(msg.arg1,
																							ResponseCode.METHOLD_TRACKER,
																							(String) msg.obj);
																				}
																				break;

																			default:

																				break;
																			}
																		}
																	}
																};

	/**
	 * 
	 * @param result : error or success
	 * @param from : method call
	 * @param message : error message or success message
	 */
	public static interface TransferMessage
	{
		void showLinkServerMessageResult(final int result, final int from, final String message);
	}

	public void setOnTransferMessageListener(Tracker.TransferMessage listener)
	{
		if (null != listener)
		{
			transferMessage = listener;
		}
	}

	private void callBack(final int result, final int from, final String message)
	{
		if (null != transferMessage)
		{
			transferMessage.showLinkServerMessageResult(result, from, message);
		}
	}

	public Tracker()
	{
		super();
	}

	public Tracker(Context context)
	{
		parentContext = context;
	}

	/**
	 * 
	 * @param app_id : User register APP from MORE console will get APP ID.
	 * @return
	 */
	public int startTracker(final String app_id)
	{
		return startTracker(app_id, null, null, null);
	}

	public int startTracker(final String app_id, final String fb_id, final String fb_name, final String fb_email)
	{
		startTrackerParm = new HashMap<String, String>();

		if (StringUtility.isValid(app_id))
		{
			startTrackerParm.put("APP_ID", app_id);
		}
		else
		{
			// 沒有APP ID就勉強用package name
			startTrackerParm.put("APP_ID", parentContext.getPackageName());
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
		return 0;
	}

	private void updateDbServerData()
	{
		if (startTracker(this.startTrackerParm.get("APP_ID"), this.startTrackerParm) == -1)
		{
			// error
		}

		startTrackerParm.values().removeAll(Collections.singleton(""));

		JSONObject jsonParm = new JSONObject(startTrackerParm);

		this.sendEvent(jsonParm.toString(), TAG_APPSENSOR_STARTTRACKER);

		startTrackerParm.clear();
		startTrackerParm = null;

	}

	private int startTracker(String app_id, HashMap<String, String> parm)
	{
		if (null == parm)
		{
			return -1;
		}
		deviceHandler = new DeviceHandler(parentContext);

		deviceHandler.getLocation();

		parm.put("APP_ID", app_id);

		// joe fix null bug in 2016/03/02 begin
		String macAddress = deviceHandler.getMacAddress();
		if (macAddress == null)
		{
			parm.put("MAC", "");
		}
		else
		{
			parm.put("MAC", macAddress.replaceAll(":", ""));
		}

		String androidVersion = deviceHandler.getAndroidVersion();
		if (androidVersion == null)
		{
			parm.put("OS", "");
		}
		else
		{
			parm.put("OS", androidVersion);
		}

		String phone = deviceHandler.getPhoneNumber();
		if (phone == null)
		{
			parm.put("PHONE", "");
		}
		else
		{
			parm.put("PHONE", phone);
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
				parm.put("FB_ACCOUNT", listAccount.valueAt(i).strAccount);
				fbAccountExist = true;
				mailForID = listAccount.valueAt(i).strAccount;
			}
			else if (listAccount.valueAt(i).strAccount.contains("gmail")
					&& listAccount.valueAt(i).strType.contains("google"))
			{
				parm.put("G_ACCOUNT", listAccount.valueAt(i).strAccount);
				if (fbAccountExist == false)
				{
					mailForID = listAccount.valueAt(i).strAccount;
				}
			}
			else if (listAccount.valueAt(i).strType.contains("twitter"))
			{
				parm.put("T_ACCOUNT", listAccount.valueAt(i).strAccount);
			}

		}

		this.ID = (parm.get("MAC") + parm.get("PHONE") + parm.get("APP_ID") + mailForID);
		parm.put("ID", this.ID);

		return 0;
	}

	/**
	 * 
	 * @param parm : HashMap
	 */
	public void track(HashMap<String, String> parm)
	{
		if (availableTracker == false)
		{
			callBack(ResponseCode.ERR_NOT_INIT, ResponseCode.METHOLD_TRACKER,
					"can not start tracker cause startTarcker fail or stopTracker run");
			return;
		}

		parm.put("ID", this.ID);

		if (DeviceHandler.lat != -1.0 && DeviceHandler.lng != -1.0)
		{
			parm.put("LOCATION", (String.valueOf(DeviceHandler.lat) + "," + String.valueOf(DeviceHandler.lng)));
		}
		if (!StringUtility.isValid(parm.get("TYPE")))
		{
			callBack(ResponseCode.ERR_ILLEGAL_STRING_LENGTH_OR_NULL, ResponseCode.METHOLD_TRACKER,
					"type must exist a value");
			return;

		}

		parm.values().removeAll(Collections.singleton(""));

		JSONObject jsonParm = new JSONObject(parm);

		this.sendEvent(jsonParm.toString(), TAG_APPSENSOR_TRACKER);

		parm.clear();
		parm = null;

	}

	public void stopTracker()
	{
		availableTracker = false;
		callBack(ResponseCode.ERR_SUCCESS, ResponseCode.METHOLD_STOP_TRACKER, "stop tracker success");
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	public String getVersion()
	{
		return Common.Version;
	}

	private void init()
	{
		Thread t = new Thread(new sendSocketRunnable(TAG_APPSENSOR_INIT));
		t.start();

	}

	private boolean initHandle(final int nReturnCode, final String strContent)
	{
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
				Logs.showTrace(startTrackerData.toString());
				Logs.showTrace(trackerData.toString());
				Common.URL_APPSENSOR_STARTTRACKER = startTrackerData.getString("ip");
				Common.PORT_APPSENSOR_STARTTRACKER = startTrackerData.getInt("port");

				Common.URL_APPSENSOR_TRACKER = trackerData.getString("ip");
				Common.PORT_APPSENSOR_TRACKER = trackerData.getInt("port");
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return true;
		}
		else
		{

			callBack(ResponseCode.ERR_NOT_INIT, ResponseCode.METHOLD_START_TRACKER,
					"Start Tracker Fail: Can't connect Server");

			return false;
		}
	}

	protected void sendEvent(String jsonString, final int nTag)
	{

		if (null == Common.URL_APPSENSOR_STARTTRACKER)
		{
			Logs.showTrace("App Sensor Not Done Init");

			return;
		}

		Thread t = new Thread(new sendSocketRunnable(jsonString, nTag));
		t.start();
	}

	private int sendSocketData(String parm, HashMap<String, String> respData, CmpClient.Response response,
			final int mnfag)
	{
		if (mnfag == TAG_APPSENSOR_INIT)
		{
			if (null == response)
			{
				return -1;
			}
		}
		else if (mnfag == TAG_APPSENSOR_TRACKER)
		{
			if (null == parm || null == response)
			{
				return -1;
			}
		}
		else if (mnfag == TAG_APPSENSOR_STARTTRACKER)
		{
			if (null == parm || null == response)
			{
				return -1;
			}
		}

		if (mnfag == TAG_APPSENSOR_TRACKER)
		{
			try
			{

				CmpClient.accessLogRequest(Common.URL_APPSENSOR_TRACKER, Common.PORT_APPSENSOR_TRACKER,
						Protocol.TYPE_MOBILE_TRACKER, parm, respData, response);

			}
			catch (Exception e)
			{
				Logs.showTrace("Exception:" + e.getMessage());
			}
		}
		else if (mnfag == TAG_APPSENSOR_INIT)
		{
			try
			{
				CmpClient.init(Common.HOST_SERVICE_INIT, Common.PORT_SERVICE_INIT, Protocol.TYPE_MOBILE_TRACKER,
						respData, response);

			}
			catch (Exception e)
			{
				Logs.showTrace("Exception:" + e.getMessage());
			}

		}
		else if (mnfag == TAG_APPSENSOR_STARTTRACKER)
		{

			CmpClient.SignUpRequest(Common.URL_APPSENSOR_STARTTRACKER, Common.PORT_APPSENSOR_STARTTRACKER,
					Protocol.TYPE_MOBILE_TRACKER, parm, respData, response);

		}
		return response.mnCode;
	}

	class sendSocketRunnable implements Runnable
	{

		private int		mnTag	= 0;
		private String	parm	= null;

		@Override
		public void run()
		{
			HashMap<String, String> respData = new HashMap<String, String>();
			CmpClient.Response response = new CmpClient.Response();
			sendSocketData(parm, respData, response, mnTag);
			Common.postMessage(theHandler, MSG_RESPONSE, response.mnCode, mnTag, response.mstrContent);
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
