package sdk.ideas.iot.amx.player;

import android.content.Context;
import android.os.Message;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.StatusQueryBehavior;

public class AMXBDPlayer extends AMXBaseHandler implements StatusQueryBehavior
{
	
	public AMXBDPlayer(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);
	}

	@Override
	public void statusQuery(int index, int requestState)
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
