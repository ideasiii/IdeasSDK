package sdk.ideas.pptcontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpSocketWrapper
{
	private DatagramSocket mSocket;
	private InetAddress mInetAddress;
	private String mAddress;
	private int mPort;
	private DatagramPacket mSendPacket;
	private byte[] mSendData = new byte[64];

	/**
	 * 一個負責傳送 UDP 封包到指定地址和連接埠的類別。<br>
	 * 綁定到本機的連接埠是隨機的。
	 * 
	 * @param dstAddress
	 *            遠端的地址
	 * @param desPort
	 *            遠端的連接埠
	 */
	public UdpSocketWrapper(String dstAddress, int desPort)
	{
		mAddress = dstAddress;
		mPort = desPort;
	}

	public void connect() throws SocketException, UnknownHostException
	{
		mInetAddress = InetAddress.getByName(mAddress);
		SocketAddress socketAddress = new InetSocketAddress(mInetAddress, mPort);
		mSendPacket = new DatagramPacket(mSendData, mSendData.length);

		// mSocket.connect(mInetAddress, mPort);
		mSocket = new DatagramSocket();
		mSocket.connect(socketAddress);
	}

	public synchronized void sendMsg(String msg) throws IOException
	{
		mSendPacket.setData(msg.getBytes());

		try
		{
			mSocket.send(mSendPacket);
		}
		finally
		{
			mSendData = null;
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

	public synchronized void receive(DatagramPacket pack) throws IOException
	{
		pack.setPort(mPort);
		pack.setAddress(mInetAddress);
		mSocket.receive(pack);
	}

	public void close()
	{
		// DO NOT call disconnect() when receive() is in progress
		// on API 25 or later this will hang disconnect()

		// mSocket.disconnect();
		mSocket.close();
	}

	public boolean isClosed()
	{
		return mSocket.isClosed();
	}

	public boolean isConnected()
	{
		return mSocket.isConnected();
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();

		close();
	}
}
