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

	public static SocketHandler getInstance()
	{
		if (null == mSocketHandler)
		{
			mSocketHandler = new SocketHandler();
		}
		return mSocketHandler;
	}

	public static SocketHandler getInstance(String strIP, int port) throws UnknownHostException, IOException
	{

		if (null == mSocketHandler)
		{
			mSocketHandler = new SocketHandler();
			mSocketHandler.connect(new InetSocketAddress(strIP, port));
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
			mSocketHandler.setSoTimeout(nReceiveTimeOut);
			mSocketHandler.connect(new InetSocketAddress(strIP, port), nConnectTimeOut);
		}
		return mSocketHandler;

	}

	/*
	 * public static class SocketRunable implements Runnable { private String
	 * strIP = null; private int port = 0; private int nConnectTimeOut = 0;
	 * private int nReceiveTimeOut = 0; private int type = -1;
	 * 
	 * @Override public void run() { try { switch (type) { case 0:
	 * mSocketHandler = new SocketHandler(strIP, port); break; case 1:
	 * mSocketHandler = new SocketHandler(); mSocketHandler.connect(new
	 * InetSocketAddress(strIP, port), nConnectTimeOut); break; case 2:
	 * mSocketHandler = new SocketHandler();
	 * mSocketHandler.setSoTimeout(nReceiveTimeOut); mSocketHandler.connect(new
	 * InetSocketAddress(strIP, port), nConnectTimeOut); break;
	 * 
	 * } } catch (Exception e) { throw new RuntimeException("Socket Exception",
	 * e); }
	 * 
	 * }
	 * 
	 * public SocketRunable(String strIP, int port) { type = 0; this.strIP =
	 * strIP; this.port = port;
	 * 
	 * }
	 * 
	 * public SocketRunable(String strIP, int port, int nConnectTimeOut) { type
	 * = 1; this.strIP = strIP; this.port = port; this.nConnectTimeOut =
	 * nConnectTimeOut;
	 * 
	 * }
	 * 
	 * public SocketRunable(String strIP, int port, int nConnectTimeOut, int
	 * nReceiveTimeOut) { type = 2; this.strIP = strIP; this.port = port;
	 * this.nConnectTimeOut = nConnectTimeOut; this.nReceiveTimeOut =
	 * nReceiveTimeOut; }
	 * 
	 * }
	 */

}
