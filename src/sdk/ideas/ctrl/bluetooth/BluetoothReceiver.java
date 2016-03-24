package sdk.ideas.ctrl.bluetooth;

import java.util.ArrayList;
import java.util.Iterator;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import sdk.ideas.common.BaseReceiver;
import sdk.ideas.common.Logs;
import sdk.ideas.ctrl.bluetooth.BluetoothHandler.BluetoothDeviceLinkableDevice;

public class BluetoothReceiver extends BaseReceiver
{
	private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();
	public BluetoothReceiver()
	{
		super();
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	@Override
	public void onReceive(Context context, Intent intent)
	{
		actionData.clear();
		if(actionData.isEmpty() == false)
		{
			Logs.showTrace("ERROR to clear action data");
		}
		
		//if (null == listener)
		//{
		//	Logs.showTrace("listener is null" +
		//	System.identityHashCode(listener));
		//}
		if (null != listener)
		{
			//Logs.showTrace("listener is not null"+
			//		System.identityHashCode(listener));
			
			
			//配對狀態
			if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction()))
			{

				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String strState = "";
				switch (device.getBondState())
				{
				case BluetoothDevice.BOND_BONDING:// 正在配對
					strState = "BOND_BONDING";
					break;
				case BluetoothDevice.BOND_BONDED:// 已經配對
					strState = "BOND_BONDED";
					break;
				case BluetoothDevice.BOND_NONE:// 取消配對
					strState = "BOND_NONE";
				default:
					break;
				}

				actionData.put("action", intent.getAction());
				actionData.put("state", strState);
				actionData.put("deviceName", device.getName());
				actionData.put("deviceAddress", device.getAddress());
			}
			//future will write to judge the scan mode change code
			else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(intent.getAction()))
			{
				int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
				String strMode = "";

				switch (mode)
				{
				case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
					strMode = "SCAN_MODE_CONNECTABLE_DISCOVERABLE";
					break;
				case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
					strMode = "SCAN_MODE_CONNECTABLE";
					break;
				case BluetoothAdapter.SCAN_MODE_NONE:
					strMode = "SCAN_MODE_NONE";
					break;
				}
				//for debugging
				//	if(mode == -2147483648)
				//	{
				//		Logs.showTrace("ERROR in scan mode change");
				//	}
				
				actionData.put("action", intent.getAction());
				actionData.put("mode", strMode);
				
			}
			// When discovery finds a device
			else if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))
			{
				actionData.put("action", intent.getAction());

				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				BluetoothHandler.mBluetoothDeviceLinkableDevice.add(new BluetoothDeviceLinkableDevice(device.getName(),device.getAddress()));
				actionData.put("deviceName", device.getName());
				actionData.put("deviceAddress", device.getAddress());
				btDeviceList.add(device);
				// debug use
				// Logs.showTrace("action: "+ BluetoothDevice.ACTION_FOUND);
				// Logs.showTrace("deviceName: "+actionData.get("deviceName"));
				// Logs.showTrace("deviceAddress:"+actionData.get("deviceAddress"));
				
			}
			// When discover finish
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction()))
			{
				actionData.put("action", BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

				// debug use
				// Logs.showTrace("action: "+
				// BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
				// Logs.showTrace("data:  "+actionData);
				
				 Iterator<BluetoothDevice> itr = btDeviceList.iterator();
	             while (itr.hasNext()) 
				 {
	               // Get Services for paired devices
	               BluetoothDevice device = itr.next();
	               //Logs.showTrace("\nGetting Services for " + device.getName() + ", " + device);
	               if(device.fetchUuidsWithSdp() == false) 
				   {
	            	   Logs.showTrace("\nSDP Failed for " + device.getName());
	            	   
	               }
	               else
	               {
						/* for debugging 
						if (null != device.getUuids())
						{
							Parcelable a[] = device.getUuids();
							for (int i = 0; i < a.length; i++)
							{
								Logs.showTrace(device.getName() + ": service : " + a[i]);
							}
						}*/
	               }
	               
	             }

			}
			//when bluetooth state change
			else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction()))
			{
				int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);

				switch (state)
				{
				case BluetoothAdapter.STATE_OFF:
					// Indicates the local Bluetooth adapter is off.
					actionData.put("action", String.valueOf(BluetoothAdapter.STATE_OFF));
					break;
				case BluetoothAdapter.STATE_ON:
					// Indicates the local Bluetooth adapter is on, and ready for use.
					actionData.put("action", String.valueOf(BluetoothAdapter.STATE_ON));
					break;
					
				case BluetoothAdapter.STATE_TURNING_ON:
					// Indicates the local Bluetooth adapter is turning on.
					// However
					// local clients should wait for STATE_ON before attempting
					// to
					// use the adapter.
					return;
				case BluetoothAdapter.STATE_TURNING_OFF:
					// Indicates the local Bluetooth adapter is turning off.
					// Local
					// clients should immediately attempt graceful disconnection
					// of
					// any remote links.
					return;
				}
			}
			listener.returnIntentAction(actionData);
		}
	}

	

}
