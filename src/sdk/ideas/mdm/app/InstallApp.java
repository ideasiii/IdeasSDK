package sdk.ideas.mdm.app;

import java.io.File;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.mdm.MDMType;

public class InstallApp
{

	public static void installApplication(Context mContext, String uRLPath, String savePath, String fileName)
	{
		
		
		IOFileHandler.urlDownloader(uRLPath, savePath, fileName);
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(IOFileHandler.getExternalStorageDirectory()+"/"+savePath + fileName)), 
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag
														// android returned
														// a intent error!
		((Activity) mContext).startActivityForResult(intent, MDMType.REQUEST_CODE_INSTALL_APP);
	}
	
	public static class InstallAppRunnable implements Runnable
	{
		private String uRLPath = null;
		private String savePath = null;
		private String fileName = null;
		private Context mContext = null;
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			IOFileHandler.urlDownloader(uRLPath, savePath, fileName);
			
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(IOFileHandler.getExternalStorageDirectory()+"/"+savePath + fileName)), 
					"application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag
															// android returned
															// a intent error!
			((Activity) mContext).startActivityForResult(intent, MDMType.REQUEST_CODE_INSTALL_APP);
			
			
		}

		public InstallAppRunnable( Context mContext,String uRLPath, String savePath, String fileName)
		{
			this.fileName = fileName;
			this.mContext = mContext;
			this.savePath = savePath;
			this.uRLPath = uRLPath;
		}
		
		
	}
	

	
	
	
	

}

