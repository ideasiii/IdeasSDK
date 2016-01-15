package sdk.ideas.mdm.restore;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.MDMType;

public class InstallApp extends AsyncTask<String, Void, Void>
{
	private Context mContext;
	private boolean  silentInstall = false;
	public void setContext(Context contextf)
	{
		mContext = contextf;
	}
	private void setSilentInstall(boolean  silentInstall)
	{
		this.silentInstall = silentInstall;
	}

	@Override
	protected Void doInBackground(String... arg0)
	{
		try
		{
			URL url = new URL(arg0[0]);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();
			String savePath = "";
			if(arg0.length > 1)
			{	savePath = arg0[1] ;
			}
			else
			{
				savePath =  "/mnt/sdcard/Download/";
			}	
			File file = new File(savePath);
			file.mkdirs();
			
			String fileName = "";
			if(arg0.length > 2)
			{
				fileName = arg0[2];
			}
			else
			{
				fileName="update.apk";
			}
			/*File outputFile = new File(file, fileName);
			if (outputFile.exists())
			{
				outputFile.delete();
			}
			
			FileOutputStream fos = new FileOutputStream(outputFile);

			InputStream is = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1)
			{
				fos.write(buffer, 0, len1);
			}
			fos.close();
			is.close();
*/
			
			if(silentInstall == false)
			{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(savePath+fileName)),
						"application/vnd.android.package-archive");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag
																// android returned
																// a intent error!
				((Activity)mContext).startActivityForResult(intent,MDMType.REQUEST_CODE_INSTALL_APP);
				}
			else
			{
				Logs.showTrace("in saint install");
				
				
			    String cmd = "adb install -r " +savePath+ fileName;
			    
				Logs.showTrace(cmd);
				
		        Process pr = Runtime.getRuntime().exec(new String[] { "/system/bin/su", "-c", cmd });
		        pr.waitFor();
		        String cmdreturn =null;
				
				BufferedReader bro = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			    BufferedReader bre = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
				
				while ((cmdreturn = bro.readLine()) != null)
				{
					Logs.showTrace(cmdreturn);
				}
				
				
				while ((cmdreturn = bre.readLine()) != null)
				{
					Logs.showTrace(cmdreturn);
				}
			}
		}
		catch (Exception e)
		{
			Logs.showTrace(e.toString());
			Logs.showTrace("Update error! " + e.getMessage());
		}
		return null;
	}

}
