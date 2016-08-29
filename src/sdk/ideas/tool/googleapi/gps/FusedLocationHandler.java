package sdk.ideas.tool.googleapi.gps;

import java.util.HashMap;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ReturnIntentAction;
import sdk.ideas.ctrl.battery.BatteryHandler;

public class FusedLocationHandler extends BaseHandler implements ListenReceiverAction
{
	private GoogleFusedLocation mGoogleFusedLocation = null;

	private GPSChangeReceiver mGPSChangeReceiver = null;
	private IntentFilter mIntentFilter = null;
	private boolean isStartListen = false;
	
	
	private Handler selfHandler = new Handler(Looper.getMainLooper())
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case GoogleFusedLocation.GPS_UPDATE:

				callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_FUSED_LOCATION_HANDLER,
						ResponseCode.METHOD_BATTERY, (HashMap<String, String>) msg.obj);
				break;
			case GoogleFusedLocation.GPS_CLOSE:
				callBackMessage(ResponseCode.ERR_GPS_INACTIVE, CtrlType.MSG_RESPONSE_FUSED_LOCATION_HANDLER,
						ResponseCode.METHOD_UPDATE_LOCATION, (HashMap<String, String>) msg.obj);
				break;
			case GoogleFusedLocation.GPS_PRIVILEGE:
				callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION, CtrlType.MSG_RESPONSE_FUSED_LOCATION_HANDLER,
						ResponseCode.METHOD_UPDATE_LOCATION, (HashMap<String, String>) msg.obj);
				break;
			default:
				break;

			}

		}

	};

	public FusedLocationHandler(Context mContext)
	{
		super(mContext);
		init();

	}

	public void init()
	{
		mGoogleFusedLocation = new GoogleFusedLocation(mContext, selfHandler);
		
		mGPSChangeReceiver = new GPSChangeReceiver();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("android.location.PROVIDERS_CHANGED");
	}

	@Override
	public void startListenAction()
	{
		
		if(isStartListen == false)
		{	
			//Logs.showTrace("setOnReceiverListener start");
			mGPSChangeReceiver.setOnReceiverListener(new ReturnIntentAction()
			{
				@Override
				public void returnIntentAction(HashMap<String, String> data)
				{
					HashMap<String, String> message = new HashMap<String, String>();
					if(data.get("NETWORK_GPS").equals("CLOSE"))
					{
						Logs.showTrace("Location Provider now is disabled..");
						message.put("message", "NETWORK GPS is closed by user");
						callBackMessage(ResponseCode.ERR_GPS_INACTIVE, CtrlType.MSG_RESPONSE_FUSED_LOCATION_HANDLER,
								ResponseCode.METHOD_UPDATE_LOCATION, message);
					}
					else if(data.get("NETWORK_GPS").equals("OPEN"))
					{
						Logs.showTrace("Location Provider now is enable..");
						//..........
					}
				}
			});
			//Logs.showTrace("setOnReceiverListener end");
			mGoogleFusedLocation.startListenAction();
			
			mContext.registerReceiver(mGPSChangeReceiver, mIntentFilter);
			
			isStartListen = true;
		}
	}

	@Override
	public void stopListenAction()
	{
		if(isStartListen == true)
		{
			mGoogleFusedLocation.stopListenAction();
			mContext.unregisterReceiver(mGPSChangeReceiver);
			isStartListen = false;
		}
	}
	public void setUpdateTime(long milliseconds)
	{
		if (null != mGoogleFusedLocation)
		{
			mGoogleFusedLocation.setFastestUpdateTime(milliseconds / 2);
			mGoogleFusedLocation.setUpdateTime(milliseconds);
		}
	}
	public void setGPSAccurary(int accurary)
	{
		if (null != mGoogleFusedLocation)
		{
			mGoogleFusedLocation.setGPSAccurary(accurary);
		}
	}
}
