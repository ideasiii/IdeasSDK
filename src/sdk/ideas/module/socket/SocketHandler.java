package sdk.ideas.module.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketHandler extends Socket
{
	private static SocketHandler mSocketHandler = null;

	private SocketHandler(String strIP, int port) throws UnknownHostException, IOException
	{
		super(strIP, port);
	}

	private SocketHandler()
	{
		super();
	}

	public static SocketHandler getInstance(String strIP, int port) throws UnknownHostException, IOException
	{

		if (null == mSocketHandler)
		{
			mSocketHandler = new SocketHandler(strIP, port);
		}
		return mSocketHandler;

	}

	public static SocketHandler getInstance(String strIP, int port, int nConnectTimeOut) throws IOException
	{

		if (null == mSocketHandler)
		{
			mSocketHandler = new SocketHandler();
			mSocketHandler.connect(new InetSocketAddress(strIP, port), nConnectTimeOut);
		}
		return mSocketHandler;

	}

	public static SocketHandler getInstance(String strIP, int port, int nConnectTimeOut, int nReceiveTimeOut)
			throws IOException
	{

		if (null == mSocketHandler)
		{
			mSocketHandler = new SocketHandler();
			mSocketHandler.connect(new InetSocketAddress(strIP, port), nConnectTimeOut);
			mSocketHandler.setSoTimeout(nReceiveTimeOut);
		}
		return mSocketHandler;

	}



}
