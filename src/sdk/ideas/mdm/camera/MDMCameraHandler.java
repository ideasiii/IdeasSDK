package sdk.ideas.mdm.camera;

import android.content.Context;
import sdk.ideas.common.Logs;

public class MDMCameraHandler
{
	private ControlCamera camera = null;
	public MDMCameraHandler (Context context)
	{
		camera = new ControlCamera(context);
		
	}
	public void setCamera(boolean disable)
	{
		if(null!= camera)
		{
			camera.setCamera(disable);
		}
		else
		{
			Logs.showTrace("not new MDMCameraHandler");
		}
		}
	
	

}
