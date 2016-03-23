package sdk.ideas.module;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import sdk.ideas.common.Logs;
import sdk.ideas.common.Protocol;
import sdk.ideas.common.Type;
import sdk.ideas.module.CmpClient.Response;

public class SdkTracker
{
	private boolean	mbInterNet	= false;
	private String	mstrHost	= null;
	private int		mnPort		= Type.INVALID;

	public SdkTracker(Context context)
	{
		mbInterNet = DeviceHandler.hasPermission(context, android.Manifest.permission.INTERNET);
		Logs.showTrace("Internet permission:" + String.valueOf(mbInterNet));
	}

	public void init(final String strInitHost, final int nInitPort)
	{
		HashMap<String, String> respData = new HashMap<String, String>();
		Response response = new Response();
		CmpClient.init(strInitHost, nInitPort, Protocol.TYPE_SDK_TRACKER, respData, response);
	}

	public void track(final HashMap<String, String> mapParam)
	{
		if (!mbInterNet || null == mapParam || 0 >= mapParam.size())
			return;
		TrackerThread thdTracker = new TrackerThread(mapParam);
		thdTracker.start();
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

	private void send(final String strParameters)
	{
		if (null == mstrHost || Type.INVALID >= mnPort)
			return;
		CmpClient.sdkTrackerRequest(mstrHost, mnPort, strParameters);
	}

}
