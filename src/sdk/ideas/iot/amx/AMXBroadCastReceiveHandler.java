package sdk.ideas.iot.amx;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.module.Controller;
import sdk.ideas.module.socket.SocketHandler;
import sdk.ideas.module.socket.SocketHandler.SocketData;

public class AMXBroadCastReceiveHandler extends BaseHandler
{
	private static Thread recieveBroadCastReceiveThread = null;
	private static SocketData mSocketData = null;

	public AMXBroadCastReceiveHandler(Context mContext, String strIP, int nPort)
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
			int status = Controller.cmpRequest(Controller.bind_request, tmp.toString(), respPacket,
					mSocketData.getSocket());

			if (status == Controller.STATUS_ROK)
			{
				while (true)
				{
					//socket 持續收訊息
					try
					{
						InputStream inSocket = null;
						inSocket = mSocketData.getSocket().getInputStream();
						
						
						
						
						
						
						
					}
					catch (IOException e)
					{
						
						
						
					}
					
					
					
					
				}
			}
			else
			{

			}

		}

		public SocketListenRunnable()
		{
			respPacket = new Controller.CMP_PACKET();
		}

	}

}
