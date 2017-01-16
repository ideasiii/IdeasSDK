package sdk.ideas.pptcontrol;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

abstract class UdpSocketThread extends Thread
{
	protected UdpSocketWrapper mSocket = null;
	protected volatile boolean explicitClosed = false;

	public UdpSocketThread(String address, int port)
	{
		mSocket = new UdpSocketWrapper(address, port);
	}

	public void connect() throws SocketException, UnknownHostException
	{
		mSocket.connect();
	}

	public void closeSocket()
	{
		explicitClosed = true;

		if (null != mSocket)
		{
			mSocket.close();
			mSocket = null;
		}
	}

	public int getLocalPort()
	{
		return mSocket.getLocalPort();
	}

	public InetAddress getInetAddress()
	{
		return mSocket.getInetAddress();
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		closeSocket();
	}
}