package sdk.ideas.common;

import android.os.Handler;
import android.os.Message;

public abstract class Common
{
	public static String Version = "0.15.12.16";
	
	public static String URL_APPSENSOR_INIT 			= "54.199.198.94";
	public static int 	 PORT_APPSENSOR_INIT 			= 6607;
	
	public static String URL_APPSENSOR_STARTTRACKER 	= "";
	public static int	 PORT_APPSENSOR_STARTTRACKER 	= 0;
	
	public static String URL_APPSENSOR_TRACKER 			= "";
	public static int	 PORT_APPSENSOR_TRACKER 		= 0;
	
	public static String MOBILE_DEVICE       			= "1";
	public static String SMART_POWER_STATION 			= "2";
	//public static String MOBILE_DEVICE_ACCESS       = "1";
	

	public static void postMessage(Handler handler, int nWhat, int nArg0, int nArg1, Object obj)
	{
		if (null == handler)
			return;
		Thread t = new Thread(new postMsgRunnable(handler, nWhat, nArg0, nArg1, obj));
		t.start();
	}

	private static class postMsgRunnable implements Runnable
	{
		private Message message = null;
		private Handler theHandler = null;

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
