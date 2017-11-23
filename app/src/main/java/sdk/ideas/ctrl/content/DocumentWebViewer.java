package sdk.ideas.ctrl.content;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import sdk.ideas.common.CtrlType;

public class DocumentWebViewer extends Activity
{

	private Intent intent = null;
	private WebView mWebView = null;
	private boolean anyError = false;
	private CloseReceiver mCloseReceiver = null;
	
	private static boolean isStartActivity = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		DocumentWebViewer.setIsStartActivity(true);
		IntentFilter filter = new IntentFilter(DocumentWebViewHandler.ACTION_CLOSE);
		mCloseReceiver = new CloseReceiver();
		registerReceiver(mCloseReceiver, filter);

		intent = getIntent();
		String linkURL = intent.getStringExtra("linkURL");

		boolean zoomControls = intent.getBooleanExtra("zoomControls", true);
		mWebView = new WebView(DocumentWebViewer.this);

		mWebView.setWebChromeClient(mWebChromeClient);
		mWebView.setWebViewClient(mWebViewClient);

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(zoomControls);

		if (isDocument(linkURL))
		{
			mWebView.loadUrl(CtrlType.URL_GOOGLE_DOC_VIEWER + linkURL);
		}
		else
		{
			mWebView.loadUrl(linkURL);
		}

		setContentView(mWebView);
	}
	
	

	private WebViewClient mWebViewClient = new WebViewClient()
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			view.loadUrl(url);
			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
			finish();
		}

	};
	private WebChromeClient mWebChromeClient = new WebChromeClient()
	{
		@Override
		public void onProgressChanged(WebView view, int newProgress)
		{
			DocumentWebViewer.this.getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress * 100);
			super.onProgressChanged(view, newProgress);
		}

	};

	public boolean isDocument(String linkURL)
	{
		if (linkURL.contains(".pdf") || linkURL.contains(".doc") || linkURL.contains(".docx")
				|| linkURL.contains(".pptx") || linkURL.contains(".ppt") || linkURL.contains(".xlsx")
				|| linkURL.contains(".xls"))
		{
			return true;
		}
		return false;
	}

	@Override
	public void finish()
	{
		setIsStartActivity(false);
		
		ResultReceiver receiver =
                getIntent().getParcelableExtra(DocumentWebViewHandler.SELF_CLOSE_RECEIVER);
		
        Bundle resultData = new Bundle();
        
		if (null != (ViewGroup) getWindow().getDecorView())
		{
			((ViewGroup) getWindow().getDecorView()).removeAllViews();
		}
		if (anyError == true)
		{
			resultData.putInt(DocumentWebViewHandler.SELF_CLOSE_MESSAGE, Activity.RESULT_CANCELED);
		}
		else
		{
			resultData.putInt(DocumentWebViewHandler.SELF_CLOSE_MESSAGE, Activity.RESULT_OK);
		}

        receiver.send(DocumentWebViewHandler.SELF_RESULT_CODE, resultData);
		

		super.finish();
	}
	private static synchronized void setIsStartActivity(boolean isStart)
	{
		isStartActivity = isStart;
	}
	public static synchronized boolean getActivityState()
	{
		return isStartActivity;
	}

	class CloseReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{

			if (intent.getAction().equals(DocumentWebViewHandler.ACTION_CLOSE))
			{
				DocumentWebViewer.this.finish();
			}
		}
	}

	@Override
	protected void onDestroy()
	{

		unregisterReceiver(mCloseReceiver);
		super.onDestroy();
	}

}
