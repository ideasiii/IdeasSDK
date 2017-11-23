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
import android.os.Looper;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ReturnIntentAction;

public class FusedLocationHandler extends BaseHandler
		implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener, ListenReceiverAction
{

	private GoogleApiClient mGoogleApiClient = null;

	private Location mCurrentLocation = null;

	private LocationRequest mLocationRequest = null;

	private static long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

	private static long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10000 / 2;

	private static int GPS_ACCURARY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

	private GPSChangeReceiver mGPSChangeReceiver = null;
	private IntentFilter mIntentFilter = null;

	private boolean isStartListen = false;

	public FusedLocationHandler(Context mContext)
	{
		super(mContext);

		init();
	}

	private void init()
	{
		Logs.showTrace("Fused INIT START");
		buildGoogleApiClient();

		mGPSChangeReceiver = new GPSChangeReceiver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("android.location.PROVIDERS_CHANGED");

		Logs.showTrace("Fused INIT END");
	}

	private synchronized void buildGoogleApiClient()
	{
		try
		{
			mGoogleApiClient = new GoogleApiClient.Builder(mContext).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
		}
		catch (Exception e)
		{
			// Logs.showTrace("On buildGoogleApiClient");
			// Logs.showTrace(e.toString());
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_FUSED_LOCATION_HANDLER,
					ResponseCode.METHOD_UPDATE_LOCATION, message);
		}
	}

	private void createLocationRequest()
	{
		mLocationRequest = new LocationRequest();

		mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

		mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

		mLocationRequest.setPriority(GPS_ACCURARY);
	}

	private void startLocationUpdates()
	{
		try
		{
			createLocationRequest();
			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this,
					Looper.getMainLooper());
		}
		catch (Exception e)
		{
			// Logs.showTrace("On startLocationUpdates");
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION, CtrlType.MSG_RESPONSE_FUSED_LOCATION_HANDLER,
					ResponseCode.METHOD_UPDATE_LOCATION, message);
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
		mCurrentLocation = location;
		// Logs.showTrace("@@@onLocationChanged@@@");
		// Logs.showTrace("lat: " +
		// String.valueOf(mCurrentLocation.getLatitude()) + " lng: "
		// + String.valueOf(mCurrentLocation.getLongitude()));
		HashMap<String, String> message = new HashMap<String, String>();
		message.put("lat", String.valueOf(mCurrentLocation.getLatitude()));
		message.put("lng", String.valueOf(mCurrentLocation.getLongitude()));

		callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_FUSED_LOCATION_HANDLER,
				ResponseCode.METHOD_UPDATE_LOCATION, message);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		Logs.showTrace("@@onConnectionFailed@@");
		Logs.showTrace("Google API Connection failed: ErrorCode: " + String.valueOf(result.getErrorCode()));
		Logs.showTrace("Google API Connection failed: ErrorMessage: " + String.valueOf(result.getErrorMessage()));

		HashMap<String, String> message = new HashMap<String, String>();
		message.put("message", result.getErrorMessage());
		callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_FUSED_LOCATION_HANDLER,
				ResponseCode.METHOD_UPDATE_LOCATION, message);
	}

	@Override
	public void onConnected(Bundle connectionHint)
	{
		// Logs.showTrace("@@onConnected@@");
		mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (mCurrentLocation != null)
		{
			// Logs.showTrace("lat: " +
			// String.valueOf(mCurrentLocation.getLatitude()) + " lng: "
			// + String.valueOf(mCurrentLocation.getLongitude()));
		}
		else
		{
			final LocationManager locationManager = (LocationManager) mContext
					.getSystemService(Context.LOCATION_SERVICE);
			// boolean isGPSEnabled =
			// locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			// Logs.showTrace("isGPSEnabled: " + String.valueOf(isGPSEnabled) +
			// " isNetworkEnabled: "
			// + String.valueOf(isNetworkEnabled));

			if (isNetworkEnabled == false)
			{
				HashMap<String, String> message = new HashMap<String, String>();
				// Logs.showTrace("GPS is closed");
				message.put("message", "GPS is closed by user");
				callBackMessage(ResponseCode.ERR_GPS_INACTIVE, CtrlType.MSG_RESPONSE_FUSED_LOCATION_HANDLER,
						ResponseCode.METHOD_UPDATE_LOCATION, message);
			}

			// Logs.showTrace("not yet to create");
		}
		startLocationUpdates();
	}

	@Override
	public void onConnectionSuspended(int cause)
	{
		// Logs.showTrace("@@onConnectionSuspended@@");
		startListenAction();
	}

	@Override
	public void startListenAction()
	{
		if (isStartListen == false)
		{
			if (null != mGoogleApiClient)
			{
				// Logs.showTrace("setOnReceiverListener start");
				mGPSChangeReceiver.setOnReceiverListener(new ReturnIntentAction()
				{
					@Override
					public void returnIntentAction(HashMap<String, String> data)
					{
						HashMap<String, String> message = new HashMap<String, String>();
						if (data.get("NETWORK_GPS").equals("CLOSE"))
						{
							Logs.showTrace("Location Provider now is disabled..");
							message.put("message", "NETWORK GPS is closed by user");
							callBackMessage(ResponseCode.ERR_GPS_INACTIVE, CtrlType.MSG_RESPONSE_FUSED_LOCATION_HANDLER,
									ResponseCode.METHOD_UPDATE_LOCATION, message);
						}
						else if (data.get("NETWORK_GPS").equals("OPEN"))
						{
							Logs.showTrace("Location Provider now is enable..");
							// ..........
						}
					}
				});

				mContext.registerReceiver(mGPSChangeReceiver, mIntentFilter);

				if (mGoogleApiClient.isConnected() == false)
				{
					mGoogleApiClient.connect();

				}
			}
			isStartListen = true;
		}
	}

	@Override
	public void stopListenAction()
	{
		if (isStartListen == true)
		{
			if (null != mGoogleApiClient)
			{
				if (mGoogleApiClient.isConnected() == true)
				{
					mGoogleApiClient.disconnect();
				}
			}
			mContext.unregisterReceiver(mGPSChangeReceiver);
			isStartListen = false;
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

	public void setGPSAccuracy(int accuracy)
	{
		switch (accuracy)
		{
		case GPSAccuracySet.PRIORITY_BALANCED_POWER_ACCURACY:
			GPS_ACCURARY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
			break;
		case GPSAccuracySet.PRIORITY_HIGH_ACCURACY:
			GPS_ACCURARY = LocationRequest.PRIORITY_HIGH_ACCURACY;
			break;
		case GPSAccuracySet.PRIORITY_LOW_POWER:
			GPS_ACCURARY = LocationRequest.PRIORITY_LOW_POWER;
			break;
		case GPSAccuracySet.PRIORITY_NO_POWER:
			GPS_ACCURARY = LocationRequest.PRIORITY_NO_POWER;
			break;
		default:
			GPS_ACCURARY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
			break;

		}
	}

}
