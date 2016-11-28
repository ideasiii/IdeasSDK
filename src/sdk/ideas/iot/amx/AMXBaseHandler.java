package sdk.ideas.iot.amx;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.module.Controller;

public abstract class AMXBaseHandler extends BaseHandler
{
	protected abstract void handleControlMessage(Message msg);

	protected abstract void handleStatusMessage(Message msg);

	protected Handler privateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			//Logs.showTrace("Result: " + String.valueOf(msg.arg1) + " What: " + String.valueOf(msg.what) + " From: "
				//	+ String.valueOf(msg.arg2) + " message: " + msg.obj);
			if (msg.what == CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER)
			{
				if (msg.arg2 == ResponseCode.METHOD_COTROL_COMMAND_AMX)
				{
					handleControlMessage(msg);
				}
				else if (msg.arg2 == ResponseCode.METHOD_STATUS_COMMAND_AMX)
				{
					handleStatusMessage(msg);
				}
			}
			if(msg.what == CtrlType.MSG_RESPONSE_AMXBROADCAST_TRANSMIT_HANDLER)
			{
				
				//Logs.showTrace("Broadcast Status message: " + msg.obj);
				
			}
		}

	};
	protected static AMXDataTransmitHandler mAMXDataTransmitHandler = null;

	public AMXBaseHandler(Context mContext, String strIP, int nPort, String moduleID)
	{
		super(mContext);

		if (null == mAMXDataTransmitHandler)
		{
			mAMXDataTransmitHandler = new AMXDataTransmitHandler(mContext, strIP, nPort);
		}
		mAMXDataTransmitHandler.setHandler(privateHandler, moduleID);

	}

	protected boolean isInInterval(int num, int smallestNum, int biggestNum)
	{
		if (num <= biggestNum && num >= smallestNum)
		{
			return true;
		}
		return false;
	}

	protected void handleControlResponseMessage(int what, Message msg)
	{
		HashMap<String, String> message = new HashMap<String, String>();

		switch (msg.arg1)
		{
		// 訊息成功傳送
		case Controller.STATUS_ROK:
			message.put("message", "success");
			super.callBackMessage(ResponseCode.ERR_SUCCESS, what, ResponseCode.METHOD_COTROL_COMMAND_AMX, message);
			break;

		// Json 格式丟錯
		case Controller.STATUS_RINVJSON:

			break;
		default:
			if (msg.arg1 < Controller.ERR_CMP)
			{
				// 內部Exception

			}
			else
			{
				// socket 訊息有通

			}
			break;

		}

	}

	protected void handleStatusResponseMessage(int what, Message msg)
	{
		// 做彈性化處理 有可能會收到status ok或是有 body的data
		HashMap<String, String> message = (HashMap<String, String>) msg.obj;
		JSONObject data = null;
		switch (msg.arg1)
		{
		// 收到成功查詢訊息結果
		case Controller.STATUS_ROK:

			super.callBackMessage(ResponseCode.ERR_SUCCESS, what, ResponseCode.METHOD_STATUS_COMMAND_AMX, message);

			break;

		// Json 格式丟錯
		case Controller.STATUS_RINVJSON:

			break;

		default:

			break;

		}

	}

	protected void handleBroadCastStatusMessage(int what, Message msg)
	{

	}

	protected JSONObject trasferToJsonCommand(int commandType, int function, int device, int command)
	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("function", function);

			obj.put("device", device);

			switch (commandType)
			{
			case AMXParameterSetting.TYPE_CONTROL_COMMAND:
				obj.put("control", command);

				break;
			case AMXParameterSetting.TYPE_STATUS_COMMAND:
				obj.put("request-status", command);
				break;

			}
		}
		catch (JSONException e)
		{
			Logs.showError(e.toString());
			return null;
		}
		return obj;

	}

}
