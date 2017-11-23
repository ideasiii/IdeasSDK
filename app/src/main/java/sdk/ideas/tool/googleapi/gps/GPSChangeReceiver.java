package sdk.ideas.tool.googleapi.gps;

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import sdk.ideas.common.BaseReceiver;

public class GPSChangeReceiver extends BaseReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			String action = intent.getAction();

			//Logs.showTrace("action: " + action);

			final LocationManager locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			//Logs.showTrace("isGPSEnabled: " + String.valueOf(isGPSEnabled) + " isNetworkEnabled: "
			//		+ String.valueOf(isNetworkEnabled));
			if (null != listener)
			{
				HashMap<String, String> data = new HashMap<String, String>();
				if (isGPSEnabled == false)
				{
					data.put("ENTITY_GPS", "CLOSE");
				}
				else
				{
					data.put("ENTITY_GPS", "OPEN");
				}

				if (isNetworkEnabled == false)
				{
					data.put("NETWORK_GPS", "CLOSE");
				}
				else
				{
					data.put("NETWORK_GPS", "OPEN");
				}

				listener.returnIntentAction(data);
			}
			else
			{
				// Logs.showTrace("Listener is null!");
			}
		}
		catch (Exception e)
		{

		}
	}

}
