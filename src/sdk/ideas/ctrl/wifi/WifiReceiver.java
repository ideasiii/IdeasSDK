package sdk.ideas.ctrl.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import sdk.ideas.common.BaseReceiver;

public class WifiReceiver extends BaseReceiver
{

	public WifiReceiver()
	{
		super();
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		actionData.clear();
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
		if (null != netInfo && netInfo.getType() == ConnectivityManager.TYPE_WIFI)
		{
			// Log.d("WifiReceiver", "Have Wifi Connection");
			actionData.put("message", "connected");
		}
		else if (null == netInfo)
		{
			// Log.d("WifiReceiver", "netInfo == null");
			// Log.d("WifiReceiver", "Don't have Wifi Connection");
			actionData.put("message", "disconnected");
		}
		if (null != listener)
		{
			listener.returnIntentAction(actionData);
			
		}
	}

}
