package sdk.ideas.ctrl.gps;

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

public class LocationHandler extends BaseHandler implements ListenReceiverAction
{
	private LocationManager manager = null;
	private HashMap<String, String> message = null;

	// milliseconds default 1 min check
	private long millisecondTime = 60000;
	// meter default 100 meter check
	private float meterDistance = 100;

	private Criteria criteria = null;

	private boolean isLocationOn = false;

	public LocationHandler(Context context)
	{
		super(context);
		message = new HashMap<String, String>();
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
		String provider = manager.getBestProvider(criteria, false);
		Logs.showTrace("Location Provider:" + provider);
		Location location = manager.getLastKnownLocation(provider);

		updateLocation(location);

		manager.requestLocationUpdates(provider, millisecondTime, meterDistance, locationListener);
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
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			updateLocation(null);
			Logs.showTrace("Location Provider now is disabled..");
			final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			mContext.startActivity(intent);
		}

	};

	private void updateLocation(Location location)
	{
		if (location != null)
		{
			message.put("lat", String.valueOf(location.getLatitude()));
			message.put("lng", String.valueOf(location.getLongitude()));

			setResponseMessage(ResponseCode.ERR_SUCCESS, message);
			returnRespose(CtrlType.MSG_RESPONSE_LOCATION_HANDLER, ResponseCode.METHOD_UPDATE_LOCATION);
			message.clear();
		}
		else
		{
			Logs.showTrace("Location: location have some problem about GPS");
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
			message.put("close", "success");
			setResponseMessage(ResponseCode.ERR_SUCCESS, message);
			message.clear();
		}
	}

}