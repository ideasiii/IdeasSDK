package sdk.ideas.iot.amx.mode;

import android.content.Context;
import android.os.Message;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.ModeBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;

public class AMXModeHandler extends AMXBaseHandler implements ModeBehavior, StatusQueryBehavior
{
	@Override
	public void handleMessage(Message msg)
	{
		
	}
	
	
	
	public AMXModeHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);
	}

	
	

	@Override
	public void statusQuery(int index, int requestState)
	{
		
	}



	@Override
	public void changeModeBehavior(int index)
	{
		
	}

}
