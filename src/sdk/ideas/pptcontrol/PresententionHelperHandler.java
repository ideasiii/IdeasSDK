package sdk.ideas.pptcontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import android.content.Context;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Microsoft PowerPoint (Windows) 簡報控制 client 端<br>
 * 此 Handler 透過 Wi-Fi 連線至 PC 上的接收端軟體並進行簡報的控制
 */
public class PresententionHelperHandler extends BaseHandler
{
	private static final DecimalFormat mFloatFormatter = new DecimalFormat("#.##");

	private ReceiveMsgThread mReceiveMsgThread;
	private SendMsgThread mSendMsgThread;

	/** 紀錄送出後未收到伺服器 ack 回應的指令數目 */
	private AtomicInteger mCommandAckBalance = new AtomicInteger(0);
	/** 建立連線的 socket 是否被 timer 關閉 */
	private volatile boolean mInitIsClosedByTimer = false;
	private volatile boolean mIsConnecting = false;

	public PresententionHelperHandler(Context context)
	{
		super(context);
	}

	/**
	 * 連線至伺服器 <br>
	 * 流程：進行 handshaking，建立接收訊息管道並告知伺服器，建立傳送訊息管道<br>
	 * this method closes old connection (if any)
	 */
	public void connect(final String address, final int port)
	{
		if (mIsConnecting)
		{
			return;
		}

		mIsConnecting = true;
		mInitIsClosedByTimer = false;
		mCommandAckBalance.set(0);

		final UdpSocketWrapper initSocket = new UdpSocketWrapper(address, port);
		final Timer initTimeoutTimer = new Timer();

		// thread used to establish connection to server
		final Thread initConnectionThread = new Thread()
		{
			@Override
			public void run()
			{
				Logs.showTrace("Connecting to " + address + ":" + port);

				if (null != mReceiveMsgThread)
				{
					mReceiveMsgThread.closeSocket();
					mReceiveMsgThread = null;
				}

				if (null != mSendMsgThread)
				{
					mSendMsgThread.closeSocket();
					mSendMsgThread = null;
				}

				try
				{
					// say hello to server
					initSocket.connect();
					initSocket.sendMsg(Consts.MSG_ABLE_TO_CONNECT);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					cancelInit(ResponseCode.ERR_IO_EXCEPTION, e.toString());
					return;
				}

				// wait server for saying hi
				byte[] recvBuffer = new byte[128];
				DatagramPacket pack = new DatagramPacket(recvBuffer, recvBuffer.length);

				while (!mInitIsClosedByTimer && initSocket.isConnected() && !initSocket.isClosed())
				{
					try
					{
						initSocket.receive(pack);
					}
					catch (IOException e)
					{
						if (!mInitIsClosedByTimer)
						{
							cancelInit(ResponseCode.ERR_IO_EXCEPTION, e.toString());
						}

						return;
					}

					String initSocketReceivedMsg = new String(pack.getData());
					if (!initSocketReceivedMsg.startsWith(Consts.MSG_CONFIRM))
					{
						continue;
					}

					try
					{
						// establish socket for receiving message
						mReceiveMsgThread = new ReceiveMsgThread(address, port);
						mReceiveMsgThread.connect();

						// establish socket for sending message
						mSendMsgThread = new SendMsgThread(address, port);
						mSendMsgThread.connect();
					}
					catch (IOException e)
					{
						e.printStackTrace();
						cancelInit(ResponseCode.ERR_IO_EXCEPTION, e.toString());
						return;
					}

					// inform server which PORT is used to receive message
					try
					{
						initSocket.sendMsg(Consts.MSG_RECEIVE_PORT + mReceiveMsgThread.getLocalPort());
					}
					catch (IOException e)
					{
						e.printStackTrace();
						cancelInit(ResponseCode.ERR_IO_EXCEPTION, e.toString());
						return;
					}
					finally
					{
						// done handshaking, now ok to cancel timer
						// regardless message is sent successfully or not
						if (null != initTimeoutTimer)
						{
							initTimeoutTimer.cancel();
							initSocket.close();
						}
					}

					mReceiveMsgThread.start();
					mSendMsgThread.start();

					Logs.showTrace("connection established");
					sendMessageOnlyCallBackMessage(ResponseCode.ERR_SUCCESS,
							ResponseCode.METHOD_PRES_HELPER_CONNECT_TO_SERVER, "connection established");

					mIsConnecting = false;
				}
			}

			/** 取消建立連線的嘗試 */
			private void cancelInit(int errorCode, String msg)
			{
				Logs.showTrace("connection failed, cancel init");

				if (null != initTimeoutTimer)
				{
					initTimeoutTimer.cancel();
				}

				if (null != initSocket)
				{
					initSocket.close();
				}

				if (null != mSendMsgThread)
				{
					mSendMsgThread.closeSocket();
					mSendMsgThread = null;
				}

				if (null != mReceiveMsgThread)
				{
					mReceiveMsgThread.closeSocket();
					mReceiveMsgThread = null;
				}

				mIsConnecting = false;
				mInitIsClosedByTimer = false;

				sendMessageOnlyCallBackMessage(errorCode, ResponseCode.METHOD_PRES_HELPER_CONNECT_TO_SERVER, msg);
			}
		};

		initConnectionThread.start();

		// timer used to trigger init timeout
		initTimeoutTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				if (null != initConnectionThread && initConnectionThread.isAlive())
				{
					mInitIsClosedByTimer = true;

					if (null != initSocket)
					{
						initSocket.close();
					}

					if (null != mSendMsgThread)
					{
						mSendMsgThread.closeSocket();
						mSendMsgThread = null;
					}

					if (null != mReceiveMsgThread)
					{
						mReceiveMsgThread.closeSocket();
						mReceiveMsgThread = null;
					}

					Logs.showTrace("connection failed (timeout)");
					sendMessageOnlyCallBackMessage(ResponseCode.ERR_TIMEOUT,
							ResponseCode.METHOD_PRES_HELPER_CONNECT_TO_SERVER, "timeout while connecting to server");

					mIsConnecting = false;
				}
			}
		}, 3000);
	}

	/** 結束連線 */
	public void close()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				if (null != mReceiveMsgThread)
				{
					mReceiveMsgThread.closeSocket();
					mReceiveMsgThread = null;
				}

				if (null != mSendMsgThread)
				{
					mSendMsgThread.closeSocket();
					mSendMsgThread = null;
				}
			}
		}.start();
	}

	/* send commands methods */

	/** 顯示簡報清單 */
	public void showPresentationList()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_LIST_SHOW);
	}

	/** 關閉簡報清單 */
	public void closePresentationList()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_LIST_CLOSE);
	}

	/** 上移簡報清單游標 */
	public void moveUpPresentationListCursor()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_LIST_UP);
	}

	/** 下移簡報清單游標 */
	public void moveDownPresentationListCursor()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_LIST_DOWN);
	}

	/** 開啟或關閉在簡報清單選中的簡報檔 */
	public void togglePresentationListSeletcted()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_OPEN);
	}

	/** 進入全螢幕放映 */
	public void enterFullscreenMode()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_SHOW);
	}

	/** 離開全螢幕放映 */
	public void exitFullscreenMode()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_STOP);
	}

	/** 移到簡報上一頁 */
	public void gotoPreviousSlide()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_PAGE_UP);
	}

	/** 移到簡報下一頁 */
	public void gotoNextSlide()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_PAGE_DOWN);
	}

	/** 移動滑鼠游標 */
	public void moveCursor(float dx, float dy)
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_MOVE_CURSOR_PREFIX + mFloatFormatter.format(dx)
				+ Consts.CMD_PARAM_SEPERATOR + mFloatFormatter.format(dy) + Consts.CMD_PARAM_SEPERATOR);
	}

	/**
	 * 滾動滑鼠垂直滾輪
	 * 
	 * @param awayFromUser
	 *            為 true 時，代表滾輪向上滾(遠離使用者的方向)，false 代表靠近使用者
	 */
	public void rollVerticalWheel(float dy, boolean awayFromUser)
	{
		int direction = awayFromUser ? Consts.CMD_VWHEEL_ROTATE_AWAY_USER : Consts.CMD_VWHEEL_ROTATE_TO_USER;

		sendCommand(Consts.CMD_PREFIX + Consts.CMD_ROLL_VWHEEL_PREFIX + (direction * dy) + Consts.CMD_PARAM_SEPERATOR);
	}

	/** 按下滑鼠左鍵 */
	public void pressLeftMouseButton()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_LEFT_DOWN);
	}

	/** 釋放滑鼠左鍵 */
	public void releaseLeftMouseButton()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_LEFT_UP);
	}

	/** 按下滑鼠右鍵 */
	public void pressRightMouseButton()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_RIGHT_DOWN);
	}

	/** 釋放滑鼠右鍵 */
	public void releaseRightMouseButton()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_RIGHT_UP);
	}

	public void showLazer()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_LAZER_SHOW);
	}
	
	public void hideLazer()
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_LAZER_OFF);
	}
		
	public void moveLazer(float dx, float dy)
	{
		sendCommand(Consts.CMD_PREFIX + Consts.CMD_MOVE_LAZER_PREFIX
				+ mFloatFormatter.format(dx) + Consts.CMD_PARAM_SEPERATOR + mFloatFormatter.format(dy)
				+ Consts.CMD_PARAM_SEPERATOR);
	}
	
	public InetAddress getInetAddress()
	{
		if (null != mSendMsgThread)
		{
			return mSendMsgThread.getInetAddress();
		}

		return null;
	}

	/** 傳送指令至伺服器 */
	private void sendCommand(final String command)
	{
		if (null == mSendMsgThread)
		{
			Logs.showTrace("null == mSendMsgThread");
			sendMessageOnlyCallBackMessage(ResponseCode.ERR_NOT_INIT, ResponseCode.METHOD_PRES_HELPER_SEND_COMMAND,
					"connect to server first");

			return;
		}

		Logs.showTrace("Enqueue Command:" + command);
		mSendMsgThread.enqueue(command);
	}

	/** 送出只有 "message" 欄位的 callBackMessage */
	private void sendMessageOnlyCallBackMessage(int errorCode, int method, String message)
	{
		HashMap<String, String> respMap = new HashMap<String, String>();
		respMap.put("message", message);
		callBackMessage(errorCode, CtrlType.MSG_RESPONSE_PRESENTATION_HELPER_HANDLER, method, respMap);
	}

	/** 送出 msgMap 到 callBackMessage */
	private void sendMapCallBackMessage(int errorCode, int method, HashMap<String, String> msgMap)
	{
		if (null == msgMap)
		{
			return;
		}
		callBackMessage(errorCode, CtrlType.MSG_RESPONSE_PRESENTATION_HELPER_HANDLER, method, msgMap);
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				if (null != mReceiveMsgThread)
				{
					mReceiveMsgThread.closeSocket();
				}

				if (null != mSendMsgThread)
				{
					mSendMsgThread.closeSocket();
				}
			}
		}).start();
	}

	/** 負責接收伺服器訊息的 Thread */
	private class ReceiveMsgThread extends UdpSocketThread
	{
		public ReceiveMsgThread(String address, int port)
		{
			super(address, port);
		}

		@Override
		public void run()
		{
			byte[] buffer = new byte[128];
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
					sendMessageOnlyCallBackMessage(ResponseCode.ERR_IO_EXCEPTION,
							ResponseCode.METHOD_PRES_HELPER_RECV_MSG, e.toString());

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

						HashMap<String, String> message = new HashMap<String, String>();
						message.put("message", "server sent slide index");
						message.put("presSlideIndex", tmp[0]);
						message.put("presSlideCount", tmp[1]);

						sendMapCallBackMessage(ResponseCode.ERR_SUCCESS,
								ResponseCode.METHOD_PRES_HELPER_RECV_MSG_SLIDE_INDEX, message);
					}
					else if (msg.startsWith(Consts.FROM_SERVER_MSG_SEND_COMMAND_ACK))
					{
						mCommandAckBalance.set(0);

						HashMap<String, String> message = new HashMap<String, String>();
						message.put("message", "got cmd ack from server");
						message.put("debugAckBalance", Integer.toString(mCommandAckBalance.get()));

						sendMapCallBackMessage(ResponseCode.ERR_SUCCESS,
								ResponseCode.METHOD_PRES_HELPER_RECV_MSG_CMD_ACK, message);
					}
				}
			}
		}
	}

	/** 負責傳送訊息的 Thread，此 Thread 每隔一段時間還會檢查是否有收到 server 的 ack 訊息 */
	private class SendMsgThread extends UdpSocketThread
	{
		private final ConcurrentLinkedQueue<String> mCmdQueue = new ConcurrentLinkedQueue<String>();

		private long mLastCommandSentTime = -1;
		private static final long CMD_ACK_MAX_WAIT = 3000;

		public SendMsgThread(String address, int port)
		{
			super(address, port);
		}

		public void enqueue(String command)
		{
			mCmdQueue.add(command);
		}

		@Override
		public void run()
		{
			runCheckBalanceThread();

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

							mLastCommandSentTime = System.currentTimeMillis();
							mCommandAckBalance.addAndGet(1);
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
						sendMessageOnlyCallBackMessage(ResponseCode.ERR_IO_EXCEPTION,
								ResponseCode.METHOD_PRES_HELPER_SEND_COMMAND, e.toString());

						// continue try to send
					}
				}
			}
		}

		private void runCheckBalanceThread()
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					while (!explicitClosed && null != mSocket && !mSocket.isClosed())
					{
						try
						{
							Thread.sleep(CMD_ACK_MAX_WAIT / 2);
						}
						catch (InterruptedException e)
						{
						}

						if (explicitClosed || null == mSocket || mSocket.isClosed())
						{
							return;
						}

						checkCommandAckBalance();
					}
				}
			}).start();
		}

		private void checkCommandAckBalance()
		{
			long now = System.currentTimeMillis();
			int ackBalance = mCommandAckBalance.get();

			if (now - mLastCommandSentTime > CMD_ACK_MAX_WAIT && ackBalance > 0)
			{
				mCommandAckBalance.set(0);
				sendMessageOnlyCallBackMessage(ResponseCode.ERR_TIMEOUT, ResponseCode.METHOD_PRES_HELPER_SEND_COMMAND,
						"network not stable, some command did not sent");
			}
		}
	}
}
