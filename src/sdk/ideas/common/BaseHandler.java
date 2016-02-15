package sdk.ideas.common;

import android.content.Context;
import android.os.Handler;
import sdk.ideas.common.ResponseCode.ResponseMessage;

public abstract class BaseHandler
{
	protected OnCallbackResult listener = null;
	protected Context mContext = null;
	protected Handler theHandler = null;

	protected boolean isHandlerEnable = false;
	protected boolean isListenerEnable = false;

	protected ResponseMessage mResponseMessage = null;

	public BaseHandler(Context context)
	{
		if (null != context)
		{
			mContext = context;
			mResponseMessage = new ResponseMessage();
		}
	}

	public BaseHandler()
	{

	}

	public void setHandler(Handler handler)
	{
		if (null != handler)
		{
			theHandler = handler;
			isHandlerEnable = true;
		}
	}
	
	public void setOnCallbackResultListener(OnCallbackResult listener)
	{
		if (null != listener)
		{
			this.listener = listener;
			isListenerEnable = true;
		}
	}

	/**
	 *  mWhat                        : which MDM module error or not
	 * 	mFrom                        : which MDM module method error or not
	 *  mResponseMessage.mnCode      : error code
	 *  mResponseMessage.mStrContent : detail error message or other functional message 
	 * */
	public void returnRespose(ResponseMessage mResponseMessage, int mWhat, int mFrom)
	{
		if (isListenerEnable)
		{
			listener.onCallbackResult(mResponseMessage.mnCode, mWhat, mFrom,mResponseMessage.mStrContent);
		}
		if (isHandlerEnable)
		{
			Common.postMessage(theHandler, mWhat, mResponseMessage.mnCode, mFrom, mResponseMessage.mStrContent);
		}
	}
	
	public void setResponseMessage(int mnCode,String errorMessage)
	{
		mResponseMessage.mnCode = mnCode;
		mResponseMessage.mStrContent = errorMessage;
		
	}

	



}
