package sdk.ideas.ctrl.content;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
public class DocumentWebViewHandler extends BaseHandler
{
	private Context mContext = null;
	private Intent intent  = null;
	public DocumentWebViewHandler(Context mContext)
	{
		this.mContext = mContext;
		intent = new Intent(mContext, DocumentWebViewer.class);
		
	}

	public void startIntent(String uRL)
	{
		
		try
		{
			if (null != mContext && null != uRL && getResponseCode(uRL)!=200)
			{
				intent.putExtra("linkURL", uRL);
				((Activity) mContext).startActivityForResult(intent, CtrlType.WEBVIEW_REQUEST_CODE);
			}
			else
			{
				Logs.showTrace("url is not linkable");
			}
		}
		catch (Exception e)
		{
			Logs.showTrace(e.toString());
		}

	}
	
	public void setZoomControls(boolean zoomControls)
	{
		if (null != intent)
		{
			intent.putExtra("zoomControls", zoomControls);
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(resultCode == Activity.RESULT_OK)
		{
			
		}
		else if(resultCode == Activity.RESULT_CANCELED)
		{
			
		}
		
	}

	public static int getResponseCode(String urlString) throws MalformedURLException, IOException
	{
		URL u = new URL(urlString);
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET");
		huc.connect();
		return huc.getResponseCode();
	}
	
	
	

}
