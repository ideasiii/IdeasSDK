package sdk.ideas.common;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import sdk.ideas.common.ResponseCode.ResponseMessage;
import sdk.ideas.module.DeviceHandler;

public abstract class BaseHandler
{
	protected ArrayList<OnCallbackResult> listener = null;
	protected Context mContext = null;
	protected Handler theHandler = null;

	protected boolean isHandlerEnable = false;
	protected boolean isListenerEnable = false;

	protected ResponseMessage mResponseMessage = null;

	protected boolean permissonCheck = false;
	
	protected BaseHandler(Context context)
	{
		if (null != context)
		{
			mContext = context;
			mResponseMessage = new ResponseMessage();
			listener = new ArrayList<OnCallbackResult> ();
		}
		else
		{
			Log.e("MORE SDK ERROR", "Context is NULL");
		}
	}

	protected BaseHandler()
	{

	}
	
	protected boolean permissionCheck(String [] permissonList)
	{
		for (int i = 0; i < permissonList.length; i++)
		{
			Logs.showTrace("check this permisson: "+permissonList[i]);
			if (DeviceHandler.hasPermission(mContext, permissonList[i]) == false)
			{
				Log.e("PERMISSION ERROR", "please add permission: "+permissonList[i]+" in manifest");
				return false;
			}
			else
			{
				Logs.showTrace("this permisson: "+permissonList[i] +"is in manifest");
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
