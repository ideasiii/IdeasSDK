package sdk.ideas.ctrl.gps;

import java.util.HashMap;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

public class LocationHandler extends BaseHandler implements ListenReceiverAction
{
	private LocationManager manager = null;

	// milliseconds default 1 min check
	private long millisecondTime = 60000;
	// meter default 100 meter check
	private float meterDistance = 100;

	private Criteria criteria = null;

	private boolean isLocationOn = false;

	public LocationHandler(Context context)
	{
		super(context);
		manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
	}
	
	
	

	public void setMinTimeUpdate(long millisecondTime)
	{
		if (millisecondTime >= 0)
			this.millisecondTime = millisecondTime;
	}

	public void setMinDistance(float meterDistance)
	{
		if (meterDistance >= 0)
			this.meterDistance = meterDistance;
	}

	private void getLocation()
	{
		HashMap<String, String> message = new HashMap<String, String>();
		try
		{
			String provider = manager.getBestProvider(criteria, false);
			Logs.showTrace("Location Provider:" + provider);
			

			//joe fix bug 2016/08/15
		/*	if (Looper.myLooper() == null)
			{
				Logs.showTrace("Looper myLooper is null!");
				Looper.prepare();
			}*/
			if(Looper.myLooper() == Looper.getMainLooper())
			{
				   // Current Thread is Main Thread.)
				Logs.showTrace("Current Thread is Main Thread(UI)");
			}
			
			manager.requestLocationUpdates(provider, millisecondTime, meterDistance, locationListener,Looper.getMainLooper());
			
			Location location = manager.getLastKnownLocation(provider);
			updateLocation(location);
		}
		catch (SecurityException e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION, CtrlType.MSG_RESPONSE_LOCATION_HANDLER,
					ResponseCode.METHOD_UPDATE_LOCATION, message);
		}
		catch (IllegalArgumentException e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION, CtrlType.MSG_RESPONSE_LOCATION_HANDLER,
					ResponseCode.METHOD_UPDATE_LOCATION, message);
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_LOCATION_HANDLER,
					ResponseCode.METHOD_UPDATE_LOCATION, message);
		}
		finally
		{
			message.clear();
		}
	}

	private final LocationListener locationListener = new LocationListener()
	{
		@Override
		public void onLocationChanged(Location location)
		{
			updateLocation(location);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{

		}

		@Override
		public void onProviderEnabled(String provider)
		{
			Logs.showTrace("Provider now is enabled..");
			getLocation();
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			HashMap<String, String> message = new HashMap<String, String>();
			updateLocation(null);

			Logs.showTrace("Location Provider now is disabled..");
			message.put("message", "GPS is closed by user");
			callBackMessage(ResponseCode.ERR_GPS_INACTIVE, CtrlType.MSG_RESPONSE_LOCATION_HANDLER,
					ResponseCode.METHOD_UPDATE_LOCATION, message);

			message.clear();

			// final Intent intent = new
			// Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			// mContext.startActivity(intent);
		}

	};

	private void updateLocation(Location location)
	{
		if (location != null)
		{
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("lat", String.valueOf(location.getLatitude()));
			message.put("lng", String.valueOf(location.getLongitude()));
			message.put("message", "success");
			callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_LOCATION_HANDLER,
					ResponseCode.METHOD_UPDATE_LOCATION, message);
			message.clear();
		}
		else
		{
		//	HashMap<String, String> message = new HashMap<String, String>();
			Logs.showTrace("location have some problem about GPS");
		//	message.put("message", "location have some problem about GPS");
		//	callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_LOCATION_HANDLER,
			//		ResponseCode.METHOD_UPDATE_LOCATION, message);
		}

	}

	@Override
	public void startListenAction()
	{
		if (isLocationOn == false)
		{
			isLocationOn = true;
			getLocation();
		}

	}

	@Override
	public void stopListenAction()
	{
		if (isLocationOn == true)
		{
			isLocationOn = false;
			manager.removeUpdates(locationListener);

		}
	}

}
