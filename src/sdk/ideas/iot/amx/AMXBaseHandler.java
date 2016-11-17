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

	private static Thread recieveBroadCastStatusMessage = null;

	protected Handler privateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER)
			{
				Logs.showTrace("NOW COMMAND: " + msg.arg2);
				if (msg.arg2 == ResponseCode.METHOD_COTROL_COMMAND_AMX)
				{
					handleControlMessage(msg);
				}
				else if (msg.arg2 == ResponseCode.METHOD_STATUS_COMMAND_AMX)
				{
					handleStatusMessage(msg);
				}
			}
		}

	};
	protected AMXDataTransmitHandler mAMXDataTransmitHandler = null;

	public AMXBaseHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext);

		mAMXDataTransmitHandler = new AMXDataTransmitHandler(mContext, strIP, nPort);
		mAMXDataTransmitHandler.setHandler(privateHandler);
		if (null != recieveBroadCastStatusMessage && recieveBroadCastStatusMessage.isAlive())
		{
			Logs.showTrace("recieveStatusMessage is Running");
		}
		else
		{
			recieveBroadCastStatusMessage = new Thread();

		}

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
