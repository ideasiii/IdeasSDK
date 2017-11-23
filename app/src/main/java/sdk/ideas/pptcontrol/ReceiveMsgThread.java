package sdk.ideas.pptcontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import sdk.ideas.common.Logs;

class ReceiveMsgThread extends UdpSocketThread
{
	private volatile EventListener mListener;

	public interface EventListener
	{
		public void onCommandAck(String message);

		public void onSlideIndex(String message, String index, String total);

		public void onIOException(String message);
	}

	public ReceiveMsgThread(String address, int port, EventListener listener)
	{
		super(address, port);

		mListener = listener;
	}

	public void removeListener()
	{
		mListener = null;
	}

	@Override
	public void run()
	{
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		while (null != mSocket && !mSocket.isClosed())
		{
			try
			{
				mSocket.receive(packet);
			}
			catch (IOException e)
			{
				if (explicitClosed)
				{
					// exception is caused by closeSocket()
					// this is expected
					break;
				}

				e.printStackTrace();
				Logs.showTrace("recieve error");

				if (mListener != null)
				{
					mListener.onIOException(e.toString());
				}

				// wait for networking connection back again
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e1)
				{
				}

				continue;
			}

			// resolve received message
			String recv = new String(packet.getData()).substring(0, packet.getLength());
			String[] msgs = recv.split(Consts.CMD_PREFIX);
			for (String msg : msgs)
			{
				if (msg.length() < 1)
				{
					continue;
				}

				if (msg.startsWith(Consts.FROM_SERVER_MSG_PPT_PAGE))
				{
					String[] tmp = msg.substring(4).split(Consts.CMD_PARAM_SEPERATOR);

					if (mListener != null)
					{
						mListener.onSlideIndex("server sent slide index", tmp[0], tmp[1]);
					}
				}
				else if (msg.startsWith(Consts.FROM_SERVER_MSG_SEND_COMMAND_ACK))
				{
					if (mListener != null)
					{
						mListener.onCommandAck("got cmd ack from server");
					}
				}
			}
		}
	}
}