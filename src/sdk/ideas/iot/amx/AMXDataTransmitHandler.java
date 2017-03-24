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
import sdk.ideas.module.socket.SocketHandler;
import sdk.ideas.module.socket.SocketHandler.SocketData;

public class AMXDataTransmitHandler extends BaseHandler
{
	private static SocketData commandSendSockectData = null;
	// private static HashMap<String, ArrayList<Integer>> callbackIDSequence =
	// null;

	// private static Thread socketReceiveThread = null;
	private static AMXBroadCastReceiveHandler mAMXBroadCastReceiveHandler = null;

	private String strIP = null;
	private int nPort = -1;
	private Handler privateHandler = new Handler()
	{
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg)
		{
			HashMap<String, String> message = new HashMap<String, String>();
			try
			{
				message = (HashMap<String, String>) msg.obj;
			}
			catch (ClassCastException e)
			{
				Logs.showError(e.toString());
			}
			// debug using
			// Logs.showTrace("Result: " + String.valueOf(msg.arg1) + " what: "
			// + String.valueOf(msg.what) + " From: "
			// + String.valueOf(msg.arg2) + " message: " + message);

			callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_AMXBROADCAST_TRANSMIT_HANDLER, 0, message);

		}

	};

	public AMXDataTransmitHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext);

		if (null == mAMXBroadCastReceiveHandler)
		{
			mAMXBroadCastReceiveHandler = new AMXBroadCastReceiveHandler(mContext, strIP, nPort);
			mAMXBroadCastReceiveHandler.setHandler(privateHandler);
			mAMXBroadCastReceiveHandler.runBroadCastReceiveThread();
		}

		/*
		 * if (null == callbackIDSequence) { callbackIDSequence = new
		 * HashMap<String, ArrayList<Integer>>(); }
		 */
		try
		{
			if (null == commandSendSockectData)
			{
				this.strIP = strIP;
				this.nPort = nPort;
				commandSendSockectData = SocketHandler.getInstance(strIP, nPort);

			}
		}
		catch (Exception e)
		{
			Logs.showError(e.toString());
		}

	}

	public void sendControlCommand(JSONObject command)
	{

		if (null != command)
		{
			Thread t = null;
			try
			{
				t = new Thread(new SocketSendRunnable(Controller.amx_control_command_request, command.toString(),
						String.valueOf(command.getInt("function"))));

				t.start();
			}
			catch (JSONException e)
			{
				Logs.showError(e.toString());
			}
		}
	}

	public void sendStatusCommand(JSONObject command)
	{
		if (null != command)
		{
			Thread t = null;
			try
			{
				Logs.showTrace("[AMXDataTransmitHandler] send Status Command:" + command.toString());
				t = new Thread(new SocketSendRunnable(Controller.amx_status_command_request, command.toString(),
						String.valueOf(command.getInt("function"))));

				t.start();
			}
			catch (JSONException e)
			{
				Logs.showError(e.toString());
			}
		}
	}

	public void setHandler(Handler handler, String handlerID)
	{
		super.setHandler(handler, handlerID);
	}
	
	public void cancelBroadCastThread()
	{
		mAMXBroadCastReceiveHandler.cancelBroadCastRevceiveThread();
	}

	private void analyzeResponseData(int returnstate, Controller.CMP_PACKET respPacket, String callbackID)
	{
		HashMap<String, String> message = new HashMap<String, String>();

		int whichFunction = 0;

		int result = ResponseCode.ERR_SUCCESS;

		switch (returnstate)
		{
		case Controller.STATUS_ROK:
			result = ResponseCode.ERR_SUCCESS;
			message.put("message", "success!");
			break;

		case Controller.ERR_SOCKET_INVALID:
		case Controller.ERR_IOEXCEPTION:
		case Controller.ERR_PACKET_LENGTH:
			result = ResponseCode.ERR_IO_EXCEPTION;
			break;

		case Controller.STATUS_SYSBUSY:
			result = ResponseCode.ERR_SYSTEM_BUSY;
			message.put("message", "AMX Server System Busy!");
			break;

		default:
			Logs.showError("[AMXDataTransmitHandler] return state code num: " + String.valueOf(returnstate));
			result = ResponseCode.ERR_UNKNOWN;
			break;
		}
		

		if (respPacket.cmpHeader.command_id == (Controller.amx_control_command_response & 0x00ffffff))
		{
			whichFunction = ResponseCode.METHOD_AMX_COTROL_COMMAND;
			callBackMessage(result, CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER, whichFunction, message, callbackID);
		}
		else if (respPacket.cmpHeader.command_id == (Controller.amx_status_command_response & 0x00ffffff))
		{
			whichFunction = ResponseCode.METHOD_AMX_STATUS_COMMAND;
			callBackMessage(result, CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER, whichFunction, message, callbackID);
		}

	}

	private class SocketSendRunnable implements Runnable
	{
		private int nCommand = 0;
		private String strBody = null;
		private Controller.CMP_PACKET respPacket = null;
		private String moduleID = null;

		@Override
		public void run()
		{

			mAMXBroadCastReceiveHandler.runBroadCastReceiveThread();

			// debug using
			// Logs.showTrace("[AMXDataTransmitHandler] start to send Command
			// Data:" + strBody);

			int status = Controller.cmpRequest(strIP, nPort, nCommand, strBody, respPacket);

			analyzeResponseData(status, respPacket, moduleID);

			// debug using
			// Logs.showTrace("#####[AMXDataTransmitHandler] CMP Send status" +
			// String.valueOf(status));

		}

		public SocketSendRunnable(int nCommand, String commandString, String moduleID)
		{
			strBody = commandString;
			this.nCommand = nCommand;
			this.moduleID = moduleID;
			respPacket = new Controller.CMP_PACKET();
		}

	}

}
