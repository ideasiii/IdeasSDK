package sdk.ideas.iot.amx;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.module.Controller;
import sdk.ideas.module.Controller.CMP_PACKET;
import sdk.ideas.module.socket.SocketHandler;
import sdk.ideas.module.socket.SocketHandler.SocketData;

public class AMXBroadCastReceiveHandler extends BaseHandler
{
	private static Thread recieveBroadCastReceiveThread = null;
	private static SocketData mSocketData = null;

	private String strIP = null;
	private int nPort = -1;

	public AMXBroadCastReceiveHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext);
		this.strIP = strIP;
		this.nPort = nPort;

	}

	public void runBroadCastReceiveThread()
	{
		if (null != recieveBroadCastReceiveThread && recieveBroadCastReceiveThread.isAlive())
		{
			Logs.showTrace("recieveBroadCastReceiveThread is Running");
		}
		else
		{
			mSocketData = SocketHandler.getInstance(strIP, nPort);
			recieveBroadCastReceiveThread = new Thread(new SocketListenRunnable());
			recieveBroadCastReceiveThread.start();
		}
	}

	class SocketListenRunnable implements Runnable
	{
		private Controller.CMP_PACKET respPacket = null;

		@Override
		public void run()
		{
			if (!mSocketData.isConnected())
			{
				try
				{
					mSocketData.connect();
				}
				catch (IOException e)
				{
					Logs.showError(e.toString());
				}
			}
			// 先下bind指令與AMX Controller連線並繫結
			JSONObject tmp = new JSONObject();
			try
			{
				tmp.put("id", null);
			}
			catch (JSONException e)
			{
				Logs.showError(e.toString());
			}
			int bindStatus = Controller.ERR_EXCEPTION;
			try
			{
				bindStatus = Controller.cmpRequest(Controller.bind_request, tmp.toString(), respPacket,
						mSocketData.getSocket());
			}
			catch (Exception e)
			{
				Logs.showError("[AMXBroadCastReceiveHandler] send bind request ERROR: ");
				Logs.showError(e.toString());
			}

			if (bindStatus == Controller.STATUS_ROK)
			{
				while (true)
				{

					CMP_PACKET receivePacket = new CMP_PACKET();
					Logs.showTrace("Now Start to Receive BroadCast Message!");
					int boardcastStatus = Controller.cmpReceive(receivePacket, mSocketData.getSocket(), -1);
					Logs.showTrace("End to Receive BroadCast Message!");
					if (boardcastStatus == Controller.STATUS_ROK)
					{
						HashMap<String, String> message = new HashMap<String, String>();
						message.put("message", receivePacket.cmpBody);
						Logs.showTrace("[AMXBroadCastReceiveHandler] Receive Message: " + receivePacket.cmpBody);
						callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_AMXBROADCAST_TRANSMIT_HANDLER,
								0, message);

						// response message
						CMP_PACKET sendPacket = new CMP_PACKET();
						int sendStatus = Controller.cmpSend(Controller.amx_broadcast_status_command_response, null,
								sendPacket, mSocketData.getSocket(), receivePacket.cmpHeader.sequence_number);
						if (sendStatus == Controller.STATUS_ROK)
						{

						}

					}
					else if (boardcastStatus == Controller.ERR_IOEXCEPTION
							|| boardcastStatus == Controller.ERR_SOCKET_INVALID)
					{
						Logs.showError("[AMXBroadCastReceiveHandler] Broken Socket IO Exception! while Receiving");

						break;
					}

				}
			}
			else
			{
				// bind failed
				Logs.showError("[AMXBroadCastReceiveHandler] Broken Socket IO Exception while Binding");

			}

		}

		public SocketListenRunnable()
		{
			respPacket = new Controller.CMP_PACKET();
		}

	}

}
