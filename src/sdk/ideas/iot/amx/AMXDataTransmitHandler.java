package sdk.ideas.iot.amx;

import java.net.Socket;
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
	protected static SocketHandler mSocketHandler = null;

	public AMXDataTransmitHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext);

		try
		{
			if (null == mSocketHandler)
			{
				mSocketHandler = SocketHandler.getInstance(strIP, nPort);
			}
		}
		catch (Exception e)
		{
			Logs.showError(e.toString());
		}

	}


	public void sendControlCommand(JSONObject command)
	{
		Thread t = new Thread(new SocketRunnable(Controller.amx_control_command_request, command.toString()));
		t.start();
	}

	public void sendStatusCommand(JSONObject command)
	{
		Thread t = new Thread(new SocketRunnable(Controller.amx_status_command_request, command.toString()));
		t.start();
	}

	private void analyzeResponseData(int returnstate, Controller.CMP_PACKET respPacket)
	{
		// for debugging Start
		Logs.showTrace("AMX Response: ");
		Logs.showTrace("Command ID: " + String.valueOf(respPacket.cmpHeader.command_id));
		Logs.showTrace("Command Length: " + String.valueOf(respPacket.cmpHeader.command_length));
		Logs.showTrace("Command Status: " + String.valueOf(respPacket.cmpHeader.command_status));
		Logs.showTrace("Sequence Number: " + String.valueOf(respPacket.cmpHeader.sequence_number));
		if (null != respPacket.cmpBody)
		{
			Logs.showTrace("Response Message: " + respPacket.cmpBody);
		}
		// for debugging End

		HashMap<String, String> message = new HashMap<String, String>();
		message.put("message", respPacket.cmpBody);

		int whichFunction = 0;

		if (respPacket.cmpHeader.command_id == Controller.amx_control_command_response)
		{
			whichFunction = ResponseCode.METHOD_COTROL_COMMAND_AMX;
		}
		else if (respPacket.cmpHeader.command_id == Controller.amx_status_command_response)
		{
			whichFunction = ResponseCode.METHOD_STATUS_COMMAND_AMX;
		}


		callBackMessage(respPacket.cmpHeader.command_status, CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER,
				whichFunction, message);
	}

	private class SocketRunnable implements Runnable
	{

		private int nCommand = 0;
		private String strBody = null;
		private Controller.CMP_PACKET respPacket = null;

		@Override
		public void run()
		{
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
