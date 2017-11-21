package sdk.ideas.ctrl.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Parcelable;
import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CommonClass.BluetoothDeviceLinkableDevice;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ReturnIntentAction;

public class BluetoothHandler extends BaseHandler implements ListenReceiverAction
{
	protected BluetoothAdapter mBluetoothAdapter = null;
	private HashMap<String, String> message = null;

	protected static ArrayList<BluetoothDeviceLinkableDevice> mBluetoothDeviceLinkableDevice = new ArrayList<BluetoothDeviceLinkableDevice>();

	private static BluetoothReceiver mBluetoothReceiver = null;
	private static IntentFilter mBluetoothIntentFilter = null;
	private boolean isRegister = false;
	private int discoverableTime = 300;

	private ConnectThread mConnectThread;
	private ConnectedThread mConnectedThread;
	private AcceptThread mAcceptThread;

	// Standard SerialPortService ID
	public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	public static final UUID uuid = UUID.fromString(SPP_UUID);
	public static final String socketType = "Standard Serial Port Service";

	// Constants that indicate the current connection state
	// we're doing nothing
	private int mState;

	public static final int STATE_NONE = 0;

	// now listening for incoming connections
	public static final int STATE_LISTEN = 1;

	// now initiating an outgoing connection
	public static final int STATE_CONNECTING = 2;

	// now connected to a remote device
	public static final int STATE_CONNECTED = 3;

	private static final String NAME = "BluetoothHandler";

	private static String needToPairDevice = "";
	private static boolean isNeedToPair = false;

	private int bluetooth_mode = MODE_ACTIVE;
	public static final int MODE_DUAL = -1;
	public static final int MODE_ACTIVE = 0;
	public static final int MODE_INACTIVE = 1;

	public static final int autoPairPin = 1234;
	private boolean isRegisterAutoPair = false;
	private IntentFilter autoBondDeviceFilter = null;
	private int pairChance = MAX_CHANCE;
	private final static int MAX_CHANCE = 10;

	private final static boolean stableVer = false;

	private final static byte END_BYTE_1 = (byte) 0x0D;
	private final static byte END_BYTE_2 = (byte) 0x0A;
	private final static int END_BYTE_NUM = 2;
	public BluetoothHandler(Context context)
	{
		super(context);
		message = new HashMap<String, String>();
		init();
	}

	public BluetoothHandler(Context context, int mode)
	{
		super(context);
		message = new HashMap<String, String>();

		switch (mode)
		{
		case MODE_ACTIVE:
			bluetooth_mode = MODE_ACTIVE;
			break;
		case MODE_INACTIVE:
			bluetooth_mode = MODE_INACTIVE;
			break;
		case MODE_DUAL:
			bluetooth_mode = MODE_DUAL;
		default:
			bluetooth_mode = MODE_ACTIVE;
			break;
		}

		init();
	}

	private void init()
	{
		mState = STATE_NONE;

		if (checkDeviceBluetooth() == false)
		{
			message.put("message", "device not support bluetooth module");
			super.callBackMessage(ResponseCode.ERR_DEVICE_NOT_SUPPORT_BLUETOOTH,
					CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SETUP_BLUETOOTH, message);
			return;
		}

		if (null == mBluetoothReceiver)
		{
			// Logs.showTrace("in new mBluetoothReceiver");
			mBluetoothReceiver = new BluetoothReceiver();
			mBluetoothReceiver.setOnReceiverListener(new ReturnIntentAction()
			{

				@Override
				protected void finalize() throws Throwable
				{
					Logs.showTrace("mBluetoothReceiver finalize");
				}

				@Override
				public void returnIntentAction(HashMap<String, String> action)
				{
					message.clear();
					// Logs.showTrace("in returnIntentAction :
					// mBluetoothReceiver");
					/*
					 * if(null == action) Logs.showTrace("action is null"); else
					 * { Logs.showTrace(action.get("action")); }
					 */

					if (action.get("action").equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
					{
						message.put("message", "BOND_STATE_CHANGED");
						message.put("state", action.get("state"));
						message.put("deviceName", action.get("deviceName"));
						message.put("deviceAddress", action.get("deviceAddress"));
						Logs.showTrace("[BOND_STATE]" + message);

						if (isNeedToPair == true && action.get("state").equals("BOND_BONDED"))
						{
							if (needToPairDevice.equals(action.get("deviceAddress"))
									|| needToPairDevice.equals(action.get("deviceName")))
							{
								needToPairDevice = "";
								isNeedToPair = false;
								pairChance = MAX_CHANCE;
								connectDeviceByMacAddress(action.get("deviceAddress"));
							}

							callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
									ResponseCode.METHOD_BOND_STATE_CHANGE_BLUETOOTH, message);
						}
						else if (isNeedToPair == true && action.get("state").equals("BOND_NONE"))
						{

							if (needToPairDevice.equals(action.get("deviceAddress"))
									|| needToPairDevice.equals(action.get("deviceName")))
							{
								pairChance--;
								if (pairChance < 0)
								{
									callBackMessage(ResponseCode.ERR_BLUETOOTH_DEVICE_BOND_FAIL,
											CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
											ResponseCode.METHOD_BOND_STATE_CHANGE_BLUETOOTH, message);
								}
								else
								{
									pairNewDevice(action.get("deviceAddress"));
								}
								// connectDeviceByMacAddress(action.get("deviceAddress"));

							}

						}
						// callBackMessage(ResponseCode.ERR_SUCCESS,
						// CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
						// ResponseCode.METHOD_BOND_STATE_CHANGE_BLUETOOTH,
						// message);

					}

					else if (action.get("action").equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED))
					{
						message.put("message", "SCAN_MODE_CHANGED");
						message.put("mode", action.get("mode"));

						callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.METHOD_SCAN_MODE_CHANGE_BLUETOOTH, message);

					}
					else if (action.get("action").equals(BluetoothDevice.ACTION_FOUND))
					{
						message.put("message", "New Device Found");
						message.put("deviceName", action.get("deviceName"));
						message.put("deviceAddress", action.get("deviceAddress"));

						callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.METHOD_BLUETOOTH_DISCOVERING_NEW_DEVICE, message);

						Logs.showTrace(String.valueOf(isNeedToPair) + " " + needToPairDevice);
						if (isNeedToPair == true && (needToPairDevice.equals(action.get("deviceName"))
								|| needToPairDevice.equals(action.get("deviceAddress"))))
						{
							stopDiscovery();
							pairNewDevice(action.get("deviceAddress"));
						}
					}
					else if (action.get("action").equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
					{
						message.put("message", "Discover finished");

						Logs.showTrace("finshed:" + BluetoothHandler.this.printState(BluetoothHandler.this.getState()));

						callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.METHOD_BLUETOOTH_DISCOVER_FINISHED, message);

						// 當裝置找不到時，又探索完成
						BluetoothDeviceLinkableDevice tmp = ArrayListUtility
								.findEqualForBluetoothDeviceLinkableDeviceClass(mBluetoothDeviceLinkableDevice,
										needToPairDevice);
						if (null == tmp)
						{
							message.clear();
							message.put("message", "device :" + needToPairDevice + "can not found");
							callBackMessage(ResponseCode.ERR_BLUETOOTH_DEVICE_NOT_FOUND,
									CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
									ResponseCode.METHOD_OPEN_BLUETOOTH_CONNECTED_LINK, message);
						}

					}
					else if (action.get("action").equals(String.valueOf(BluetoothAdapter.STATE_OFF)))
					{
						message.put("message", "bluetooth is OFF");
						callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.BLUETOOTH_IS_OFF, message);

					}
					else if (action.get("action").equals(String.valueOf(BluetoothAdapter.STATE_ON)))
					{
						message.put("message", "bluetooth is ON");
						callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.BLUETOOTH_IS_ON, message);

					}

				}
			});
			// Logs.showTrace("mBluetoothReceiver set finish");
		}

		if (null == mBluetoothIntentFilter)
		{
			mBluetoothIntentFilter = new IntentFilter();
			mBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			mBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			mBluetoothIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
			mBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
			mBluetoothIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);

		}

		// unstable code
		if (stableVersion() == true)
		{
			autoBondDeviceInit();
		}

	}

	private void closeBluetoothConnecting() throws IOException
	{

		stop();

	}

	private boolean stableVersion()
	{
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			if (stableVer == true)
				return true;
			else
				return false;

		}
		return false;

	}

	public void closeBluetoothLink()
	{
		HashMap<String, String> message = new HashMap<String, String>();
		try
		{
			Logs.showTrace("closing bluetooth link");
			closeBluetoothConnecting();
			Logs.showTrace("close bluetooth link success");

			message.put("message", "close bluetooth link success");
			callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
					ResponseCode.METHOD_CLOSE_BLUETOOTH_CONNECTED_LINK, message);
		}
		catch (IOException e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
					ResponseCode.METHOD_CLOSE_BLUETOOTH_CONNECTED_LINK, message);
		}
		finally
		{
			message.clear();
		}
	}

	private void closeBluetooth()
	{
		Logs.showTrace("closing bluetooth success");
		mBluetoothAdapter = null;
		mBluetoothDeviceLinkableDevice.clear();
		Logs.showTrace("close bluetooth success");
	}

	public void connectDeviceByName(String name)
	{
		connectDeviceByMacAddress(name);
	}

	public void connectDeviceByMacAddress(String address)
	{
		pairChance = MAX_CHANCE;
		BluetoothDevice mBluetoothDevice = isPairedDevices(address);

		if (null != mBluetoothDevice)
		{
			showDeviceService(mBluetoothDevice);
			this.connect(mBluetoothDevice, socketType);
		}
		// non paired device
		else
		{
			// start to discover new device where it exist
			needToPairDevice = address;
			isNeedToPair = true;
			Logs.showTrace("connectDeviceByMacAddress : " + String.valueOf(isNeedToPair) + " " + needToPairDevice);
			this.startDiscovery();

		}
	}

	private void pairNewDevice(String address)
	{
		Logs.showTrace("start to pair new device");
		if (null != mBluetoothAdapter)
		{
			BluetoothDevice mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);

			if (null == mBluetoothDevice)
			{
				Logs.showTrace("device is null");
				return;
			}
			// showDeviceService(mBluetoothDevice);

			Thread createBond = new Thread(new createBondRunnable(mBluetoothDevice));
			createBond.start();

			/*
			 * try {
			 * 
			 * //Boolean returnValue = false;
			 * 
			 * //Method createBondMethod =
			 * BluetoothDevice.class.getMethod("createBond"); //Logs.showTrace(
			 * "start to pair to " + mBluetoothDevice.getName() + " address:" +
			 * mBluetoothDevice.getAddress()); //returnValue = (Boolean)
			 * createBondMethod.invoke(mBluetoothDevice);
			 * 
			 * //returnValue =
			 * BluetoothPairUtils.createBond(mBluetoothDevice.getClass(),
			 * mBluetoothDevice); if (android.os.Build.VERSION.SDK_INT >=
			 * Build.VERSION_CODES.KITKAT) { returnValue =
			 * newVersionBondDevice(mBluetoothDevice); } else {
			 * 
			 * }
			 * 
			 * if (returnValue == false) { Logs.showTrace(
			 * "[pair New Device] fail to pair ~"); } else { Logs.showTrace(
			 * "[pair New Device] now cancel Pairing User Input"); if
			 * (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			 * { BluetoothPairUtils.cancelPairingUserInput(mBluetoothDevice.
			 * getClass(),mBluetoothDevice); } } } catch (Exception e) {
			 * Logs.showTrace("[pair New Device] fail to pair: " +
			 * e.toString()); }
			 */
		}
		else
		{
			// error message
			Logs.showTrace("[pair New Device] error BluetoothAdapter is null");
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private boolean newVersionBondDevice(BluetoothDevice mBluetoothDevice)
	{
		return mBluetoothDevice.createBond();
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void autoBondDeviceInit()
	{
		if (null == autoBondDeviceFilter)
		{
			autoBondDeviceFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
		}
	}

	private final BroadcastReceiver mAutoPairingRequestReceiver = new BroadcastReceiver()
	{
		@TargetApi(Build.VERSION_CODES.KITKAT)
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST))
			{
				try
				{
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					int pin = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", autoPairPin);
					// the pin in case you need to accept for an specific pin
					Logs.showTrace("[AUTO PAIRING] Start Auto Pairing. PIN = "
							+ intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", autoPairPin));
					byte[] pinBytes;
					pinBytes = ("" + pin).getBytes("UTF-8");
					// Logs.showTrace("[AUTO PAIRING] device setPin");
					device.setPin(pinBytes);
					// Logs.showTrace("[AUTO PAIRING] END device setPin");
					// setPairing confirmation if neeeded
					Logs.showTrace("[AUTO PAIRING] device.setPairingConfirmation");
					device.setPairingConfirmation(true);
					Logs.showTrace("[AUTO PAIRING] END device.setPairingConfirmation");
				}
				catch (Exception e)
				{
					Logs.showTrace("[AUTO PAIRING] Error occurs when trying to auto pair " + e.toString());

				}
			}

		}
	};

	public boolean removePairedDevice(String nameOrAddress)
	{
		BluetoothDevice mDevice = isPairedDevices(nameOrAddress);
		if (null == mDevice)
		{
			return true;
		}
		else
		{
			try
			{
				BluetoothPairUtils.removeBond(mDevice.getClass(), mDevice);
				return true;
			}
			catch (Exception e)
			{
				Logs.showTrace("[Remove Paired] ERROR " + e.toString());
				return false;
			}
		}
	}

	private void showDeviceService(BluetoothDevice device)
	{
		if (device.fetchUuidsWithSdp() == true)
		{
			if (null == device.getUuids())
			{
				Logs.showTrace("uuid is null");
			}
			else
			{
				Parcelable a[] = device.getUuids();
				for (int i = 0; i < a.length; i++)
				{
					Logs.showTrace(device.getName() + ": service : " + a[i]);
				}
			}
		}
		else
		{
			Logs.showTrace("device.fetchUuidsWithSdp() = false");
		}
	}

	private BluetoothDevice isPairedDevices(String nameOrAddress)
	{
		if (null != mBluetoothAdapter)
		{
			Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
			if (mPairedDevices.size() > 0)
			{
				for (BluetoothDevice mDevice : mPairedDevices)
				{
					if (mDevice.getAddress().equals(nameOrAddress) == true || mDevice.getName().equals(nameOrAddress))
						return mDevice;
				}
			}
		}
		return null;

	}

	public void requestBluetoothDiscoverable()
	{
		if (null != mBluetoothAdapter)
		{
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, discoverableTime);
			((Activity) mContext).startActivityForResult(intent, CtrlType.REQUEST_CODE_DISCOVERABLE_BLUETOOTH);
		}
	}

	public void setBluetooth(boolean turnOn)
	{
		if (null != mBluetoothAdapter)
		{
			if (turnOn == true)
			{
				if (mBluetoothAdapter.isEnabled() == false)
				{
					Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					((Activity) mContext).startActivityForResult(turnOnIntent, CtrlType.REQUEST_CODE_ENABLE_BLUETOOTH);
				}
				else
				{
					// Logs.showTrace("opened bluetooth");
					message.put("message", "success");
					callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
							ResponseCode.BLUETOOTH_IS_ON, message);
				}
			}
			else if (turnOn == false)
			{
				if (mBluetoothAdapter.isEnabled() == true)
				{
					mBluetoothAdapter.disable();
					try
					{
						closeBluetoothConnecting();
						closeBluetooth();
					}
					catch (IOException e)
					{
						message.put("message", e.toString());
						super.callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.BLUETOOTH_IS_OFF, message);
					}
				}
				else
				{
					message.put("message", "success");
					super.callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
							ResponseCode.BLUETOOTH_IS_OFF, message);
				}
			}
		}
		message.clear();
	}

	public void stopDiscovery()
	{
		if (null != mBluetoothAdapter)
		{
			if (isStillDiscovery() == true)
			{
				mBluetoothAdapter.cancelDiscovery();
			}
		}
	}

	private boolean isStillDiscovery()
	{
		if (null != mBluetoothAdapter)
		{
			return mBluetoothAdapter.isDiscovering();
		}
		else
		{
			return false;
		}
	}

	public void startDiscovery()
	{

		if (null != mBluetoothAdapter && mBluetoothAdapter.isEnabled())
		{

			mBluetoothDeviceLinkableDevice.clear();

			mBluetoothAdapter.startDiscovery();
			// Logs.showTrace("mBluetoothAdapter startDiscoverying");
		}

	}

	private boolean checkDeviceBluetooth()
	{
		if (null == mBluetoothAdapter)
		{
			if (null != BluetoothAdapter.getDefaultAdapter())
			{
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == CtrlType.REQUEST_CODE_ENABLE_BLUETOOTH)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				Logs.showTrace("opened bluetooth");

				// message.put("message", "success");
				// super.setResponseMessage(ResponseCode.ERR_SUCCESS, message);
				// super.returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
				// ResponseCode.METHOD_SETUP_BLUETOOTH);

			}
			else
			{
				// cancel by user
				Logs.showTrace("can not open bluetooth cause user");
				message.put("message", "can not open bluetooth cause user");
				super.callBackMessage(ResponseCode.ERR_BLUETOOTH_CANCELLED_BY_USER,
						CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SETUP_BLUETOOTH, message);
			}

		}
		else if (requestCode == CtrlType.REQUEST_CODE_DISCOVERABLE_BLUETOOTH)
		{
			Logs.showTrace("result code : " + String.valueOf(resultCode));
			if (resultCode == Activity.RESULT_CANCELED || resultCode == Activity.RESULT_FIRST_USER)
			{

				Logs.showTrace("can not let device's bluetooth discoverable cause by user");
				message.put("message", "can not let device's bluetooth discoverable cause by user");
				super.callBackMessage(ResponseCode.ERR_BLUETOOTH_DISCOVERABLE_CANCELLED_BY_USER,
						CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SETUP_BLUETOOTH, message);
			}
			else
			{
				// 開啟讓其他連線
				Logs.showTrace("our device bluetooth is discoverable ");
				message.put("message", "discoverable success!");
				super.callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
						ResponseCode.METHOD_DISCOVERABLE_BLUETOOTH, message);

			}

		}

	}

	@Override
	public void startListenAction()
	{
		// Logs.showTrace("startListenAction isRegister:"+
		// String.valueOf(isRegister));
		if (isRegister == false)
		{
			// Logs.showTrace("registerReceiver");
			mContext.registerReceiver(mBluetoothReceiver, mBluetoothIntentFilter);

			isRegister = true;
		}
		if (stableVersion() == true)
		{
			if (isRegisterAutoPair == false)
			{
				mContext.registerReceiver(mAutoPairingRequestReceiver, autoBondDeviceFilter);
				isRegisterAutoPair = true;
			}
		}
	}

	@Override
	public void stopListenAction()
	{
		// Logs.showTrace("stopListenAction isRegister:"+
		// String.valueOf(isRegister));
		if (isRegister == true)
		{
			mContext.unregisterReceiver(mBluetoothReceiver);
			isRegister = false;
		}
		if (stableVersion() == true)
		{
			if (isRegisterAutoPair == true)
			{
				mContext.unregisterReceiver(mAutoPairingRequestReceiver);
				isRegisterAutoPair = false;
				Logs.showTrace("[AUTO PAIRING] unregister AUTO PAIR Receiver SUCCESS");
			}
		}
	}

	public void sendMessage(String message)
	{
		// Check that we're actually connected before trying anything
		if (this.getState() != BluetoothHandler.STATE_CONNECTED)
		{
			HashMap<String, String> data = new HashMap<String, String>();
			Logs.showTrace("bluetooth is not connected");
			data.put("message", "bluetooth is not connected");
			callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
					ResponseCode.METHOD_SEND_MESSAGE_BLUETOOTH, data);
			return;
		}

		// Check that there's actually something to send

		if (message.length() > 0)
		{
			byte[] send = (message).getBytes();
			this.write(send);
		}
		Logs.showTrace("send success: " + message);
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 *
	 * @param device
	 *            The BluetoothDevice to connect
	 */
	private synchronized void connect(BluetoothDevice device, String socketType)
	{
		Logs.showTrace("connect to: " + device);

		// Cancel any thread attempting to make a connection
		if (mState == STATE_CONNECTING)
		{
			if (null != mConnectThread)
			{
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (null != mConnectedThread)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device, socketType);
		mConnectThread.start();
		setState(STATE_CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 *
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType)
	{
		Logs.showTrace("connected, Socket Type:" + socketType);

		// Cancel the thread that completed the connection
		if (null != mConnectThread)
		{
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (null != mConnectedThread)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Cancel the accept thread because we only want to connect to one
		// device
		if (null != mAcceptThread)
		{
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket, socketType);
		mConnectedThread.start();

		setState(STATE_CONNECTED);
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	private synchronized void start()
	{

		// Cancel any thread attempting to make a connection
		if (null != mConnectThread)
		{
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (null != mConnectedThread)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (this.bluetooth_mode != BluetoothHandler.MODE_ACTIVE)
		{
			setState(STATE_LISTEN);

			// Start the thread to listen on a BluetoothServerSocket
			if (null == mAcceptThread)
			{
				mAcceptThread = new AcceptThread(socketType);
				mAcceptThread.start();
			}
		}
		else
		{
			setState(STATE_NONE);
		}
	}

	/**
	 * Stop all threads
	 */
	private synchronized void stop()
	{
		if (null != mConnectThread)
		{
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (null != mConnectedThread)
		{
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		if (null != mAcceptThread)
		{
			mAcceptThread.cancel();
			mAcceptThread = null;
		}
		setState(STATE_NONE);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner
	 *
	 * @param out
	 *            The bytes to write
	 */
	private void write(byte[] out)
	{
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this)
		{
			if (mState != STATE_CONNECTED)
				return;
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	private void connectionFailed()
	{
		// Send a failure message back to the Activity
		Logs.showTrace("IN connectionFailed method: Unable to connect device");

		// Start the service over to restart listening mode
		BluetoothHandler.this.start();
	}

	private void connectionLost()
	{
		// Send a failure message back to the Activity
		Logs.showTrace("IN connectionLost method: Device connection was lost");

		// Start the service over to restart listening mode
		BluetoothHandler.this.start();
	}

	/**
	 * Set the current state of the chat connection
	 *
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state)
	{

		Logs.showTrace("bluetooth mode: " + printState(mState) + " -> " + printState(state));
		mState = state;

		// Give the new state to the Handler so the UI Activity can update

	}

	private String printState(int state)
	{
		String string_state = "";
		switch (state)
		{
		case STATE_NONE:
			string_state = "NONE";
			break;
		case STATE_LISTEN:
			string_state = "LISTEN";
			break;
		case STATE_CONNECTING:
			string_state = "CONNECTING";
			break;
		case STATE_CONNECTED:
			string_state = "CONNECTED";
			break;
		}
		return string_state;
	}

	/**
	 * Return the current connection state.
	 */
	private synchronized int getState()
	{
		return mState;
	}

	/**
	 * This thread runs while listening for incoming connections. It behaves
	 * like a server-side client. It runs until a connection is accepted (or
	 * until cancelled).
	 */
	private class AcceptThread extends Thread
	{
		// The local server socket
		private final BluetoothServerSocket mmServerSocket;
		private String mSocketType;

		public AcceptThread(String socketType)
		{
			BluetoothServerSocket tmp = null;
			mSocketType = socketType;

			// Create a new listening server socket
			try
			{
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, uuid);
			}
			catch (IOException e)
			{
				Logs.showTrace("[AcceptThread] Socket Type: " + mSocketType + "listen() failed: " + e.toString());
			}
			catch (Exception e)
			{
				Logs.showTrace("[AcceptThread] Socket Type: " + mSocketType + "listen() failed: " + e.toString());
			}
			mmServerSocket = tmp;
		}

		public void run()
		{

			Logs.showTrace("[AcceptThread]Socket Type: " + mSocketType + "BEGIN mAcceptThread" + this);
			setName("AcceptThread" + mSocketType);

			BluetoothSocket socket = null;

			// Listen to the server socket if we're not connected
			while (mState != STATE_CONNECTED)
			{
				try
				{
					// This is a blocking call and will only return on a
					// successful connection or an exception
					socket = mmServerSocket.accept();
				}
				catch (IOException e)
				{
					Logs.showTrace("[AcceptThread]Socket Type: " + mSocketType + "accept() failed: " + e.toString());
					break;
				}

				// If a connection was accepted
				if (socket != null)
				{
					synchronized (BluetoothHandler.this)
					{
						switch (mState)
						{
						case STATE_LISTEN:
						case STATE_CONNECTING:
							// Situation normal. Start the connected thread.
							connected(socket, socket.getRemoteDevice(), mSocketType);
							break;
						case STATE_NONE:
						case STATE_CONNECTED:
							// Either not ready or already connected. Terminate
							// new socket.
							try
							{
								socket.close();
							}
							catch (IOException e)
							{
								Logs.showTrace("[AcceptThread]Could not close unwanted socket: " + e.toString());
							}
							break;
						}
					}
				}
			}
			Logs.showTrace("[AcceptThread]END mAcceptThread, socket Type: " + mSocketType);

		}

		public void cancel()
		{
			Logs.showTrace("[AcceptThread]Socket Type" + mSocketType + "cancel " + this);
			try
			{
				mmServerSocket.close();
			}
			catch (IOException e)
			{
				Logs.showTrace("[AcceptThread]Socket Type" + mSocketType + "close() of server failed: " + e.toString());
			}
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */

	private class ConnectThread extends Thread
	{
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;
		private String mSocketType;

		public ConnectThread(BluetoothDevice device, String socketType)
		{
			HashMap<String, String> message = new HashMap<String, String>();
			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			mSocketType = socketType;
			try
			{
				tmp = device.createRfcommSocketToServiceRecord(uuid);
			}
			catch (IOException e)
			{
				Logs.showTrace("[ConnectThread]Socket Type: " + mSocketType + "create() failed: " + e.toString());
				message.put("message", e.toString());
				callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
						ResponseCode.METHOD_OPEN_BLUETOOTH_CONNECTED_LINK, message);
			}
			mmSocket = tmp;
		}

		public void run()
		{
			HashMap<String, String> message = new HashMap<String, String>();
			Logs.showTrace("[ConnectThread]BEGIN mConnectThread SocketType:" + mSocketType);
			setName("ConnectThread" + mSocketType);

			// Always cancel discovery because it will slow down a connection
			// stopDiscovery();

			// Make a connection to the BluetoothSocket
			try
			{
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();

			}
			catch (IOException e)
			{
				message.put("message", e.toString());
				Logs.showTrace("[ConnectThread]Unable to connect socket :" + e.toString());

				callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
						ResponseCode.METHOD_OPEN_BLUETOOTH_CONNECTED_LINK, message);

				// Close the socket
				try
				{
					mmSocket.close();
				}
				catch (IOException e2)
				{
					message.clear();
					message.put("message", e2.toString());
					Logs.showTrace("[ConnectThread]unable to close() " + mSocketType
							+ " socket during connection failure: " + e2.toString());

					callBackMessage(ResponseCode.ERR_IO_EXCEPTION, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
							ResponseCode.METHOD_OPEN_BLUETOOTH_CONNECTED_LINK, message);
				}
				finally
				{
					// callBackMessage();

				}
				connectionFailed();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothHandler.this)
			{
				mConnectThread = null;
			}

			// Start the connected thread

			connected(mmSocket, mmDevice, mSocketType);
		}

		public void cancel()
		{
			try
			{
				mmSocket.close();
			}
			catch (IOException e)
			{
				Logs.showTrace("[ConnectThread]close() of connect " + mSocketType + " socket failed" + e.toString());
				// callBackMessage();
			}
		}
	}

	/**
	 * 透過connectThread或acceptThread取得連線後，即啟動此thread做socket傳輸處理
	 * 
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread
	{
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket, String socketType)
		{
			HashMap<String, String> message = new HashMap<String, String>();
			Logs.showTrace("[ConnectedThread] create ConnectedThread: " + socketType);
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try
			{
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();

				message.put("message", "success connected device");
				callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
						ResponseCode.METHOD_OPEN_BLUETOOTH_CONNECTED_LINK, message);
			}
			catch (IOException e)
			{
				Logs.showTrace("[ConnectedThread] sockets not created: " + e.toString());
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run()
		{
			Logs.showTrace("[ConnectedThread] BEGIN mConnectedThread");

			HashMap<String, String> message = new HashMap<String, String>();

			ArrayList<DataStreamStruct> inputDataStream = new ArrayList<DataStreamStruct>();
			int findEndByteCount = END_BYTE_NUM;
			// Keep listening to the InputStream while connected
			while (true)
			{

				try
				{

					int bytesLength = 0;
					byte[] buffer = new byte[1024];
					boolean bufferFoundEndByte = false;
					// Read from the InputStream
					bytesLength = mmInStream.read(buffer);

					/* debug using
					Logs.showTrace("[ConnectedThread]message bytes: " + String.valueOf(bytesLength));
					Logs.showTrace("[ConnectedThread]message buffer: " + new String(buffer));
					
					for (int i = 0; i < bytesLength; i++)
					{
						Logs.showError("buffer.data:: " + String.format("Byte: %x", buffer[i]));
					}
					 */
					for (int i = 0; i < bytesLength; i++)
					{
						if (buffer[i] == END_BYTE_1 || (buffer[i] == END_BYTE_2))
						{
							findEndByteCount -= 1;
							// bufferFoundEndByte = true;
						}
					}

					inputDataStream.add(new DataStreamStruct(bytesLength, buffer, bufferFoundEndByte));

					if (findEndByteCount <= 0)
					{
						// if (bufferFoundEndByte == true)
						{
							findEndByteCount = END_BYTE_NUM;
							int totalDataLength = 0;
							for (int i = 0; i < inputDataStream.size(); i++)
							{
								totalDataLength += inputDataStream.get(i).bytesLength;
							}
							byte[] totalBuffer = new byte[totalDataLength];
							int index = 0;
							for (int i = 0; i < inputDataStream.size(); i++)
							{
								for (int j = 0; j < inputDataStream.get(i).bytesLength; j++)
								{
									totalBuffer[index++] = (inputDataStream.get(i).data)[j];
								}
							}
							byte[] totalCheckBuffer = new byte[totalDataLength - END_BYTE_NUM];

							for (int i = 0; i < totalCheckBuffer.length; i++)
							{
								totalCheckBuffer[i] = totalBuffer[i];
							}

							message.put("message", new String(totalCheckBuffer));
							callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
									ResponseCode.METHOD_GET_MESSAGE_BLUETOOTH, message);
							message.clear();

							inputDataStream.clear();
						}
					}

				}
				catch (IOException e)
				{
					Logs.showTrace("[ConnectedThread]disconnected: " + e.toString());
					connectionLost();

					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 *
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(byte[] buffer)
		{
			HashMap<String, String> message = new HashMap<String, String>();
			try
			{
				// add end Byte
				byte[] outputbufferWithEnd = new byte[buffer.length + 1];

				for (int i = 0; i < buffer.length; i++)
				{
					outputbufferWithEnd[i] = buffer[i];
				}
				outputbufferWithEnd[buffer.length] = END_BYTE_1;

				/* debug using
				for (int i = 0; i < buffer.length; i++)
				{
					Logs.showError("buffer.data:: " + String.format("Byte: %x", buffer[i]));
				}*/
				mmOutStream.write(outputbufferWithEnd);

				// Share the sent message back to the UI Activity
				message.put("message", "bluetooth socket send message successfully!");
				callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
						ResponseCode.METHOD_SEND_MESSAGE_BLUETOOTH, message);
			}
			catch (IOException e)
			{
				Logs.showTrace("[ConnectedThread] Exception during write" + e.toString());
			}
		}

		public void cancel()
		{
			try
			{
				mmSocket.close();
			}
			catch (IOException e)
			{
				Logs.showTrace("[ConnectedThread] close() of connect socket failed" + e.toString());
			}
		}
	}

	class createBondRunnable implements Runnable
	{
		private BluetoothDevice mBluetoothDevice = null;

		public createBondRunnable(BluetoothDevice mBluetoothDevice)
		{
			this.mBluetoothDevice = mBluetoothDevice;
		}

		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			Boolean returnValue = false;

			try
			{
				Method createBondMethod;
				createBondMethod = BluetoothDevice.class.getMethod("createBond");
				Logs.showTrace(
						"start to pair to " + mBluetoothDevice.getName() + " address:" + mBluetoothDevice.getAddress());
				returnValue = (Boolean) createBondMethod.invoke(mBluetoothDevice);
			}
			catch (Exception e)
			{
				Logs.showTrace("[pair New Device] fail to pair: " + e.toString());
			}

		}

	}

	class DataStreamStruct
	{
		public int bytesLength = 0;
		public byte[] data;
		public boolean isFoundEndByte = false;

		public DataStreamStruct(int bytesLength, byte[] data, boolean isFoundEndByte)
		{
			this.bytesLength = bytesLength;
			this.data = data;
			this.isFoundEndByte = isFoundEndByte;
		}

	}

}
