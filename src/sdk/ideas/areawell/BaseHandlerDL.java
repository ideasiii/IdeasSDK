package sdk.ideas.areawell;


import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.os.Handler;
import sdk.ideas.common.OnCallbackResult;
import sdk.ideas.common.ResponseCode.ResponseMessage;

public abstract class BaseHandlerDL
{
	protected ArrayList<OnCallbackResult> listener = null;
	protected Context mContext = null;
	protected Handler theHandler = null;

	protected boolean isHandlerEnable = false;
	protected boolean isListenerEnable = false;

	protected ResponseMessage mResponseMessage = null;

	protected BaseHandlerDL(Context context)
	{
		if (null != context)
		{
			mContext = context;
			mResponseMessage = new ResponseMessage();
			listener = new ArrayList<OnCallbackResult> ();
		}
	}

	protected BaseHandlerDL()
	{

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
	
	protected void returnRespose(int mWhat, int mFrom)
	{
		if (isListenerEnable)
		{
			for (int i = 0; i < listener.size(); i++)
				listener.get(i).onCallbackResult(mResponseMessage.mnCode, mWhat, mFrom, mResponseMessage.mStrContent);
		}
		
	}
	
	/**
	 *  Waring! call this method "setResponseMessage" first to set mResponseMessage
	 *  
	 *  mWhat                        : which Ctrl module message
	 * 	mFrom                        : which Ctrl module method message
	 *  mResponseMessage.mnCode      : error code
	 *  mResponseMessage.mStrContent : HashMap<String,String> detail error message or other functional message 
	 *  callbackID                   : if you use interface to listen callback method should have this id
	 * */
	protected void returnResponse(int mWhat , int mFrom, int callbackID)
	{
		if (isListenerEnable)
		{
			listener.get(callbackID).onCallbackResult(mResponseMessage.mnCode, mWhat, mFrom,mResponseMessage.mStrContent);
		}
		
	}
	
	
	
	protected void setResponseMessage(int mnCode, HashMap<String,String> message)
	{
		mResponseMessage.mnCode = mnCode;
		mResponseMessage.mStrContent = new HashMap<String,String>(message);
	}

	



}
