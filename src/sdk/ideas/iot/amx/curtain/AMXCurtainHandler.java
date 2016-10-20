package sdk.ideas.iot.amx.curtain;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXDataTransmitHandler;
import sdk.ideas.iot.amx.LiftingBehavior;

public class AMXCurtainHandler extends BaseHandler implements LiftingBehavior
{
	private Handler privateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER:
				if (msg.arg2 == ResponseCode.METHOD_COTROL_COMMAND_AMX)
				{

				}

				break;

			}

		}
	};
	private static AMXDataTransmitHandler mAMXDataTransmitHandler = null;

	public AMXCurtainHandler(Context mContext, String strIP, int port)
	{
		super(mContext);
		mAMXDataTransmitHandler = new AMXDataTransmitHandler(mContext, strIP, port);
		
		mAMXDataTransmitHandler.setHandler(privateHandler);
		
		

	}

	@Override
	public void upBehavior()
	{
		mAMXDataTransmitHandler.sendControlCommand(command);
	}

	@Override
	public void downBehavior()
	{
		mAMXDataTransmitHandler.sendControlCommand(command);
	}

}
