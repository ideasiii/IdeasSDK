package sdk.ideas.mdm.restore;

import android.content.Context;


public class RestoreHandler 
{
	private Context mContext = null;
	private boolean mReadLocalInit = false;
	private RestoreHandler.ReturnRestoreAction listener = null;
	public RestoreHandler(Context context, boolean readLocalInit)
	{
		this.mContext = context;
		this.mReadLocalInit = readLocalInit;

	}
	
	//need to modify
	public void restore() 
	{
		// for app to restore
		Thread restoreApp = null;
		
		restoreApp = new Thread(new Restore(mContext, mReadLocalInit, listener));
		
		restoreApp.start();
	}
	
	public void setOnRestoreListener(RestoreHandler.ReturnRestoreAction listener)
	{
		this.listener = listener;
	}
	
	public interface ReturnRestoreAction
	{
		void returnRestoreActionResult(String action);
	}
	
	
}
