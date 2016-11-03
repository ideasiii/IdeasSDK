package sdk.ideas.iot.amx.project;

import android.content.Context;
import android.os.Message;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.LiftingBehavior;
import sdk.ideas.iot.amx.PowerBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;
import sdk.ideas.iot.amx.VideoSignalBehavior;
import sdk.ideas.iot.amx.VolumeBehavior;

public class AMXProjectHandler extends AMXBaseHandler
		implements VolumeBehavior, VideoSignalBehavior, LiftingBehavior, PowerBehavior, StatusQueryBehavior
{

	@Override
	public void handleControlMessage(Message msg)
	{
		
	}

	@Override
	public void handleStatusMessage(Message msg)
	{
		
	}


	public AMXProjectHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);
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
	public void upBehavior(int index)
	{

	}

	@Override
	public void downBehavior(int index)
	{

	}

	@Override
	public void hdmiBehavior(int index)
	{

	}

	@Override
	public void vgaBehavior(int index)
	{

	}

	@Override
	public void muteBehavior(int index)
	{

	}

	@Override
	public void unMuteBehavior(int index)
	{

	}

	@Override
	public void statusQuery(int index, int requestState)
	{

	}


}
