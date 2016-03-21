package sdk.ideas.ctrl.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import sdk.ideas.common.BaseReceiver;

public class BluetoothReceiver extends BaseReceiver
{
	//ArrayList<>
	public BluetoothReceiver()
	{
		
	}
	
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		

		// When discovery finds a device

		if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))
		{
			actionData.put("action", intent.getAction()) ;

			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			
			
			//actionData
			//.add(device.getName() + "\n" + device.getAddress());

		}

	}
	
	public class BluetoothLinkableDevice
	{
		public String deviceName = null;
		public String deviceMacAddress = null;

		public BluetoothLinkableDevice(String deviceName, String deviceMacAddress)
		{
			this.deviceMacAddress = deviceMacAddress;
			this.deviceName = deviceName;
		}

	}
	
	

}
