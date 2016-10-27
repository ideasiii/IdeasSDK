package sdk.ideas.iot.amx.player;

import android.content.Context;
import android.os.Message;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.StatusQueryBehavior;

public class AMXBDPlayer extends AMXBaseHandler implements StatusQueryBehavior
{
	@Override
	public void handleMessage(Message msg)
	{

	}

	public AMXBDPlayer(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);
	}

	@Override
	public void statusQuery(int index, int requestState)
	{

	}

}
