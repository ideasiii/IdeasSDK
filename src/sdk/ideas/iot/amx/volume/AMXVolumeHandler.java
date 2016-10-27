package sdk.ideas.iot.amx.volume;

import android.content.Context;
import android.os.Message;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.PowerBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;

public class AMXVolumeHandler extends AMXBaseHandler implements PowerBehavior, StatusQueryBehavior
{
	@Override
	public void handleMessage(Message msg)
	{
		if (msg.what == CtrlType.MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER)
		{
			if (msg.arg2 == ResponseCode.METHOD_COTROL_COMMAND_AMX)
			{
				
				
				
			}
			else if(msg.arg2 == ResponseCode.METHOD_STATUS_COMMAND_AMX)
			{
				
				
			}
		}
	}

	public AMXVolumeHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);

	}

	@Override
	public void statusQuery(int index,int requestState)
	{
		
		
		
	}

	@Override
	public void onBehavior(int index)
	{

	}

	@Override
	public void offBehavior(int index)
	{

	}

}
