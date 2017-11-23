package sdk.ideas.module;

import java.util.HashMap;
import org.json.JSONObject;
import android.content.Context;
import sdk.ideas.common.Common;
import sdk.ideas.common.Logs;
import sdk.ideas.common.Protocol;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.Type;
import sdk.ideas.module.CmpClient.Response;

public class SdkTracker
{
	private boolean mbInterNet = false;
	private String mstrHost = null;
	private int mnPort = Type.INVALID;
	private Context context = null;

	private String appID = null;

	private boolean isValidAppID = false;

	public SdkTracker(Context context)
	{
		this.context = context;
		mbInterNet = DeviceHandler.hasPermission(context, android.Manifest.permission.INTERNET);
		Logs.showTrace("Internet permission:" + String.valueOf(mbInterNet));
		Thread init = new Thread(new InitRunnable());
		init.start();
	}

	public boolean getAppIDVaild()
	{
		return isValidAppID;
	}

	public void track(final HashMap<String, String> mapParam)
	{
		if (!mbInterNet || null == mapParam || 0 >= mapParam.size())
			return;
		TrackerThread thdTracker = new TrackerThread(mapParam);
		thdTracker.start();
	}

	private void init()
	{
		HashMap<String, String> respData = new HashMap<String, String>();
		Response response = new Response();

		CmpClient.init(Common.URL_SDK_TRACKER_INIT, Common.PORT_SDK_TRACKER_INIT, Protocol.TYPE_SDK_TRACKER, respData,
				response);

		if (response.mnCode == ResponseCode.ERR_SUCCESS)
		{
			appID = MetaHandler.getMetaDataFromApplication(context, "more.sdk.appid");
			Logs.showTrace(appID);
			if (appID != null)
			{
				String strContent = response.mstrContent;
				response = null;
				response = new Response();
				respData.clear();
				try
				{
					JSONObject dataArray = new JSONObject(strContent);
					JSONObject authenticationData, sdkTrackerData = null;

					if (((JSONObject) dataArray.getJSONArray("server").get(0)).get("id").equals(0))
					{
						authenticationData = ((JSONObject) dataArray.getJSONArray("server").get(0));
						sdkTrackerData = ((JSONObject) dataArray.getJSONArray("server").get(1));
					}
					else
					{
						authenticationData = ((JSONObject) dataArray.getJSONArray("server").get(1));
						sdkTrackerData = ((JSONObject) dataArray.getJSONArray("server").get(0));
					}

					mstrHost = sdkTrackerData.getString("ip");
					mnPort = sdkTrackerData.getInt("port");

					CmpClient.authenticationRequest(authenticationData.getString("ip"),
							authenticationData.getInt("port"), Protocol.TYPE_SDK_TRACKER, appID, respData, response);
					if (response.mnCode == ResponseCode.ERR_SUCCESS)
					{
						isValidAppID = true;
					}
					else
					{
						isValidAppID = false;
					}

				}
				catch (Exception e)
				{
					Logs.showTrace("sdk tracker init ERROR: " + e.toString());
				}

			}
		}
		else
		{
			Logs.showTrace(String.valueOf(response.mnCode));
			Logs.showTrace(response.mstrContent);
		}
	}

	private class InitRunnable implements Runnable
	{
		@Override
		public void run()
		{
			init();
		}

		public InitRunnable()
		{
		}

	}
	
	private void send(final String strParameters)
	{
		if (null == mstrHost || Type.INVALID >= mnPort)
			return;
		CmpClient.sdkTrackerRequest(mstrHost, mnPort, strParameters);
	}

	private class TrackerThread extends Thread
	{
		String strParam = null;

		@Override
		public void run()
		{
			send(strParam);
		}

		public TrackerThread(final HashMap<String, String> mapParam)
		{
			JSONObject jsonObj = new JSONObject(mapParam);
			strParam = jsonObj.toString();
		}
	}

	

}
