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
import android.os.Bundle;
import android.os.ResultReceiver;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

public class DocumentWebViewHandler extends BaseHandler
{
	public static final String ACTION_CLOSE = "sdk.ideas.ctrl.content.DocumentWebViewer.ACTION_CLOSE";
	private Intent intent = null;
	public static final String SELF_CLOSE_RECEIVER = "SELF_CLOSE_RECEIVER";
	public static final String SELF_CLOSE_MESSAGE = "SELF_CLOSE_MESSAGE";
	public static final int SELF_RESULT_CODE = 10;

	public DocumentWebViewHandler(Context mContext)
	{
		super(mContext);
		intent = new Intent(mContext, DocumentWebViewer.class);

	}

	public void startIntent(String uRL)
	{
		Thread linkable = new Thread(new IsLinkable(uRL));
		linkable.start();
	}

	private void isLinkable(String uRL)
	{
		HashMap<String, String> message = new HashMap<String, String>();
		try
		{
			// parse not English word to utf-8 format
			uRL = URLEncoder.encode(uRL, "UTF-8").replace("%3A", ":").replace("%2F", "/");
			int responseCode = getResponseCode(uRL);
			if (null != mContext && null != uRL && (responseCode == 200))
			{
				// Logs.showTrace( "code is :"+ String.valueOf(responseCode));
				intent.putExtra("linkURL", uRL);
				intent.putExtra(SELF_CLOSE_RECEIVER, new SelfCloseReceiver());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try
				{
					mContext.startActivity(intent);
				}
				catch (Exception e)
				{
					message.put("message", e.toString());
					super.callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER,
							ResponseCode.METHOD_START_WEBVIEW_INTENT, message);
				}

				message.put("message", "success");
				super.callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER,
						ResponseCode.METHOD_START_WEBVIEW_INTENT, message);

			}
			else
			{
				message.put("message", "url is not linkable  " + "http code is :" + String.valueOf(responseCode));
				// Logs.showTrace("url is not linkable " + "code is :"+
				// String.valueOf(responseCode));
				callBackMessage(ResponseCode.ERR_URL_UNLINKABLE, CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER,
						ResponseCode.METHOD_START_WEBVIEW_INTENT, message);
			}
		}
		catch (SocketException e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_NO_SPECIFY_USE_PERMISSION, CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER,
					ResponseCode.METHOD_START_WEBVIEW_INTENT, message);
		}
		catch (Exception e)
		{
			message.put("message", e.toString());
			callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER,
					ResponseCode.METHOD_START_WEBVIEW_INTENT, message);
		}
		finally
		{
			message.clear();

		}
	}

	public void closeActivity()
	{
		
		if (getActivityState())
		{
			Logs.showTrace("##now close DocumentWebViewerActivity##");
			Intent myIntent = new Intent(ACTION_CLOSE);
			(mContext).sendBroadcast(myIntent);
		}

	}

	
	public static synchronized boolean getActivityState()
	{
		return DocumentWebViewer.getActivityState();
	}
	
	

	public void setZoomControls(boolean zoomControls)
	{
		if (null != intent)
		{
			intent.putExtra("zoomControls", zoomControls);
		}
	}

	/*
	 * public void onActivityResult(int requestCode, int resultCode, Intent
	 * data) {
	 * 
	 * HashMap<String, String> message = new HashMap<String, String>(); if
	 * (resultCode == Activity.RESULT_OK) { // finish Logs.showTrace("OK!");
	 * message.put("message", "close successful!");
	 * super.callBackMessage(ResponseCode.ERR_SUCCESS,
	 * CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER,
	 * ResponseCode.METHOD_FINISH_WEBVIEW_INTENT, message); Logs.showTrace(
	 * "finish OK!");
	 * 
	 * } else if (resultCode == Activity.RESULT_CANCELED) { // any error will
	 * return Logs.showTrace("ERROR!"); message.put("message", "close ERROR!");
	 * super.callBackMessage(ResponseCode.ERR_UNKNOWN,
	 * CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER,
	 * ResponseCode.METHOD_FINISH_WEBVIEW_INTENT, message); Logs.showTrace(
	 * "finish ERROR!"); }
	 * 
	 * 
	 * }
	 */

	private int getResponseCode(String urlString) throws MalformedURLException, IOException
	{
		URL u = new URL(urlString);
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		huc.setRequestMethod("GET");
		huc.connect();
		return huc.getResponseCode();
	}

	/**
	 * Used by an activity to send a result back to our service.
	 */
	class SelfCloseReceiver extends ResultReceiver
	{

		public SelfCloseReceiver()
		{
			super(null);
		}

		/**
		 * Called when there's a result available.
		 */
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData)
		{
			if (resultCode != SELF_RESULT_CODE)
			{
				return;
			}

			

			int result = resultData.getInt(SELF_CLOSE_MESSAGE);
			HashMap<String, String> message = new HashMap<String, String>();
			if (result == Activity.RESULT_OK)
			{
				// finish
				Logs.showTrace("OK!");
				message.put("message", "close successful!");
				callBackMessage(ResponseCode.ERR_SUCCESS, CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER,
						ResponseCode.METHOD_FINISH_WEBVIEW_INTENT, message);
				Logs.showTrace("finish OK!");

			}
			else if (result == Activity.RESULT_CANCELED)
			{
				// any error will return
				Logs.showTrace("ERROR!");
				message.put("message", "close ERROR!");
				callBackMessage(ResponseCode.ERR_UNKNOWN, CtrlType.MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER,
						ResponseCode.METHOD_FINISH_WEBVIEW_INTENT, message);
				Logs.showTrace("finish ERROR!");
			}

		}

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
