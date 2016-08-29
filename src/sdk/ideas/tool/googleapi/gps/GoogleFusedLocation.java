package sdk.ideas.tool.googleapi.gps;

import java.util.HashMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import sdk.ideas.common.Common;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.ctrl.battery.BatteryReceiver;

public class GoogleFusedLocation
		implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, ListenReceiverAction
{

	public static final int GPS_UNKNOWN = -1;
	public static final int GPS_UPDATE = 0;
	public static final int GPS_CLOSE = 1;
	public static final int GPS_PRIVILEGE = 2;
	private GoogleApiClient mGoogleApiClient = null;

	private Location mCurrentLocation = null;

	private LocationRequest mLocationRequest = null;

	private static long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

	private static long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10000 / 2;

	private static int GPS_ACCURARY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

	private Handler mHandler = null;

	private Context mContext = null;

	
	
	public GoogleFusedLocation(Context mContext, Handler mHandler)
	{
		if (null != mHandler && null != mContext)
		{
			this.mHandler = mHandler;
			this.mContext = mContext;
		}
		init();
	}

	public void init()
	{
		buildGoogleApiClient();
	}

	
	protected synchronized void buildGoogleApiClient()
	{
		try
		{
		mGoogleApiClient = new GoogleApiClient.Builder(mContext).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
		}
		catch(Exception e)
		{
			//Logs.showTrace("On buildGoogleApiClient");
			//Logs.showTrace(e.toString());
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("message", e.toString());
			Common.postMessage(mHandler, GPS_UNKNOWN, 0, 0, message);
		}
	}

	protected void createLocationRequest()
	{
		mLocationRequest = new LocationRequest();

		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

		mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

		mLocationRequest.setPriority(GPS_ACCURARY);
	}

	protected void startLocationUpdates()
	{
		try
		{
			createLocationRequest();
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this,
					Looper.getMainLooper());
		}
		catch (Exception e)
		{
			//Logs.showTrace("On startLocationUpdates");
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("message", e.toString());
			Common.postMessage(mHandler, GPS_PRIVILEGE, 0, 0, message);
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
		mCurrentLocation = location;
		//Logs.showTrace("@@@onLocationChanged@@@");
		//Logs.showTrace("lat: " + String.valueOf(mCurrentLocation.getLatitude()) + " lng: "
		//		+ String.valueOf(mCurrentLocation.getLongitude()));
		HashMap<String, String> message = new HashMap<String, String>();
		message.put("lat", String.valueOf(mCurrentLocation.getLatitude()));
		message.put("lng", String.valueOf(mCurrentLocation.getLongitude()));
		Common.postMessage(mHandler, GPS_UPDATE, 0, 0, message);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		Logs.showTrace("@@onConnectionFailed@@");
		Logs.showTrace("Google API Connection failed: ErrorCode: " + String.valueOf(result.getErrorCode()));
		Logs.showTrace("Google API Connection failed: ErrorMessage: " + String.valueOf(result.getErrorMessage()));
		
		HashMap<String, String> message = new HashMap<String, String>();
		message.put("message", result.getErrorMessage());
		Common.postMessage(mHandler, GPS_UNKNOWN, 0, 0, message);
	}

	@Override
	public void onConnected(Bundle connectionHint)
	{
		//Logs.showTrace("@@onConnected@@");
		mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (mCurrentLocation != null)
		{
			//Logs.showTrace("lat: " + String.valueOf(mCurrentLocation.getLatitude()) + " lng: "
			//		+ String.valueOf(mCurrentLocation.getLongitude()));
		}
		else
		{
			final LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
			boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			//Logs.showTrace("isGPSEnabled: " + String.valueOf(isGPSEnabled) + " isNetworkEnabled: "
			//				+ String.valueOf(isNetworkEnabled));
			
			if(isNetworkEnabled == false)
			{
				HashMap<String, String> message = new HashMap<String, String>();
				//Logs.showTrace("GPS is closed");
				message.put("message", "GPS is closed by user");
				Common.postMessage(mHandler, GPS_CLOSE, 0, 0, message);
			}
			
			//Logs.showTrace("not yet to create");
		}
		startLocationUpdates();
	}
	
	

	@Override
	public void onConnectionSuspended(int cause)
	{
		//Logs.showTrace("@@onConnectionSuspended@@");
		startListenAction();
	}

	@Override
	public void startListenAction()
	{
		if (null != mGoogleApiClient)
		{
			if (mGoogleApiClient.isConnected() == false)
			{
				mGoogleApiClient.connect();
				
			}
		}
	}

	@Override
	public void stopListenAction()
	{

		if (null != mGoogleApiClient)
		{
			if (mGoogleApiClient.isConnected() == true)
			{
				mGoogleApiClient.disconnect();
			}
		}
	}

	public void setFastestUpdateTime(long milliseconds)
	{
		FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = milliseconds;
	}

	public void setUpdateTime(long milliseconds)
	{
		UPDATE_INTERVAL_IN_MILLISECONDS = milliseconds;
	}

	public void setGPSAccurary(int accurary)
	{
		switch (accurary)
		{
		case GPSAccuraySet.PRIORITY_BALANCED_POWER_ACCURACY:
			GPS_ACCURARY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
			break;
		case GPSAccuraySet.PRIORITY_HIGH_ACCURACY:
			GPS_ACCURARY = LocationRequest.PRIORITY_HIGH_ACCURACY;
			break;
		case GPSAccuraySet.PRIORITY_LOW_POWER:
			GPS_ACCURARY = LocationRequest.PRIORITY_LOW_POWER;
			break;
		case GPSAccuraySet.PRIORITY_NO_POWER:
			GPS_ACCURARY = LocationRequest.PRIORITY_NO_POWER;
			break;
		default:
			GPS_ACCURARY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
			break;

		}
	}

}
