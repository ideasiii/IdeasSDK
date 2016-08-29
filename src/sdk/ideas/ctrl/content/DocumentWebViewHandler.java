package sdk.ideas.ctrl.content;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

public class DocumentWebViewHandler extends BaseHandler
{
	private Intent intent = null;
	private HashMap<String, String> message = null;

	public DocumentWebViewHandler(Context mContext)
	{
		super(mContext);
		message = new HashMap<String, String>();
		intent = new Intent(mContext, DocumentWebViewer.class);

	}

	public void startIntent(String uRL)
	{
		Thread linkable = new Thread(new IsLinkable(uRL));
		linkable.start();
	}

	private void isLinkable(String uRL)
	{
		try
		{
			// parse not English word to utf-8 format
			uRL = URLEncoder.encode(uRL, "UTF-8").replace("%3A", ":").replace("%2F", "/");
			int responseCode = getResponseCode(uRL);
			if (null != mContext && null != uRL && (responseCode == 200))
			{
				// Logs.showTrace( "code is :"+ String.valueOf(responseCode));
				intent.putExtra("linkURL", uRL);
				((Activity) mContext).startActivityForResult(intent, CtrlType.REQUEST_CODE_WEBVIEW);

				message.put("message", "success");
				super.callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER, ResponseCode.METHOD_START_WEBVIEW_INTENT, message);
				
			}
			else
			{
				message.put("message", "url is not linkable  " + "http code is :" + String.valueOf(responseCode));
				// Logs.showTrace("url is not linkable " + "code is :"+
				// String.valueOf(responseCode));
				callBackMessage(ResponseCode.ERR_URL_UNLINKABLE,CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER, ResponseCode.METHOD_START_WEBVIEW_INTENT, message);
			}
		}
		catch (SocketException e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION,CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER, ResponseCode.METHOD_START_WEBVIEW_INTENT, message);
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_UNKNOWN,CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER, ResponseCode.METHOD_START_WEBVIEW_INTENT, message);
		}
		finally
		{
			message.clear();

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
		if (resultCode == Activity.RESULT_OK)
		{
			// finish
			Logs.showTrace("OK!");
		}
		else if (resultCode == Activity.RESULT_CANCELED)
		{
			// any error will return
			Logs.showTrace("ERROR!");
		}

	}

	private int getResponseCode(String urlString) throws MalformedURLException, IOException
	{
		URL u = new URL(urlString);
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET");
		huc.connect();
		return huc.getResponseCode();
	}

	public class IsLinkable implements Runnable
	{
		private String uRL = null;

		@Override
		public void run()
		{
			isLinkable(uRL);
		}

		public IsLinkable(String uRL)
		{
			this.uRL = uRL;
		}

	}

}
