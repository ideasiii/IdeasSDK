package sdk.ideas.ctrl.battery;

import java.util.HashMap;
import android.content.Context;
import android.content.IntentFilter;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ReturnIntentAction;

public class BatteryHandler extends BaseHandler implements ListenReceiverAction
{
	private static BatteryReceiver mBatteryReceiver = null;
	private static Context mContext = null;
	private IntentFilter intentFilter = null;
	private static boolean isReceiverOn = false;

	public BatteryHandler(Context mContext)
	{
		super(mContext);
		BatteryHandler.mContext = mContext;
		mBatteryReceiver = new BatteryReceiver();

		intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
		// intentFilter.addAction("android.intent.action.BATTERY_LOW");

	}

	public void setDetailBatteryInfo(boolean isNeedDetailInfo)
	{
		mBatteryReceiver.setBatteryDetail(isNeedDetailInfo);
	}

	public void setDiffBatteryLevel(int diff)
	{
		mBatteryReceiver.setBatteryDiff(diff);
	}

	@Override
	public void startListenAction()
	{
		try
		{
			if (isReceiverOn == false)
			{
				isReceiverOn = true;
				mBatteryReceiver.setOnReceiverListener(new ReturnIntentAction()
				{
					@Override
					public void returnIntentAction(HashMap<String, String> action)
					{
						callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_BATTERY_HANDLER,
								ResponseCode.METHOD_BATTERY, action);
					}
				});
				BatteryHandler.mContext.registerReceiver(mBatteryReceiver, intentFilter);
			}
		}
		catch (Exception e)
		{
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_BATTERY_HANDLER,
					ResponseCode.METHOD_BATTERY, message);
			message.clear();
			message = null;

		}
	}

	@Override
	public void stopListenAction()
	{
		try
		{
			if (isReceiverOn == true)
			{
				isReceiverOn = false;
				BatteryHandler.mContext.unregisterReceiver(mBatteryReceiver);
			}
		}
		catch (Exception e)
		{
			HashMap<String, String> message = new HashMap<String, String>();
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_BATTERY_HANDLER,
					ResponseCode.METHOD_BATTERY, message);

			message.clear();
			message = null;

		}
	}

}
