package sdk.ideas.common;

import java.util.HashMap;
import android.content.BroadcastReceiver;

public abstract class BaseReceiver extends BroadcastReceiver
{
	protected ReturnIntentAction listener = null;
	protected HashMap<String,String> actionData = null;

	public BaseReceiver()
	{
		actionData = new HashMap<String,String>();
	}

	public void setOnReceiverListener(ReturnIntentAction listener)
	{
		if (null != listener)
		{
			this.listener = listener;
			//Logs.showTrace("listener is set OK" );
		}
	}

}
