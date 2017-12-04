package sdk.ideas.common;

import android.os.Handler;
import android.os.Message;

public abstract class Common
{
	public final static String	BANNER_AD_UNIT_ID			= "ca-app-pub-4351303625351134/2404547206";
	public final static String	INTERSTITIAL_AD_UNIT_ID		= "ca-app-pub-4351303625351134/4481552801";
	public final static int		TYPE_BANNER_AD				= 1;
	public final static int		TYPE_INTERSTITIALAD			= 2;
	public static String		Version						= "0.17.02.03";

	public static String		HOST_SERVICE_INIT			= "54.199.198.94";
	public static int			PORT_SERVICE_INIT			= 6607;
	
	//public static String      HOST_SERVICE_INIT_BACKUP    = "54.199.198.94";
	//public static int			PORT_SERVICE_INIT_BACKUP	= 6607;
	

	public static String		URL_TRACKER_STARTTRACKER	= "";
	public static int			PORT_TRACKER_STARTTRACKER	= 0;

	public static String		URL_TRACKER_TRACKER		= "";
	public static int			PORT_TRACKER_TRACKER		= 0;
	
	public static String        URL_SDK_TRACKER_INIT    = "54.199.198.94";
	public static int 			PORT_SDK_TRACKER_INIT   = 6607;

	public static void postMessage(Handler handler, int nWhat, int nArg0, int nArg1, Object obj)
	{
		if (null == handler)
			return;
		Thread t = new Thread(new postMsgRunnable(handler, nWhat, nArg0, nArg1, obj));
		t.start();
	}

	private static class postMsgRunnable implements Runnable
	{
		private Message	message		= null;
		private Handler	theHandler	= null;

		@Override
		public void run()
		{
			if (null == message || null == theHandler)
				return;
			theHandler.sendMessage(message);
		}

		public postMsgRunnable(Handler handler, int nWhat, int nArg1, int nArg2, Object obj)
		{
			theHandler = handler;
			message = new Message();
			message.what = nWhat;
			message.arg1 = nArg1;
			message.arg2 = nArg2;
			message.obj = obj;
		}
	}

}
