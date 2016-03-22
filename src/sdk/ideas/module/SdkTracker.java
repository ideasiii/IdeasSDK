package sdk.ideas.module;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import sdk.ideas.common.Logs;

public class SdkTracker
{
	private boolean mbInterNet = false;

	public SdkTracker(Context context)
	{
		mbInterNet = DeviceHandler.hasPermission(context, android.Manifest.permission.INTERNET);
		Logs.showTrace("Internet permission:" + String.valueOf(mbInterNet));
	}

	public void track(final HashMap<String, String> mapParam)
	{
		TrackerThread thdTracker = new TrackerThread(mapParam);
		thdTracker.start();
	}

	private class TrackerThread extends Thread
	{
		String strParam = null;

		@Override
		public void run()
		{
			if (null == strParam)
				return;
			send(strParam);
		}

		public TrackerThread(final HashMap<String, String> mapParam)
		{
			JSONObject jsonObj = new JSONObject(mapParam);
			strParam = jsonObj.toString();
		}
	}

	private int send(final String strParameters)
	{
		Logs.showTrace("SDK send:" + strParameters);
		return 0;
	}

}
