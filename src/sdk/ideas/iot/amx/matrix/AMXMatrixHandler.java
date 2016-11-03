package sdk.ideas.iot.amx.matrix;

import android.content.Context;
import android.os.Message;
import sdk.ideas.iot.amx.AMXBaseHandler;
import sdk.ideas.iot.amx.MatrixBehavior;
import sdk.ideas.iot.amx.StatusQueryBehavior;

public class AMXMatrixHandler extends AMXBaseHandler implements MatrixBehavior, StatusQueryBehavior
{
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

	public AMXMatrixHandler(Context mContext, String strIP, int nPort)
	{
		super(mContext, strIP, nPort);
	}

	@Override
	public void changeMatrixBehavior(int index)
	{

	}

	@Override
	public void statusQuery(int index, int requestState)
	{
		
	}



}
