package sdk.ideas.pptcontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import sdk.ideas.common.Logs;

class ReceiverDiscoveryThread extends Thread
{
	private volatile boolean mCalledStop = false;
	private volatile EventListener mListener;
	private String mMulticastGroupAddress;
	private int mPort;

	private volatile MulticastSocket mMulticastSocket = null;

	public interface EventListener
	{
		public void onDiscoverReceiver(String ip, int port);

		public void onIOException(String message);
	}

	public ReceiverDiscoveryThread(String multicastGroupAddress, int port, EventListener listener)
	{
		mListener = listener;
		mMulticastGroupAddress = multicastGroupAddress;
		mPort = port;
	}
	
	public void removeListener()
	{
		mListener = null;
	}

	public void stopDiscovery()
	{
		Logs.showTrace("stopping receiver discovery");
		mCalledStop = true;

		try
		{
			if (mMulticastSocket != null)
			{
				mMulticastSocket.leaveGroup(InetAddress.getByName(mMulticastGroupAddress));
				mMulticastSocket.close();
			}
		}
		catch (Exception e)
		{
			// Don't care
		}

		mMulticastSocket = null;
	}

	@Override
	public void run()
	{
		Logs.showTrace("starting receiver discovery");
		
		try
		{
			// TODO 在僅開啟無線熱點的狀況下無法使用
			mMulticastSocket = new MulticastSocket(mPort);
			mMulticastSocket.joinGroup(InetAddress.getByName(mMulticastGroupAddress));
		}
		catch (IOException e)
		{
			e.printStackTrace();

			if (mListener != null)
			{
				mListener.onIOException(e.toString());
			}

			return;
		}

		byte buf[] = new byte[2048];
		DatagramPacket dgPacket = new DatagramPacket(buf, buf.length);

		while (!mCalledStop)
		{
			try
			{
				if (mMulticastSocket != null)
				{
					mMulticastSocket.receive(dgPacket);
				}
			}
			catch (IOException e)
			{
				if (mCalledStop)
				{
					return;
				}

				e.printStackTrace();
				
				// TODO how if wifi is off , will we receive this?
				if (mListener != null)
				{
					mListener.onIOException(e.toString());
				}

				// TODO should we continue if wifi is turned off??
				continue;
			}

			ArrayList<byte[]> localIpAddresses = getLocalIpv4Address();
			byte[] srcIpAddress = dgPacket.getAddress().getAddress();
			String srcHostAddress = dgPacket.getAddress().getHostAddress();

			// only consider IPv4 address
			if (localIpAddresses == null || localIpAddresses.size() == 0)
			{
				Logs.showTrace("localIpAddress is empty");
				continue;
			}

			if (srcIpAddress == null || srcIpAddress.length != 4)
			{
				Logs.showTrace(String.format("fail! srcLen = %d", srcIpAddress.length));
				continue;
			}

			// check if both IP are in the same network
			boolean isInSameNetwork = false;
			for (byte[] localIpAddress : localIpAddresses)
			{
				boolean matches = true;
				for (int b = 0; b < 3; b++)
				{
					if (srcIpAddress[b] != localIpAddress[b])
					{
						matches = false;
						break;
					}
				}
				if (matches)
				{
					isInSameNetwork = true;
					break;
				}
			}

			if (!isInSameNetwork)
			{
				Logs.showTrace("Not in same network: " + srcHostAddress);
				continue;
			}

			if (mListener != null)
			{
				mListener.onDiscoverReceiver(srcHostAddress, 99999);
			}

		}
	}

	private ArrayList<byte[]> getLocalIpv4Address()
	{
		ArrayList<byte[]> addrs = new ArrayList<byte[]>();

		try
		{
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();

			while (en.hasMoreElements())
			{
				NetworkInterface intf = en.nextElement();
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();

				while (enumIpAddr.hasMoreElements())
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					// only return IPv4 address
					if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4)
					{
						addrs.add(inetAddress.getAddress());
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// Logs.showTrace(e.toString());
		}

		return addrs;
	}
};