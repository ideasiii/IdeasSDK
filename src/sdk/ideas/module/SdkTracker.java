package sdk.ideas.module;

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

	public void track(final String strParam)
	{
		trackerThread thdTracker = new trackerThread();
		thdTracker.start();
	}

	private class trackerThread extends Thread
	{
		@Override
		public void run()
		{
			runTracker("", "");
		}
	}

	private int runTracker(final String strTargetURL, final String strParameters)
	{
		return 0;
	}

}
