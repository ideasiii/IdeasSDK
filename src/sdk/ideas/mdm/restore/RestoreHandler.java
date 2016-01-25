package sdk.ideas.mdm.restore;

import android.content.Context;


public class RestoreHandler 
{
	private Context mContext = null;
	private boolean mReadLocalInit = false;
	

	public RestoreHandler(Context context, boolean readLocalInit)
	{
		this.mContext = context;
		this.mReadLocalInit = readLocalInit;

	}

	public void restore() 
	{
		//for app to restore
		Thread restoreApp =new Thread(new Restore(mContext,mReadLocalInit));
		restoreApp.start();
	}
	
	
}
