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
			// debug using
			// Logs.showTrace("Result: " + String.valueOf(msg.arg1) + " What: "
			// + String.valueOf(msg.what) + " From: "
			// + String.valueOf(msg.arg2) + " message: " + msg.obj);
			if (msg.what == CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER)
			{
				if (msg.arg2 == ResponseCode.METHOD_AMX_COTROL_COMMAND)
				{
					handleControlMessage(msg);
				}
				else if (msg.arg2 == ResponseCode.METHOD_AMX_STATUS_COMMAND)
				{
					handleStatusMessage(msg);
				}
			}
			else if (msg.what == CtrlType.MSG_RESPONSE_AMXBROADCAST_TRANSMIT_HANDLER)
			{
				handleStatusMessage(msg);
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
		switch (msg.arg1)
		{
		// 訊息成功傳送
		case ResponseCode.ERR_SUCCESS:
			callBackMessage(ResponseCode.ERR_SUCCESS, what, ResponseCode.METHOD_AMX_COTROL_COMMAND,
					(HashMap<String, String>) msg.obj);
			break;
		default:
			callBackMessage(msg.arg1, what, ResponseCode.METHOD_AMX_COTROL_COMMAND, (HashMap<String, String>) msg.obj);

			break;

		}

	}

	protected void handleStatusResponseMessage(int what, int from, Message msg)
	{
		callBackMessage(msg.arg1, what, from, (HashMap<String, String>) msg.obj);
	}

	private int getFunctionNumByJsonObj(String message) throws JSONException
	{
		JSONObject broadcastData = new JSONObject(message);
		return broadcastData.getInt("function");

	}

	protected boolean isEqual(int a, int b)
	{
		if (a == b)
		{
			return true;
		}
		return false;
	}

	protected boolean isFunctionIDSame(String message, int functionID) throws JSONException
	{
		int func = getFunctionNumByJsonObj(message);
		return isEqual(func, functionID);

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
