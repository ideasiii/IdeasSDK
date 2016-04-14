package sdk.ideas.common;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import sdk.ideas.common.ResponseCode.ResponseMessage;
import sdk.ideas.module.DeviceHandler;
import sdk.ideas.module.SdkTracker;

public abstract class BaseHandler
{
	protected ArrayList<OnCallbackResult> listener = null;
	protected Context mContext = null;
	protected Handler theHandler = null;

	protected boolean isHandlerEnable = false;
	protected boolean isListenerEnable = false;

	protected ResponseMessage mResponseMessage = null;

	protected boolean permissonCheck = false;
	
	private SdkTracker mSdkTracker = null;
	
	private HashMap<String,String> sdkTrackerMessage = new HashMap<String, String>();
	
	protected BaseHandler(Context context)
	{
		if (null != context)
		{
			mContext = context;
			mResponseMessage = new ResponseMessage();
			listener = new ArrayList<OnCallbackResult> ();
			
			//if Control Center ok 
			//need to realese
			//sdkTrackerInit();
			
		}
		else
		{
			Log.e("MORE SDK ERROR", "Context is NULL");
		}
	}
	
	private void sdkTrackerInit()
	{
		
		mSdkTracker = new SdkTracker(mContext);
	}
	
	protected void sdkTrackerMessage(String SDKName,String SDKMethod)
	{
		sdkTrackerMessage.clear();
		sdkTrackerMessage.put("SDK", SDKName);
		sdkTrackerMessage.put("Method", SDKMethod);
		
		//if Control Center ok 
		//need to realese
		if(null != mSdkTracker)
		{
			mSdkTracker.track(sdkTrackerMessage);
		}
		else
		{
			Logs.showTrace("mSdkTracker is not init");
		}
	}
	protected boolean getAppIDVaild()
	{
		if(null != mSdkTracker)
		{
			return mSdkTracker.getAppIDVaild();
		}
		
		//if Control Center ok 
		//need to realese
		//return false;
		return true;
	}
	
	

	protected BaseHandler()
	{

	}
	
	protected boolean permissionCheck(String [] permissonList)
	{
		for (int i = 0; i < permissonList.length; i++)
		{
			//Logs.showTrace("check this permisson: "+permissonList[i]);
			if (DeviceHandler.hasPermission(mContext, permissonList[i]) == false)
			{
				Log.e("PERMISSION ERROR", "please add permission: "+permissonList[i]+" in manifest");
				return false;
			}
			else
			{
				//Logs.showTrace("this permisson: "+permissonList[i] +"is in manifest");
			}
		}
		permissonCheck = true;
		return true;
	}

	public void setHandler(Handler handler)
	{
		if (null != handler)
		{
			theHandler = handler;
			isHandlerEnable = true;
		}
	}
	
	
	
	
	/**
	 * 
	 * this setOnCallbackResultListener is multi listener
	 * and will return callbackID
	 * 
	 * */
	public void setOnCallbackResultListener(OnCallbackResult listener)
	{
		if (null != listener)
		{
			this.listener.add( listener);
			isListenerEnable = true;
		}
	}

	/**
	 *  Waring! call this method "setResponseMessage" first to set mResponseMessage
	 *  
	 *  mWhat                        : which Ctrl module message
	 * 	mFrom                        : which Ctrl module method message
	 *  mResponseMessage.mnCode      : error code
	 *  mResponseMessage.mStrContent : HashMap<String,String> detail error message or other functional message 
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
	}
	
	
	protected void setResponseMessage(int mnCode, HashMap<String,String> message)
	{
		mResponseMessage.mnCode = mnCode;
		mResponseMessage.mStrContent = new HashMap<String,String>(message);
	}
	
	protected void callBackMessage(int result, int what, int from, HashMap<String, String> message)
	{
		setResponseMessage(result, message);
		returnResponse(what, from);
	}



}
