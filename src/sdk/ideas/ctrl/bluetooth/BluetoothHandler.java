package sdk.ideas.ctrl.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Parcelable;
import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ReturnIntentAction;

public class BluetoothHandler extends BaseHandler implements ListenReceiverAction
{
	protected static BluetoothAdapter mBluetoothAdapter = null;
	private HashMap<String, String> message = null;

	protected static ArrayList<BluetoothDeviceLinkableDevice> mBluetoothDeviceLinkableDevice = new ArrayList<BluetoothDeviceLinkableDevice>();
	
	
	
	private static BluetoothReceiver mBluetoothReceiver = null;
	private static IntentFilter mBluetoothIntentFilter = null;
	private static boolean isRegister = false;
	private int discoverableTime = 300;
	
	
	//Standard SerialPortService ID
	public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";  
	
	private static BluetoothSocket mmSocket = null;  
	public static BluetoothDevice mBluetoothDevice = null;
	
	private OutputStream mmOutputStream = null;
	private InputStream mmInputStream = null;
	
	private volatile boolean stopWorker;
	private Thread workerThread;
	private byte[] readBuffer;
	private int readBufferPosition;
	
	private final static String codeType = "US-ASCII";
	private static boolean nowConnecting = false;
	
	
	public BluetoothHandler(Context context)
	{
		super(context);
		message = new HashMap<String, String>();
		init();
	}

	private void init()
	{
		if (checkDeviceBluetooth() == false)
		{
			message.put("message", "device not support bluetooth module");
			super.callBackMessage(ResponseCode.ERR_DEVICE_NOT_SUPPORT_BLUETOOTH,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SETUP_BLUETOOTH, message);
			return;
		}

		if (null == mBluetoothReceiver)
		{
			//Logs.showTrace("in new mBluetoothReceiver");
			mBluetoothReceiver = new BluetoothReceiver();
			mBluetoothReceiver.setOnReceiverListener(new ReturnIntentAction()
			{
				
				@Override
				protected void finalize() throws Throwable
				{
					Logs.showTrace("mBluetoothReceiver finalize");
				}

				@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
				@SuppressLint("NewApi")
				@Override
				public void returnIntentAction(HashMap<String, String> action)
				{
					message.clear();
					// Logs.showTrace("in returnIntentAction :
					// mBluetoothReceiver");
					/*if(null == action)
						Logs.showTrace("action is null");
					else
					{
						Logs.showTrace(action.get("action"));
					}*/
					
					if(action.get("action").equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
					{
						message.put("message", "BOND_STATE_CHANGED");
						message.put("state", action.get("state"));
						if(action.get("state").equals("BOND_BONDED"))
						{
							connect();
						}
						
						callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.METHOD_BOND_STATE_CHANGE_BLUETOOTH, message);
						
					}
					
					else if(action.get("action").equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED))
					{
						message.put("message", "SCAN_MODE_CHANGED");
						message.put("mode", action.get("mode"));
						
						callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.METHOD_SCAN_MODE_CHANGE_BLUETOOTH, message);
						
					}
					else if (action.get("action").equals(BluetoothDevice.ACTION_FOUND))
					{
						message.put("message", "New Device Found");
						message.put("deviceName", action.get("deviceName"));
						message.put("deviceAddress", action.get("deviceAddress"));
						
						callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.METHOD_BLUETOOTH_DISCOVERING_NEW_DEVICE, message);
					}
					else if (action.get("action").equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
					{
						message.put("message", "Discover finished");
						if (isStillDiscovery() == true)
						{
							stopDiscovery();
							Logs.showTrace("stop success");
						}
						callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_BLUETOOTH_DISCOVER_FINISHED, message);

					}
					else if (action.get("action").equals(String.valueOf(BluetoothAdapter.STATE_OFF)))
					{
						message.put("message", "bluetooth is OFF");
						callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.BLUETOOTH_IS_OFF, message);
						
					}
					else if (action.get("action").equals(String.valueOf(BluetoothAdapter.STATE_ON)))
					{
						message.put("message", "bluetooth is ON");
						callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.BLUETOOTH_IS_ON, message);
						
					}
					
				}
			});
			//Logs.showTrace("mBluetoothReceiver set finish");
		}
	
		if(null == mBluetoothIntentFilter)
		{
			mBluetoothIntentFilter= new IntentFilter();
			mBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
			mBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			mBluetoothIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
			mBluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
			mBluetoothIntentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
			
		}
		
	

	}
	
	private void connect()
	{
		if(null != mBluetoothDevice)
		{
			UUID uuid = UUID.fromString(SPP_UUID); 
	       
			try
			{
				mmSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
				mmSocket.connect();
				mmOutputStream = mmSocket.getOutputStream();
				mmInputStream = mmSocket.getInputStream();
				nowConnecting = true;
				beginListenForData();
			}
			catch (IOException e)
			{
				Logs.showTrace(e.toString());
			}
			catch(Exception e)
			{
				Logs.showTrace(e.toString());
			}
			finally
			{
				
			}

			
		}
	}
	
	
	private void beginListenForData()
	{

		final byte delimiter = 10; // This is the ASCII code for a newline
									// character

		stopWorker = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable()
		{
			public void run()
			{
				while (!Thread.currentThread().isInterrupted() && !stopWorker)
				{
					try
					{
						int bytesAvailable = mmInputStream.available();
						if (bytesAvailable > 0)
						{
							byte[] packetBytes = new byte[bytesAvailable];
							mmInputStream.read(packetBytes);
							for (int i = 0; i < bytesAvailable; i++)
							{
								byte b = packetBytes[i];
								if (b == delimiter)
								{
									byte[] encodedBytes = new byte[readBufferPosition];
									System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
									final String returnMessage = new String(encodedBytes, codeType);
									readBufferPosition = 0;

									message.put("message", returnMessage);
									callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
											ResponseCode.METHOD_RETURN_MESSAGE_BLUETOOTH, message);
								}
								else
								{
									readBuffer[readBufferPosition++] = b;
								}
							}
						}
					}
					catch (IOException ex)
					{
						stopWorker = true;
					}
				}
			}
		});

		workerThread.start();
	}

	public void sendData(String msg) 
	{
		message.clear();
		if(nowConnecting == false)
		{
			return;
		}
		if(null == msg)
		{
			
			message.put("message", "message is null");
			callBackMessage(ResponseCode.ERR_ILLEGAL_ARGUMENT_EXCEPTION,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SEND_MESSAGE_BLUETOOTH, message);
			return;
		}
		if (null != msg)
		{
			msg += "\n";
			try
			{
				mmOutputStream.write(msg.getBytes(codeType));
				message.put("message", "success");
				callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SEND_MESSAGE_BLUETOOTH, message);
				
			}
			catch (UnsupportedEncodingException e)
			{

				message.put("message", e.toString());
				callBackMessage(ResponseCode.ERR_UNSUPPORTED_ENCODING_EXCEPTION,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SEND_MESSAGE_BLUETOOTH, message);
			}
			catch (IOException e)
			{
				message.put("message", e.toString());
				callBackMessage(ResponseCode.ERR_IO_EXCEPTION,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SEND_MESSAGE_BLUETOOTH, message);
			}
			finally
			{
				message.clear();
			}
			Logs.showTrace("Data Sent");
		}
	}

	private void closeBluetooth() throws IOException
	{
		if (nowConnecting == true)
		{
			stopWorker = true;
			mmOutputStream.close();
			mmInputStream.close();
			mmSocket.close();
			nowConnecting = false;
		}
	}
	
	public void closeBluetoothLink() 
	{
		try
		{
			closeBluetooth();
		}
		catch (IOException e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_IO_EXCEPTION,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_CLOSE_BLUETOOTH_LINK, message);
		}
	}
	
	
	public void connectDeviceByName(String name)
	{
		connectDeviceByMacAddress(name);
	}
	
	
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	public void connectDeviceByMacAddress(String address)
	{
		stopDiscovery();
		boolean isDevicePaired = false;
		BluetoothHandler.BluetoothDeviceLinkableDevice deviceExist = ArrayListUtility
				.findEqualForBluetoothDeviceLinkableDeviceClass(mBluetoothDeviceLinkableDevice, address);

		/*for debugging
		for (int i = 0; i < mBluetoothDeviceLinkableDevice.size(); i++)
		{
			Logs.showTrace("address: " + mBluetoothDeviceLinkableDevice.get(i).address + " name: "
					+ mBluetoothDeviceLinkableDevice.get(i).name);
		}*/
		
		
		
		// Logs.showTrace(tmp.getAddress());
		if (null != deviceExist)
		{
			Logs.showTrace(
					"this device is exist: name: " + deviceExist.name + " address: " + deviceExist.address);

			if (deviceExist.address.equals(address) == false)
			{
				address = deviceExist.address;
			}

			mBluetoothDevice= null;
			mBluetoothDevice = isPairedDevices(address);
			
			if (null != mBluetoothDevice)
			{
				isDevicePaired = true;
			}
			
			if(isDevicePaired)
			{
				Logs.showTrace("device is already paired");
				showDeviceService(mBluetoothDevice);
				connect();
			}
			else
			{
				Logs.showTrace("device is not paired");
				// start to pair new device
				mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
				if (null == mBluetoothDevice)
				{
					Logs.showTrace("device is null");
					return;
				}
		
				showDeviceService(mBluetoothDevice);
			
				try
				{
					Boolean returnValue = false;

					Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
					Logs.showTrace("start to pair to " + mBluetoothDevice.getName() + " address:" + mBluetoothDevice.getAddress());
					returnValue = (Boolean) createBondMethod.invoke(mBluetoothDevice);
					
				}
				catch (Exception e)
				{
					Logs.showTrace(e.toString());
				}
			}

		}

		// device.createInsecureRfcommSocketToServiceRecord("");
	
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
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
	
	
	private BluetoothDevice isPairedDevices(String address)
	{
		Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();
		if (mPairedDevices.size() > 0)
		{
			for (BluetoothDevice mDevice : mPairedDevices)
			{
				if (mDevice.getAddress().equals(address))
					return mDevice;
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
			message.clear();
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
					callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.BLUETOOTH_IS_ON, message);
				}
			}
			else if (turnOn == false)
			{
				if (mBluetoothAdapter.isEnabled() == true)
				{
					mBluetoothAdapter.disable();
					try
					{
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
					super.callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.BLUETOOTH_IS_OFF, message);
				}
			}
		}

	}

	public void stopDiscovery()
	{
		if (null != mBluetoothAdapter)
		{
			mBluetoothAdapter.cancelDiscovery();
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
	/*	if(null == mBluetoothAdapter)
		{
			Logs.showTrace("mBluetoothAdapter = null");
			return;
		}
	
		if(mBluetoothAdapter.isEnabled() ==false)
		{
			Logs.showTrace("mBluetoothAdapter = unable");
			return;
		}*/
		
		if (null != mBluetoothAdapter && mBluetoothAdapter.isEnabled())
		{

			mBluetoothDeviceLinkableDevice.clear();
			
			mBluetoothAdapter.startDiscovery();
			//Logs.showTrace("mBluetoothAdapter startDiscoverying");
			
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
				super.callBackMessage(ResponseCode.ERR_BLUETOOTH_CANCELLED_BY_USER,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SETUP_BLUETOOTH, message);
			}

		}
		else if(requestCode == CtrlType.REQUEST_CODE_DISCOVERABLE_BLUETOOTH)
		{
			Logs.showTrace("result code : "+ String.valueOf(resultCode));
			if (resultCode == Activity.RESULT_CANCELED || resultCode == Activity.RESULT_FIRST_USER)
			{

				Logs.showTrace("can not let device's bluetooth discoverable cause by user");
				message.put("message", "can not let device's bluetooth discoverable cause by user");
				super.callBackMessage(ResponseCode.ERR_BLUETOOTH_CANCELLED_BY_USER,CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SETUP_BLUETOOTH, message);
			}
			else
			{
				// 開啟讓其他連線
				Logs.showTrace("our device bluetooth is discoverable ");

			}
			
		}

	}
	


	

	@Override
	public void startListenAction()
	{
		//Logs.showTrace("startListenAction isRegister:"+ String.valueOf(isRegister));
		if (isRegister == false)
		{
			//Logs.showTrace("registerReceiver");
			mContext.registerReceiver(mBluetoothReceiver, mBluetoothIntentFilter);
		
			isRegister = true;
		}
	}

	@Override
	public void stopListenAction()
	{
		//Logs.showTrace("stopListenAction isRegister:"+ String.valueOf(isRegister));
		if(isRegister == true)
		{
			mContext.unregisterReceiver(mBluetoothReceiver);
			isRegister = false;
		}
	}
	
	public static class BluetoothDeviceLinkableDevice 
	{
		public String address = null;
		public String name = null;
		
		public BluetoothDeviceLinkableDevice(String name,String address)
		{
			this.address = address;
			this.name = name;
		}
		
		
		
		
	}
	

}
