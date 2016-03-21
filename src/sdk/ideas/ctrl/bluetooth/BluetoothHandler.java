package sdk.ideas.ctrl.bluetooth;

import java.util.HashMap;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;

public class BluetoothHandler extends BaseHandler
{
	 private static BluetoothAdapter mBluetoothAdapter = null;
	 private HashMap<String,String> message = null;
	
	
	public BluetoothHandler(Context context)
	{
		super(context);
		message = new HashMap<String,String>();
		
	}
	
	public void setBluetooth(boolean on)
	{
		
		if(checkDeviceBluetooth() == false)
		{
			message.put("message", "device not support bluetooth module");
			super.setResponseMessage(ResponseCode.ERR_DEVICE_NOT_SUPPORT_BLUETOOTH, message);
			super.returnRespose(CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER, ResponseCode.METHOD_SETUP_BLUETOOTH);
			
			return;
		}
		if (on == true)
		{
			if (mBluetoothAdapter.isEnabled() == false)
			{
				Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				((Activity) mContext).startActivityForResult(turnOnIntent, CtrlType.REQUEST_CODE_ENABLE_BLUETOOTH);
			}
		}
		else
		{
			mBluetoothAdapter.disable();
		}
		
		
		
	}
	
	private boolean checkDeviceBluetooth()
	{
		if(null == mBluetoothAdapter)
		{
			if(null != BluetoothAdapter.getDefaultAdapter())
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

			if (mBluetoothAdapter.isEnabled())
			{


			}
			else
			{


			}

		}

		
		
	}
	
	
	
	
}
