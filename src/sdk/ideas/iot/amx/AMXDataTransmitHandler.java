package sdk.ideas.iot.amx;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Handler;
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
	// private static int commandSendSockectID = -1;
	private static HashMap<String, ArrayList<Integer>> callbackIDSequence = null;

	private static Thread socketReceiveThread = null;
	private static boolean socketReceiveThreadAlive = false;

	public AMXDataTransmitHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext);

		if (null == callbackIDSequence)
		{
			callbackIDSequence = new HashMap<String, ArrayList<Integer>>();
		}

		try
		{
			if (null == commandSendSockectData)
			{
				commandSendSockectData = SocketHandler.getInstance(strIP, nPort);

				if (null != socketReceiveThread && socketReceiveThread.isAlive())
				{
					Logs.showTrace("[AMXDataTransmitHandler] SocketReceiveThread is running");
				}
				else
				{
					socketReceiveThread = new Thread(new SocketReceiveRunnable());
					socketReceiveThread.start();
				}
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
			// command.getInt("function");

			Thread t;
			try
			{
				t = new Thread(new SocketSendRunnable(Controller.amx_control_command_request, command.toString(),
						String.valueOf(command.getInt("function"))));

				t.start();
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void sendStatusCommand(JSONObject command)
	{
		if (null != command)
		{
			Thread t;
			try
			{
				t = new Thread(new SocketSendRunnable(Controller.amx_status_command_request, command.toString(),
						String.valueOf(command.getInt("function"))));

				t.start();
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setHandler(Handler handler, String handlerID)
	{
		if (null == callbackIDSequence.get(handlerID))
		{
			callbackIDSequence.put(handlerID, new ArrayList<Integer>());
		}
		super.setHandler(handler, handlerID);
	}

	private void analyzeResponseData(int returnstate, Controller.CMP_PACKET respPacket, String callbackID)
	{
		HashMap<String, String> message = new HashMap<String, String>();

		int whichFunction = 0;

		if (returnstate != respPacket.cmpHeader.command_status)
		{
			Logs.showError("@@Return State@@: " + String.valueOf(returnstate));
		}
		else
		{
			if (respPacket.cmpHeader.command_id == (Controller.amx_control_command_response & 0x00ffffff))
			{
				whichFunction = ResponseCode.METHOD_COTROL_COMMAND_AMX;
				callBackMessage(respPacket.cmpHeader.command_status, CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER,
						whichFunction, message, callbackID);
			}
			else if (respPacket.cmpHeader.command_id == (Controller.amx_status_command_response & 0x00ffffff))
			{
				whichFunction = ResponseCode.METHOD_STATUS_COMMAND_AMX;
				callBackMessage(respPacket.cmpHeader.command_status, CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER,
						whichFunction, message, callbackID);
			}
			else
			{
				Logs.showError("@@UNKnown Command ID@@");
			}
		}

	}

	private class SocketReceiveRunnable implements Runnable
	{

		private Controller.CMP_PACKET receivePacket = null;

		@Override
		public void run()
		{
			socketReceiveThreadAlive = true;
			while (true)
			{
				if (!commandSendSockectData.isConnected() || !commandSendSockectData.getIsLinkable())
				{
					try
					{
						commandSendSockectData.connect();
						commandSendSockectData.setIsLinkable(true);
					}
					catch (IOException e)
					{
						Logs.showError(e.toString());

						commandSendSockectData.setIsLinkable(false);
						socketReceiveThreadAlive = false;

						break;
					}
				}

				int status = Controller.cmpReceive(receivePacket, commandSendSockectData.getSocket(), -1);
				int sequence = receivePacket.cmpHeader.sequence_number;
				boolean isFind = false;
				for (String callbackID : callbackIDSequence.keySet())
				{

					if (callbackIDSequence.get(callbackID).contains(sequence))
					{
						Logs.showTrace("#$#: remove before callbackID: " + String.valueOf(callbackID) + " size: "
								+ String.valueOf(callbackIDSequence.get(callbackID).size()));

						callbackIDSequence.get(callbackID).remove(Integer.valueOf(sequence));

						Logs.showTrace("#%#: remove after callbackID: " + String.valueOf(callbackID) + " size: "
								+ String.valueOf(callbackIDSequence.get(callbackID).size()));

						isFind = true;
						analyzeResponseData(status, receivePacket, callbackID);
						break;
					}
				}
				if (isFind == false)
				{
					Logs.showTrace("[AMXDataTransmitHandler] Cannot found this sequence: " + String.valueOf(sequence));
				}

			}

		}

		public SocketReceiveRunnable()
		{
			receivePacket = new Controller.CMP_PACKET();
		}

	}

	private class SocketSendRunnable implements Runnable
	{

		private int nCommand = 0;
		private String strBody = null;
		private Controller.CMP_PACKET sendPacket = null;
		private String moduleID = null;

		@Override
		public void run()
		{
			if (!commandSendSockectData.isConnected() || !commandSendSockectData.getIsLinkable())
			{
				try
				{
					commandSendSockectData.connect();
				}
				catch (IOException e)
				{
					Logs.showError(e.toString());
				}
			}

			if (socketReceiveThreadAlive == false)
			{
				
			}

			int status = Controller.cmpSend(nCommand, strBody, sendPacket, commandSendSockectData.getSocket());

			if (status == Controller.STATUS_ROK)
			{
				if (null != callbackIDSequence.get(moduleID))
				{
					callbackIDSequence.get(moduleID).add(sendPacket.cmpHeader.sequence_number);
				}
			}
			else
			{
				Logs.showError("@@[AMXDataTransmitHandler]send Message occur ERROR: status num: "
						+ String.valueOf(status) + " @@");
				commandSendSockectData.setIsLinkable(false);
			}

			sendPacket = null;

		}

		public SocketSendRunnable(int nCommand, String commandString, String moduleID)
		{
			strBody = commandString;
			this.nCommand = nCommand;
			this.moduleID = moduleID;
			sendPacket = new Controller.CMP_PACKET();
		}

	}

}
