package sdk.ideas.iot.amx.volume;

import android.content.Context;
import android.os.Message;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.PowerBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;

public class AMXVolumeHandler extends AMXBaseHandler implements PowerBehavior, StatusQueryBehavior
{


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

	@Override
	public void handleControlMessage(Message msg)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleStatusMessage(Message msg)
	{
		// TODO Auto-generated method stub
		
	}

}
