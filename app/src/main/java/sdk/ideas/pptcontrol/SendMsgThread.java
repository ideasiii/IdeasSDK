package sdk.ideas.pptcontrol;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import sdk.ideas.common.Logs;

class SendMsgThread extends UdpSocketThread
{
	private final ConcurrentLinkedQueue<String> mCmdQueue = new ConcurrentLinkedQueue<String>();
	private volatile EventListener mListener;

	public interface EventListener
	{
		void onIOException(String message);

		void onCommandSent(String message);
	}

	public SendMsgThread(String address, int port, EventListener listener)
	{
		super(address, port);

		mListener = listener;
	}

	public void removeListener()
	{
		mListener = null;
	}

	public void enqueue(String command)
	{
		mCmdQueue.add(command);
	}

	@Override
	public void run()
	{
		while (!explicitClosed && null != mSocket && !mSocket.isClosed())
		{
			while (mCmdQueue.isEmpty())
			{
				try
				{
					Thread.sleep(200);
				}
				catch (InterruptedException e)
				{
					// e.printStackTrace();
				}

				if (explicitClosed || null == mSocket || mSocket.isClosed())
				{
					return;
				}
			}

			if (explicitClosed || null == mSocket || mSocket.isClosed())
			{
				return;
			}

			String queueCmd = mCmdQueue.poll();
			if (null != queueCmd)
			{
				Logs.showTrace("Send Command:" + queueCmd);
				try
				{
					if (!explicitClosed && null != mSocket && !mSocket.isClosed())
					{
						mSocket.sendMsg(queueCmd);

						if (mListener != null)
						{
							mListener.onCommandSent("command sent");
						}

					}
				}
				catch (IOException e)
				{
					if (explicitClosed)
					{
						// exception caused by closeSocket()
						// this is expected
						return;
					}

					e.printStackTrace();
					Logs.showTrace("send error");

					if (mListener != null)
					{
						mListener.onIOException(e.toString());
					}

					// continue try to send
				}
			}
		}
	}

}