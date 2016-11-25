package sdk.ideas.module.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class SocketHandler
{
	private static ArrayList<SocketData> mSocketHandler = null;

	private SocketHandler()
	{

	}

	public static SocketData getInstance(String strIP, int port)
	{
		if (null == mSocketHandler)
		{
			mSocketHandler = new ArrayList<SocketData>();
		}
		mSocketHandler.add(new SocketData(strIP, port));

		return mSocketHandler.get(mSocketHandler.size() - 1);

	}

	public static SocketData getInstance(String strIP, int port, int nConnectTimeOut, int nReceiveTimeOut)
			throws IOException
	{

		if (null == mSocketHandler)
		{
			mSocketHandler = new ArrayList<SocketData>();
		}
		mSocketHandler.add(new SocketData(strIP, port, nConnectTimeOut, nReceiveTimeOut));

		return mSocketHandler.get(mSocketHandler.size() - 1);

	}

	public static SocketData getInstance(int socketID)
	{

		if (null != mSocketHandler)
		{
			for (int i = 0; i < mSocketHandler.size(); i++)
			{
				if (mSocketHandler.get(i).getSocketID() == socketID)
				{
					return mSocketHandler.get(i);
				}
			}
		}

		return null;

	}

	public static class SocketData
	{
		private Socket mSocket = null;

		private static int socketCountID = 1;
		private int socketID = -1;
		private boolean isLinkable = false;

		private String strIP = null;
		private int nPort = -1;
		private int nReceiveTimeOut = -1;
		private int nConnectTimeOut = -1;

		public SocketData(String strIP, int nPort, int nReceiveTimeOut, int nConnectTimeOut)
		{
			mSocket = new Socket();
			this.strIP = strIP;
			this.nPort = nPort;
			this.nReceiveTimeOut = nReceiveTimeOut;
			this.nConnectTimeOut = nConnectTimeOut;
			socketID = socketCountID++;
		}

		public SocketData(String strIP, int nPort)
		{
			mSocket = new Socket();
			this.strIP = strIP;
			this.nPort = nPort;
			socketID = socketCountID++;
		}

		public void setIsLinkable(boolean isLinkable)
		{
			this.isLinkable = isLinkable;
		}

		public boolean getIsLinkable()
		{
			return this.isLinkable;
		}

		public void connect() throws SocketException, IOException
		{
			if (nReceiveTimeOut > 0)
			{
				mSocket.setSoTimeout(nReceiveTimeOut);
			}

			if (nConnectTimeOut > 0)
			{
				mSocket.connect(new InetSocketAddress(strIP, nPort), nConnectTimeOut);
			}
			else
			{
				mSocket.connect(new InetSocketAddress(strIP, nPort));
			}
			this.isLinkable = true;

		}

		public boolean isConnected()
		{
			return mSocket.isConnected();
		}

		public Socket getSocket()
		{
			return mSocket;
		}

		public int getSocketID()
		{
			return socketID;
		}

	}

}
