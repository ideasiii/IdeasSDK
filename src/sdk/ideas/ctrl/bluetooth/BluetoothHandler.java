package sdk.ideas.ctrl.bluetooth;

import java.io.IOException;
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
	
	private static BluetoothSocket btSocket = null;  
	
	public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";  
	
	
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
			super.setResponseMessage(ResponseCode.ERR_DEVICE_NOT_SUPPORT_BLUETOOTH, message);
			super.returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SETUP_BLUETOOTH);

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
						setResponseMessage(ResponseCode.ERR_SUCCESS, message);
						returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.METHOD_BOND_STATE_CHANGE_BLUETOOTH);
					}
					
					else if(action.get("action").equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED))
					{
						message.put("message", "SCAN_MODE_CHANGED");
						message.put("mode", action.get("mode"));
						setResponseMessage(ResponseCode.ERR_SUCCESS, message);
						returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.METHOD_SCAN_MODE_CHANGE_BLUETOOTH);
					}
					else if (action.get("action").equals(BluetoothDevice.ACTION_FOUND))
					{
						message.put("message", "New Device Found");
						message.put("deviceName", action.get("deviceName"));
						message.put("deviceAddress", action.get("deviceAddress"));
						

						setResponseMessage(ResponseCode.ERR_SUCCESS, message);
						returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER,
								ResponseCode.METHOD_BLUETOOTH_DISCOVERING_NEW_DEVICE);

					}
					else if (action.get("action").equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
					{
						message.put("message", "Discover finished");
						if (isStillDiscovery() == true)
						{
							stopDiscovery();
							Logs.showTrace("stop success");
						}

						setResponseMessage(ResponseCode.ERR_SUCCESS, message);
						returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_BLUETOOTH_DISCOVER_FINISHED);

					}
					else if (action.get("action").equals(String.valueOf(BluetoothAdapter.STATE_OFF)))
					{
						message.put("message", "bluetooth is OFF");
						setResponseMessage(ResponseCode.ERR_SUCCESS, message);
						returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.BLUETOOTH_IS_OFF);
					}
					else if (action.get("action").equals(String.valueOf(BluetoothAdapter.STATE_ON)))
					{
						message.put("message", "bluetooth is ON");
						setResponseMessage(ResponseCode.ERR_SUCCESS, message);
						returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.BLUETOOTH_IS_ON);
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
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	public void connectDevice(String address)
	{
		boolean isDevicePaired = false;
		boolean isDeviceExist = ArrayListUtility
				.findEqualForBluetoothDeviceLinkableDeviceClass(mBluetoothDeviceLinkableDevice, address);

		for (int i = 0; i < mBluetoothDeviceLinkableDevice.size(); i++)
		{
			Logs.showTrace("address: " + mBluetoothDeviceLinkableDevice.get(i).address + " name: "
					+ mBluetoothDeviceLinkableDevice.get(i).name);
		}
		
		Logs.showTrace("this address device is exist: " + String.valueOf(isDeviceExist));

		// Logs.showTrace(tmp.getAddress());
		if (isDeviceExist == true)
		{
			BluetoothDevice device = null;
			device = isPairedDevices(address);
			if (null != device)
			{
				isDevicePaired = true;
				device = isPairedDevices(address);

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
			}
			else
			{
				device = mBluetoothAdapter.getRemoteDevice(address);
			}
			if (null == device)
			{
				Logs.showTrace("device is null");
				return;
			}
			
			if (isDevicePaired == false)
			{
				// start to pair new device
				try
				{
					Boolean returnValue = false;
					if (device.getBondState() == BluetoothDevice.BOND_NONE)
					{
						// 利用反射方法调用BluetoothDevice.createBond(BluetoothDevice
						// remoteDevice);
						Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
						Logs.showTrace("start to bond to " + device.getName() + " address:" + device.getAddress());
						returnValue = (Boolean) createBondMethod.invoke(device);

					}
					else if (device.getBondState() == BluetoothDevice.BOND_BONDED)
					{
						UUID uuid = UUID.fromString(SPP_UUID);

						btSocket = device.createRfcommSocketToServiceRecord(uuid);
						Logs.showTrace("start to link");
						btSocket.connect();
					}
				}
				catch (IOException e)
				{
					Logs.showTrace(e.toString());
				}
				catch (Exception e)
				{
					Logs.showTrace(e.toString());
				}
			}

		}

		// device.createInsecureRfcommSocketToServiceRecord("");
	
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
					super.setResponseMessage(ResponseCode.ERR_SUCCESS, message);
					super.returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.BLUETOOTH_IS_ON);

					
				}
			}
			else if (turnOn == false)
			{
				if (mBluetoothAdapter.isEnabled() == true)
				{
					mBluetoothAdapter.disable();
				}
				else
				{
					message.put("message", "success");
					super.setResponseMessage(ResponseCode.ERR_SUCCESS, message);
					super.returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.BLUETOOTH_IS_OFF);
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

	/*
	 * public boolean isBluetoothOn() { if (null != mBluetoothAdapter) { return
	 * mBluetoothAdapter.isEnabled(); } return false; }
	 */
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
				super.setResponseMessage(ResponseCode.ERR_BLUETOOTH_CANCELLED_BY_USER, message);
				super.returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SETUP_BLUETOOTH);
			}

		}
		else if(requestCode == CtrlType.REQUEST_CODE_DISCOVERABLE_BLUETOOTH)
		{
			Logs.showTrace("result code : "+ String.valueOf(resultCode));
			if (resultCode == Activity.RESULT_CANCELED || resultCode == Activity.RESULT_FIRST_USER)
			{

				Logs.showTrace("can not let device's bluetooth discoverable cause by user");
				message.put("message", "can not let device's bluetooth discoverable cause by user");
				super.setResponseMessage(ResponseCode.ERR_BLUETOOTH_CANCELLED_BY_USER, message);
				super.returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SETUP_BLUETOOTH);
			}
			else
			{
				// 開啟讓其他連線
				Logs.showTrace("our device bluetooth is discoverable ");

			}
			
		}

	}
	

	public void finish()
	{
		try
		{
			if (btSocket != null)
			{
				btSocket.close();
			}
		}
		catch (IOException e)
		{
			Logs.showTrace(e.toString());
		}
		//BluetoothHandler.this.finish();  
	}
	

	@Override
	public void startListenAction()
	{
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
