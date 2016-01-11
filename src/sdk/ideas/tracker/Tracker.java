package sdk.ideas.tracker;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import sdk.ideas.common.Common;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.StringUtility;
import sdk.ideas.module.CmpClient;
import sdk.ideas.module.DeviceHandler;
import sdk.ideas.module.DeviceHandler.AccountData;

public class Tracker
{
	public static final String	TYPE_VIEW					= "0";
	public static final String	TYPE_SHOPPING_CART_ADD		= "1";
	public static final String	TYPE_SHOPPING_CART_CANCEL	= "2";
	public static final String	TYPE_PROD_ORDER				= "3";
	public static final String	TYPE_PROD_CANCEL			= "4";
	public static final String	TYPE_PROD_PREORDER			= "5";
	public static final String	TYPE_PROD_SEARCH			= "6";
	public static final String	TYPE_PROD_SELECT			= "7";
	public static final String	TYPE_BONUS_SELECT			= "8";
	public static final String	TYPE_GIFT_SELECT			= "9";
	public static final String	TYPE_VALUE_ADD_SELECT		= "10";
	public static final String	TYPE_OUTLET_SELECT			= "11";
	public static final String	TYPE_WELFARE_SELECT			= "12";
	public static final String	TYPE_PUSH_MESSAGE_VIEW		= "13";
	public static final String	TYPE_SERIAL_GET				= "14";
	public static final String	TYPE_SERVICE				= "15";

	private TransferMessage	transferMessage				= null;
	private final int		TAG_APPSENSOR_INIT			= 1025;
	private final int		TAG_APPSENSOR_STARTTRACKER	= 1027;
	private final int		TAG_APPSENSOR_TRACKER		= 1028;

	public static final int MSG_RESPONSE = 1026;

	private Context			parentContext		= null;
	private DeviceHandler	deviceHandler		= null;
	private boolean			availableTracker	= false;

	// MAC + Phone + APP ID +Email(facebook first)
	private String ID = "";

	private HashMap<String, String> startTrackerParm = null;

	private Handler theHandler = new Handler()
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
						transferMessage.showLinkServerMessageResult(ResponseCode.ERR_SUCCESS,
								ResponseCode.METHOLD_START_TRACKER, "success");
					}
					else if (msg.arg1 <= ResponseCode.ERR_MAX)
					{
						transferMessage.showLinkServerMessageResult(ResponseCode.ERR_IO_EXCEPTION,
								ResponseCode.METHOLD_START_TRACKER, "error in transfer data to server ");
					}
					else
					{
						transferMessage.showLinkServerMessageResult(msg.arg1, ResponseCode.METHOLD_START_TRACKER,
								(String) msg.obj);
					}
					break;

				case TAG_APPSENSOR_TRACKER:
					if (msg.arg1 == ResponseCode.ERR_SUCCESS)
					{
						transferMessage.showLinkServerMessageResult(ResponseCode.ERR_SUCCESS,
								ResponseCode.METHOLD_TRACKER, "success");
					}
					else if (msg.arg1 <= ResponseCode.ERR_MAX)
					{
						transferMessage.showLinkServerMessageResult(ResponseCode.ERR_IO_EXCEPTION,
								ResponseCode.METHOLD_TRACKER, "error in transfer data to server ");
					}
					else
					{
						transferMessage.showLinkServerMessageResult(msg.arg1, ResponseCode.METHOLD_TRACKER,
								(String) msg.obj);
					}
					break;

				default:

					break;
				}
			}
		}
	};

	public static interface TransferMessage
	{
		// result : error or success
		// from : method call
		// message : error message or success message
		void showLinkServerMessageResult(final int result, final int from, final String message);

	}

	public void setOnTransferMessageListener(Tracker.TransferMessage listener)
	{
		if (null != listener)
		{
			transferMessage = listener;
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
	 * startTracker( Parameter MAX Size APP ID 20 FB_ID 20 FB_NAME 50 FB_EMAIL
	 * 50 )
	 */

	public int startTracker(String app_id)
	{
		return startTracker(app_id, "NA", null, null);
	}

	public int startTracker(String app_id, String fb_id, String fb_name, String fb_email)
	{
		String strFbName = StringUtility.convertNull(fb_name);
		String strFbEmail = StringUtility.convertNull(fb_email);

		if ((!StringUtility.stringCheck(app_id, 1, 20)) || (!StringUtility.stringCheck(fb_id, 0, 20)))
		{
			transferMessage.showLinkServerMessageResult(ResponseCode.ERR_ILLEGAL_STRING_LENGTH_OR_NULL,
					ResponseCode.METHOLD_START_TRACKER, "one of row too more character or null");
			return -1;
		}
		startTrackerParm = new HashMap<String, String>();

		startTrackerParm.put("APP_ID", app_id);
		startTrackerParm.put("FB_ID", fb_id);
		startTrackerParm.put("FB_NAME", strFbName);
		startTrackerParm.put("FB_EMAIL", strFbEmail);

		init();
		return 0;
	}

	private void updateDbServerData()
	{
		startTracker(this.startTrackerParm.get("APP_ID"), this.startTrackerParm);

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
		parm.put("MAC", deviceHandler.getMacAddress().replaceAll(":", ""));
		parm.put("OS", deviceHandler.getAndroidVersion());
		parm.put("PHONE", deviceHandler.getPhoneNumber());

		SparseArray<AccountData> listAccount = new SparseArray<AccountData>();

		int num = deviceHandler.getAccounts(listAccount);

		String mailForID = "";
		boolean fbAccountExist = false;
		for (int i = 0; i < num; i++)
		{
			if (listAccount.valueAt(i).strType.contains("facebook"))
			{
				// Logs.showTrace("facebook account: " +
				// listAccount.valueAt(i).strAccount);
				parm.put("FB_ACCOUNT", listAccount.valueAt(i).strAccount);
				fbAccountExist = true;
				mailForID = listAccount.valueAt(i).strAccount;
			}
			else if (listAccount.valueAt(i).strAccount.contains("gmail")
					&& listAccount.valueAt(i).strType.contains("google"))
			{
				// Logs.showTrace("google account: " +
				// listAccount.valueAt(i).strAccount);
				parm.put("G_ACCOUNT", listAccount.valueAt(i).strAccount);
				if (fbAccountExist == false)
				{
					mailForID = listAccount.valueAt(i).strAccount;
				}
			}
			else if (listAccount.valueAt(i).strType.contains("twitter"))
			{
				// Logs.showTrace("twitter account: " +
				// listAccount.valueAt(i).strAccount);
				parm.put("T_ACCOUNT", listAccount.valueAt(i).strAccount);
			}

		}

		this.ID = (parm.get("MAC") + parm.get("PHONE") + parm.get("APP_ID") + mailForID);
		parm.put("ID", this.ID);
		parm.put("DATE", new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.TAIWAN).format(new Date()));
		return 0;
	}

	/**
	 * track(
	 * 
	 * HashMap<String,String>
	 *
	 *
	 * )
	 * 
	 * 
	 */
	public void track(HashMap<String, String> parm)
	{
		if (availableTracker == false)
		{
			transferMessage.showLinkServerMessageResult(ResponseCode.ERR_NOT_INIT, ResponseCode.METHOLD_TRACKER,
					"can not start tracker cause startTarcker fail or stopTracker run");
			return;
		}

		parm.put("ID", this.ID);

		parm.put("LOCATION", (String.valueOf(DeviceHandler.lat) + "," + String.valueOf(DeviceHandler.lng)));

		if (!StringUtility.isValid(parm.get("TYPE")))
		{
			transferMessage.showLinkServerMessageResult(ResponseCode.ERR_ILLEGAL_STRING_LENGTH_OR_NULL,
					ResponseCode.METHOLD_TRACKER, "type must exist a value");
			return;

		}
		parm.put("DATE", new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.TAIWAN).format(new Date()));
		parm.values().removeAll(Collections.singleton(""));

		JSONObject jsonParm = new JSONObject(parm);

		this.sendEvent(jsonParm.toString(), TAG_APPSENSOR_TRACKER);

		parm.clear();
		parm = null;

	}

	/**
	 * track( Parameter MAX Size TYPE 4 SOURCE_FROM 50 PAGE 50 PRODUCTION 50
	 * PRICE 10 )
	 */
	/*
	 * private int track(String type, String source_from, String page, String
	 * production, String price) { if (availableTracker == false) {
	 * transferMessage.showLinkServerMessageResult(ResponseCode.
	 * ERR_IO_EXCEPTION, ResponseCode.METHOLD_TRACKER,
	 * "can not start tracker because startTarcker fail or stopTracker already called"
	 * ); return -1; } // return error code table HashMap<String, String> parm =
	 * new HashMap<String, String>();
	 * 
	 * if ((!StringUtility.stringCheck(this.ID, 0, 100)) ||
	 * (!StringUtility.stringCheck(type, 0, 4)) ||
	 * (!StringUtility.stringCheck(source_from, 0, 50)) ||
	 * (!StringUtility.stringCheck(page, 0, 50)) ||
	 * (!StringUtility.stringCheck(production, 0, 50)) ||
	 * (!StringUtility.stringCheck(price, 0, 50))) { Logs.showTrace(this.ID +
	 * " " + type + " " + source_from + " "); Logs.showTrace(page + " " +
	 * production + " " + price + " ");
	 * transferMessage.showLinkServerMessageResult(ResponseCode.
	 * ERR_ILLEGAL_STRING_LENGTH_OR_NULL, ResponseCode.METHOLD_TRACKER,
	 * "one of row too more character or null");
	 * 
	 * return -1; }
	 * 
	 * parm.put("ID", this.ID); parm.put("TYPE", type); parm.put("SOURCE_FROM",
	 * source_from); parm.put("PAGE", page); parm.put("PRODUCTION", production);
	 * parm.put("PRICE", price);
	 * 
	 * parm.put("LOCATION", (String.valueOf(DeviceHandler.lat) + "," +
	 * String.valueOf(DeviceHandler.lng)));
	 * 
	 * parm.values().removeAll(Collections.singleton(""));
	 * 
	 * this.sendEvent(parm, ResponseCode.METHOLD_TRACKER);
	 * 
	 * parm.clear(); parm = null;
	 * 
	 * return 0; }
	 */

	public void stopTracker()
	{
		availableTracker = false;
		transferMessage.showLinkServerMessageResult(ResponseCode.ERR_SUCCESS, ResponseCode.METHOLD_STOP_TRACKER,
				"stop tracker success");
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

	/*
	 * public void init(HashMap<String, String> sendParm) { Thread t = new
	 * Thread(new sendPostRunnable( sendParm, TAG_APPSENSOR_INIT)); t.start();
	 * 
	 * }
	 */

	private void init()
	{
		Thread t = new Thread(new sendSocketRunnable(TAG_APPSENSOR_INIT));
		Logs.showTrace("in 375");
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

			transferMessage.showLinkServerMessageResult(ResponseCode.ERR_NOT_INIT, ResponseCode.METHOLD_START_TRACKER,
					"Start Tracker Fail: Can't connect Server");
			// Logs.showTrace("Get App Sensor Server URL Fail, Return Code:" +
			// String.valueOf(nReturnCode) + " Error:"
			// + strContent);
			return false;
		}
	}
	/*
	 * public void sendEvent(HashMap<String, String> postParams, final int nTag)
	 * {
	 * 
	 * if (null == Common.URL_APPSENSOR_STARTTRACKER) { Logs.showTrace(
	 * "App Sensor Not Done Init");
	 * 
	 * return; }
	 * 
	 * Thread t = new Thread(new sendPostRunnable(postParams, nTag)); t.start();
	 * }
	 */

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
						Common.MOBILE_DEVICE, parm, respData, response);

			}
			catch (Exception e)
			{
				e.printStackTrace();
				Logs.showTrace("Exception:" + e.getMessage());
			}
		}
		else if (mnfag == TAG_APPSENSOR_INIT)
		{
			try
			{
				CmpClient.init(Common.URL_APPSENSOR_INIT, Common.PORT_APPSENSOR_INIT,
						String.valueOf(Common.MOBILE_DEVICE), respData, response);

			}
			catch (Exception e)
			{
				e.printStackTrace();
				Logs.showTrace("Exception:" + e.getMessage());
			}

		}
		else if (mnfag == TAG_APPSENSOR_STARTTRACKER)
		{

			CmpClient.SignUpRequest(Common.URL_APPSENSOR_STARTTRACKER, Common.PORT_APPSENSOR_STARTTRACKER,
					Common.MOBILE_DEVICE, parm, respData, response);

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
			/*
			 * if(mnTag == TAG_APPSENSOR_TRACKER) {
			 * Common.postMessage(theHandler, MSG_RESPONSE, response.mnCode,
			 * mnTag, response.mstrContent); } else if(mnTag ==
			 * TAG_APPSENSOR_INIT) {
			 * 
			 * } else if(mnTag == TAG_APPSENSOR_STARTTRACKER) {
			 * Common.postMessage(theHandler, MSG_RESPONSE, response.mnCode,
			 * mnTag, response.mstrContent); }
			 */

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

	/*
	 * private int sendPostData(Map<String, String> parm, HttpClient.Response
	 * response) { if (null == parm || null == response) { return -1; }
	 * 
	 * try { HttpClient.sendPostData(Common.URL_APPSENSOR_TRACKER, parm,
	 * response, this.transferMessage);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); Logs.showTrace("Exception:"
	 * + e.getMessage()); } return response.mnCode; }
	 * 
	 * 
	 * class sendPostRunnable implements Runnable { Map<String, String> parm;
	 * private int mnTag = 0;
	 * 
	 * @Override public void run() { HttpClient.Response response = new
	 * HttpClient.Response(); sendPostData(parm, response);
	 * 
	 * /*Message msg = new Message(); msg.what = MSG_RESPONSE; msg.arg1 =
	 * response.mnCode; msg.arg2 = mnTag; msg.obj = response.mstrContent;
	 * theHandler.sendMessage(msg); msg = null;
	 * 
	 * Common.postMessage(theHandler, MSG_RESPONSE, response.mnCode, mnTag,
	 * response.mstrContent);
	 * 
	 * 
	 * parm.clear(); parm = null; }
	 */
	/*
	 * public sendPostRunnable(HashMap<String, String> PostParm, final int nTag)
	 * { this.mnTag = nTag; parm = new HashMap<String, String>();
	 * 
	 * if (null != PostParm && 0 < PostParm.size()) { for (Entry<String, String>
	 * item : PostParm.entrySet()) { parm.put(item.getKey(), item.getValue()); }
	 * } } }
	 * 
	 * private int sendGetData(final String strURL, Map<String, String> parm,
	 * HttpClient.Response response) { if (null == strURL || null == response) {
	 * return -1; }
	 * 
	 * try { HttpClient.sendGetData(strURL, parm, response); } catch (Exception
	 * e) { e.printStackTrace(); Logs.showTrace("Exception:" + e.getMessage());
	 * } return response.mnCode; }
	 * 
	 * class sendGetRunnable implements Runnable { private String mstrURL =
	 * null; private HashMap<String, String> parm = null; private int mnTag = 0;
	 * 
	 * @Override public void run() { HttpClient.Response response = new
	 * HttpClient.Response(); sendGetData(mstrURL, parm, response); /* 
	 * message Message msg = new Message(); msg.what = MSG_RESPONSE; msg.arg1 =
	 * response.mnCode; msg.arg2 = mnTag; msg.obj = response.mstrContent;
	 * theHandler.sendMessage(msg); msg = null;
	 * 
	 * 
	 * 
	 * Common.postMessage(theHandler, MSG_RESPONSE, response.mnCode, mnTag,
	 * response.mstrContent);
	 * 
	 * parm.clear(); parm = null;
	 * 
	 * }
	 *
	 * public sendGetRunnable(final String strURL, final HashMap<String, String>
	 * sendParm, final int nTag) { if (!StringUtility.isValid(strURL)) return;
	 * 
	 * mstrURL = strURL; mnTag = nTag; parm = new HashMap<String, String>();
	 * 
	 * if (null != sendParm && 0 < sendParm.size()) { for (Entry<String, String>
	 * item : sendParm.entrySet()) { parm.put(item.getKey(), item.getValue()); }
	 * } }
	 */
}
