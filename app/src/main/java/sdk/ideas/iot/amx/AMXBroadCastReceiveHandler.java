package sdk.ideas.iot.amx;

import java.io.IOException;
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

	public void cancelBroadCastRevceiveThread()
	{
		if (null != recieveBroadCastReceiveThread && recieveBroadCastReceiveThread.isAlive())
		{
			recieveBroadCastReceiveThread.interrupt();
			recieveBroadCastReceiveThread = null;
		}

	}

	public void runBroadCastReceiveThread()
	{
		if (null != recieveBroadCastReceiveThread && recieveBroadCastReceiveThread.isAlive())
		{
			// debug using
			Logs.showTrace("[AMXBroadCastReceiveHandler] RecieveBroadCastReceiveThread is Running");
		}
		else
		{
			// debug using
			Logs.showTrace("[AMXBroadCastReceiveHandler] RecieveBroadCastReceiveThread is died, reconnecting");
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
					mSocketData.closeSocket();
					Logs.showError(e.toString());
					return;
				}
			}

			// 先下bind指令與AMX Controller連線並繫結
			int bindStatus = Controller.ERR_EXCEPTION;
			try
			{
				JSONObject tmp = new JSONObject();
				tmp.put("id", null);
				bindStatus = Controller.cmpRequest(Controller.bind_request, tmp.toString(), respPacket,
						mSocketData.getSocket());
			}
			catch (JSONException e)
			{
				Logs.showError(e.toString());
			}
			catch (Exception e)
			{
				Logs.showError("[AMXBroadCastReceiveHandler] send bind request ERROR: ");
				Logs.showError(e.toString());
			}

			if (bindStatus == Controller.STATUS_ROK)
			{
				boolean anyERROR = false;
				while (!Thread.currentThread().isInterrupted())
				{

					CMP_PACKET receivePacket = new CMP_PACKET();
					int boardcastStatus = Controller.cmpReceive(receivePacket, mSocketData.getSocket(), -1);

					if (boardcastStatus == Controller.STATUS_ROK)
					{
						HashMap<String, String> message = new HashMap<String, String>();
						message.put("message", receivePacket.cmpBody);

						// debug using
						// Logs.showTrace("[AMXBroadCastReceiveHandler]
						// Receive Message: " + receivePacket.cmpBody);

						callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_AMXBROADCAST_TRANSMIT_HANDLER,
								0, message);

						// response message start
						CMP_PACKET sendPacket = new CMP_PACKET();
						int sendStatus = Controller.cmpSend(Controller.amx_broadcast_status_command_response, null,
								sendPacket, mSocketData.getSocket(), receivePacket.cmpHeader.sequence_number);
						sendPacket = null;
						if (sendStatus == Controller.STATUS_ROK)
						{
							// do nothing
						}
						else
						{
							// do something handle ERROR
							Logs.showTrace("[AMXBroadCastReceiveHandler] get sendStatus " + String.valueOf(sendStatus));
							anyERROR = true;
							break;
						}
						// response message end

					}
					else if (boardcastStatus == Controller.ERR_IOEXCEPTION
							|| boardcastStatus == Controller.ERR_SOCKET_INVALID)
					{
						Logs.showError("[AMXBroadCastReceiveHandler] Broken Socket IO Exception! while Receiving");
						anyERROR = true;
						break;
					}

					receivePacket = null;

				}

				// end boardcast receiver
				if (anyERROR == false)
				{
					CMP_PACKET sendPacket = new CMP_PACKET();
					CMP_PACKET receivePacket = new CMP_PACKET();
					Controller.cmpSend(Controller.unbind_request, null, sendPacket,
							mSocketData.getSocket(), receivePacket.cmpHeader.sequence_number);
					
					sendPacket = null;
					receivePacket = null;

				}

				mSocketData.closeSocket();

			}
			else
			{
				// bind failed
				Logs.showError("[AMXBroadCastReceiveHandler] Broken Socket IO Exception while Binding");
				mSocketData.closeSocket();

			}

		}

		public SocketListenRunnable()
		{
			respPacket = new Controller.CMP_PACKET();
		}

	}

}
