package sdk.ideas.mdm.lock;

import android.content.Context;
import sdk.ideas.common.Logs;

public class MDMLockHandler
{
	private ControlLock locker = null;
	public MDMLockHandler(Context context)
	{
		locker = new ControlLock(context);
	}
	public boolean lockNow()
	{
		if(null != locker)
		{
			return locker.lockNow();
		}
		else
		{
			Logs.showTrace("not new MDMLockHandler");
			return false;
		}
	}
	
	
}
