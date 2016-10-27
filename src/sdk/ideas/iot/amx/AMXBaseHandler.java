package sdk.ideas.iot.amx;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;

public abstract class AMXBaseHandler extends BaseHandler
{
	public abstract void handleMessage(Message msg);

	protected Handler privateHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			AMXBaseHandler.this.handleMessage(msg);
		}

	};
	protected AMXDataTransmitHandler mAMXDataTransmitHandler = null;

	public AMXBaseHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext);

		mAMXDataTransmitHandler = new AMXDataTransmitHandler(mContext, strIP, nPort);
		mAMXDataTransmitHandler.setHandler(privateHandler);

	}

	
	protected JSONObject trasferToJsonCommand(int commandType, int function, int device, int command)

	{
		JSONObject obj = new JSONObject();
		try
		{
			obj.put("function", function);

			obj.put("device", device);

			switch (commandType)
			{
			case AMXParameterSetting.TYPE_CONTROL_COMMAND:
				obj.put("control", command);

				break;
			case AMXParameterSetting.TYPE_STATUS_COMMAND:
				obj.put("request-status", command);
				break;

			}
		}
		catch (JSONException e)
		{
			Logs.showError(e.toString());
			return null;
		}
		return obj;

	}

}
