package sdk.ideas.ctrl.battery;

import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import sdk.ideas.common.BaseReceiver;
import sdk.ideas.common.Logs;

public class BatteryReceiver extends BaseReceiver
{
	public static int oldLevel = 0;
	public static int oldScale = 1;

	// default 5, unless the old level and new level are difference with 5 or
	// not update
	public static int diff = 5;

	// default false
	private boolean batteryDetail = false;

	public void setBatteryDiff(int diff)
	{
		BatteryReceiver.diff = diff;
	}

	public void setBatteryDetail(boolean isDetail)
	{
		this.batteryDetail = isDetail;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_BATTERY_CHANGED))
		{
			int status = intent.getIntExtra("status", 0);
			int health = intent.getIntExtra("health", 0);
			boolean present = intent.getBooleanExtra("present", false);
			int nowLevel = intent.getIntExtra("level", 0);
			int nowScale = intent.getIntExtra("scale", 0);
			int icon_small = intent.getIntExtra("icon-small", 0);
			int plugged = intent.getIntExtra("plugged", 0);
			int voltage = intent.getIntExtra("voltage", 0);
			double temperature = intent.getIntExtra("temperature", 0) * 0.1;
			String technology = intent.getStringExtra("technology");

			String statusString = "";
			String healthString = "";
			String acString = "";

			switch (status)
			{
			case BatteryManager.BATTERY_STATUS_UNKNOWN:
				statusString = "Unknown";
				break;

			case BatteryManager.BATTERY_STATUS_CHARGING:
				statusString = "Charging";
				break;

			case BatteryManager.BATTERY_STATUS_DISCHARGING:
				statusString = "Discharging";
				break;

			case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
				statusString = "Not Charging";
				break;

			case BatteryManager.BATTERY_STATUS_FULL:
				statusString = "Full";
				break;
			}

			switch (health)
			{
			case BatteryManager.BATTERY_HEALTH_UNKNOWN:
				healthString = "Unknown";
				break;

			case BatteryManager.BATTERY_HEALTH_GOOD:
				healthString = "Good";
				break;

			case BatteryManager.BATTERY_HEALTH_OVERHEAT:
				healthString = "Overheat";
				break;

			case BatteryManager.BATTERY_HEALTH_DEAD:
				healthString = "Dead";
				break;

			case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
				healthString = "Voltage";
				break;

			case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
				healthString = "Unspecified Failure";
				break;
			}

			switch (plugged)
			{
			case BatteryManager.BATTERY_PLUGGED_AC:
				acString = "AC";
				break;

			case BatteryManager.BATTERY_PLUGGED_USB:
				acString = "USB";
				break;
			}

			if (Math.abs(batteryPersent(oldLevel, oldScale) - batteryPersent(nowLevel, nowScale)) >= diff)
			{
				HashMap<String, String> data = new HashMap<String, String>();
				Logs.showTrace(String.valueOf(batteryPersent(nowLevel, nowScale)));
				data.put("level", String.valueOf(batteryPersent(nowLevel, nowScale)));
				oldLevel = nowLevel;
				oldScale = nowScale;

				if (this.batteryDetail == true)
				{
					data.put("status", statusString);
					data.put("health", healthString);
					data.put("present", String.valueOf(present));
					data.put("scale", String.valueOf(nowScale));
					data.put("icon_small", String.valueOf(icon_small));
					data.put("plugged", acString);
					data.put("voltage", String.valueOf(voltage));
					data.put("temperature", String.valueOf(temperature));
					data.put("technology", technology);
				}
				if (null != listener)
				{
					listener.returnIntentAction(data);
				}

			}

			/*
			 * using in debug Log.v("status", statusString); Log.v("health",
			 * healthString); Log.v("present", String.valueOf(present));
			 * Log.v("level", String.valueOf(level)); Log.v("scale",
			 * String.valueOf(scale)); Log.v("icon_small",
			 * String.valueOf(icon_small)); Log.v("plugged", acString);
			 * Log.v("voltage", String.valueOf(voltage)); Log.v("temperature",
			 * String.valueOf(temperature)); Log.v("technology", technology);
			 */

		}

		else if (action.equals(Intent.ACTION_BATTERY_LOW))
		{
			Logs.showTrace("Low Battery");
			// do something after

		}

	}

	private double batteryPersent(int level, int scale)
	{
		// Logs.showTrace("level : " + String.valueOf(level) + " scale : " +
		// String.valueOf(scale));
		if (scale != 0)
			return (level / (scale * 1.0)) * 100;
		else
			return 0;
	}

}
