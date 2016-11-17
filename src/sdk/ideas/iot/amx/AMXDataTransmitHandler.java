package sdk.ideas.iot.amx;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import org.json.JSONObject;
import android.content.Context;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.module.Controller;
import sdk.ideas.module.socket.SocketHandler;

public class AMXDataTransmitHandler extends BaseHandler
{
	private static SocketHandler mSocketHandler = null;
	private String strIP = null;
	private int port = 0;

	public AMXDataTransmitHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext);

		this.strIP = strIP;
		this.port = nPort;

		try
		{
			if (null == mSocketHandler)
			{
				mSocketHandler = SocketHandler.getInstance();
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
			Thread t = new Thread(new SocketRunnable(Controller.amx_control_command_request, command.toString()));
			t.start();
		}
	}

	public void sendStatusCommand(JSONObject command)
	{
		if (null != command)
		{
			Thread t = new Thread(new SocketRunnable(Controller.amx_status_command_request, command.toString()));
			t.start();
		}
	}

	private void analyzeResponseData(int returnstate, Controller.CMP_PACKET respPacket)
	{
		HashMap<String, String> message = new HashMap<String, String>();

		int whichFunction = 0;

		if (respPacket.cmpHeader.command_id == (Controller.amx_control_command_response & 0x00ffffff))
		{
			whichFunction = ResponseCode.METHOD_COTROL_COMMAND_AMX;
		}
		else if (respPacket.cmpHeader.command_id == (Controller.amx_status_command_response& 0x00ffffff))
		{
			whichFunction = ResponseCode.METHOD_STATUS_COMMAND_AMX;

			Logs.showTrace("Status Message" + respPacket.cmpBody);

			message.put("message", respPacket.cmpBody);
		}
		else
		{
			Logs.showError("@@UNKnown Command ID@@");
		}

		if (returnstate != respPacket.cmpHeader.command_status)
		{
			Logs.showTrace("Return State: "+ String.valueOf(returnstate));
		}
		else
		{
			callBackMessage(respPacket.cmpHeader.command_status, CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER,
					whichFunction, message);
		}
	}

	private class SocketRunnable implements Runnable
	{

		private int nCommand = 0;
		private String strBody = null;
		private Controller.CMP_PACKET respPacket = null;

		@Override
		public void run()
		{
			if (!mSocketHandler.isConnected())
			{
				try
				{
					mSocketHandler.connect(
							new InetSocketAddress(AMXDataTransmitHandler.this.strIP, AMXDataTransmitHandler.this.port));
				}
				catch (IOException e)
				{
					Logs.showError(e.toString());
				}
			}

			// for debugging Start
			Logs.showTrace("AMX Request: ");
			Logs.showTrace("Command ID: " + String.valueOf(nCommand));
			if (null != strBody)
			{
				Logs.showTrace("Request Message: " + strBody);
			}
			// for debugging End

			int status = Controller.cmpRequest(nCommand, strBody, respPacket, mSocketHandler);

			analyzeResponseData(status, respPacket);

		}

		public SocketRunnable(int nCommand, String commandString)
		{
			strBody = commandString;
			this.nCommand = nCommand;

			respPacket = new Controller.CMP_PACKET();

		}

	}

}
