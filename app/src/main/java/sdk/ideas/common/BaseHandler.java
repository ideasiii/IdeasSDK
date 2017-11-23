package sdk.ideas.common;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import sdk.ideas.common.ResponseCode.ResponseMessage;

public abstract class BaseHandler
{
	protected ArrayList<OnCallbackResult> listener = null;
	protected HashMap<String, OnCallbackResult> hashMapListener = null;

	protected Context mContext = null;
	protected Handler theHandler = null;
	protected HashMap<String, Handler> theHashMapHandler = null;

	protected boolean isHandlerEnable = false;
	protected boolean isListenerEnable = false;

	protected boolean isHashMapListenerEnable = false;
	protected boolean isHashMapHandlerEnable = false;

	protected ResponseMessage mResponseMessage = null;

	// protected boolean permissonCheck = false;

	// private SdkTracker mSdkTracker = null;

	// private HashMap<String,String> sdkTrackerMessage = new HashMap<String,
	// String>();

	protected BaseHandler(Context context)
	{
		if (null != context)
		{
			mContext = context;
			mResponseMessage = new ResponseMessage();
			listener = new ArrayList<OnCallbackResult>();
			hashMapListener = new HashMap<String, OnCallbackResult>();
			theHashMapHandler = new HashMap<String, Handler>();

			// if Control Center ok
			// need to realese
			// sdkTrackerInit();

		}
		else
		{
			Log.e("MORE SDK ERROR", "Context is NULL");
		}
	}

	/*
	 * private void sdkTrackerInit() {
	 * 
	 * mSdkTracker = new SdkTracker(mContext); }
	 */

	/*
	 * protected void sdkTrackerMessage(String SDKName,String SDKMethod) {
	 * sdkTrackerMessage.clear(); sdkTrackerMessage.put("SDK", SDKName);
	 * sdkTrackerMessage.put("Method", SDKMethod);
	 * 
	 * //if Control Center ok //need to realese if(null != mSdkTracker) {
	 * mSdkTracker.track(sdkTrackerMessage); } else { Logs.showTrace(
	 * "mSdkTracker is not init"); } }
	 */
	/*
	 * protected boolean getAppIDVaild() { if(null != mSdkTracker) { return
	 * mSdkTracker.getAppIDVaild(); }
	 * 
	 * //if Control Center ok //need to realese //return false; return true; }
	 */


	/*
	 * protected boolean permissionCheck(String [] permissonList) { for (int i =
	 * 0; i < permissonList.length; i++) { Logs.showTrace(
	 * "check this permisson: "+permissonList[i]); if
	 * (DeviceHandler.hasPermission(mContext, permissonList[i]) == false) {
	 * Logs.showTrace("PERMISSION ERROR: please add permission: "
	 * +permissonList[i]+" in manifest"); return false; } else { Logs.showTrace(
	 * "this permisson: "+permissonList[i] +"is in manifest"); } }
	 * permissonCheck = true; return true; }
	 */

	public void setHandler(Handler handler)
	{
		if (null != handler)
		{
			theHandler = handler;
			isHandlerEnable = true;
		}
	}

	public void setHandler(Handler handler, String handlerID)
	{
		if (null != handler)
		{
		//	Logs.showTrace("handlerID: " + handlerID + " ");
			theHashMapHandler.put(handlerID, handler);

			isHashMapHandlerEnable = true;
		}
	}

	/**
	 * 
	 * this setOnCallbackResultListener is multi listener
	 * 
	 */
	public void setOnCallbackResultListener(OnCallbackResult listener)
	{
		if (null != listener)
		{
			this.listener.add(listener);
			isListenerEnable = true;
		}
	}

	/**
	 * 
	 * this setOnCallbackResultListener is multi listener
	 * 
	 */
	public void setOnCallbackResultListener(OnCallbackResult listener, String callBackID)
	{
		if (null != listener)
		{
			this.hashMapListener.put(callBackID, listener);
			isHashMapListenerEnable = true;
		}
	}

	/**
	 * Waring! call this method "setResponseMessage" first to set
	 * mResponseMessage
	 * 
	 * mWhat : which Ctrl module message mFrom : which Ctrl module method
	 * message mResponseMessage.mnCode : error code mResponseMessage.mStrContent
	 * : HashMap<String,String> detail error message or other functional message
	 */

	protected void returnResponse(int mWhat, int mFrom)
	{
		if (isListenerEnable)
		{
			for (int i = 0; i < listener.size(); i++)
				listener.get(i).onCallbackResult(mResponseMessage.mnCode, mWhat, mFrom, mResponseMessage.mStrContent);
		}
		if (isHandlerEnable)
		{
			Common.postMessage(theHandler, mWhat, mResponseMessage.mnCode, mFrom, mResponseMessage.mStrContent);
		}
		if (isHashMapListenerEnable)
		{
			for (String callbackID : hashMapListener.keySet())
			{
				if (null != this.hashMapListener.get(callbackID))
				{
					this.hashMapListener.get(callbackID).onCallbackResult(mResponseMessage.mnCode, mWhat, mFrom,
							mResponseMessage.mStrContent);
				}
			}

		}
		if (isHashMapHandlerEnable)
		{
			for (String callbackID : theHashMapHandler.keySet())
			{
				if (null != this.theHashMapHandler.get(callbackID))
				{
					Common.postMessage(this.theHashMapHandler.get(callbackID), mWhat, mResponseMessage.mnCode, mFrom,
							mResponseMessage.mStrContent);
				}
				else
				{
					Logs.showError("this.theHashMapHandler.get(callbackID) is null");
				}
			}

		}

	}

	private void returnResponse(int mWhat, int mFrom, String callbackID)
	{
		if (isListenerEnable)
		{
			for (int i = 0; i < listener.size(); i++)
				listener.get(i).onCallbackResult(mResponseMessage.mnCode, mWhat, mFrom, mResponseMessage.mStrContent);
		}
		if (this.isHashMapListenerEnable)
		{
			if (null != this.hashMapListener.get(callbackID))
			{
				this.hashMapListener.get(callbackID).onCallbackResult(mResponseMessage.mnCode, mWhat, mFrom,
						mResponseMessage.mStrContent);
			}

		}

		if (isHandlerEnable)
		{
			Common.postMessage(theHandler, mWhat, mResponseMessage.mnCode, mFrom, mResponseMessage.mStrContent);
		}

		if (isHashMapHandlerEnable)
		{
			if (null != this.theHashMapHandler.get(callbackID))
			{
				Common.postMessage(this.theHashMapHandler.get(callbackID), mWhat, mResponseMessage.mnCode, mFrom,
						mResponseMessage.mStrContent);
			}
			else
			{
				Logs.showError("this.theHashMapHandler.get(callbackID) is null");
			}

		}

	}

	protected void setResponseMessage(int mnCode, HashMap<String, String> message)
	{
		mResponseMessage.mnCode = mnCode;
		mResponseMessage.mStrContent = new HashMap<String, String>(message);
	}

	protected void callBackMessage(int result, int what, int from, HashMap<String, String> message)
	{
		setResponseMessage(result, message);
		returnResponse(what, from);
	}

	protected void callBackMessage(int result, int what, int from, HashMap<String, String> message, String callbackID)
	{
		setResponseMessage(result, message);
		returnResponse(what, from, callbackID);
	}

}
