package sdk.ideas.ctrl.content;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		intent = getIntent();
		String linkURL = intent.getStringExtra("linkURL");
		
		boolean zoomControls = intent.getBooleanExtra("zoomControls", false);
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
		if(null!=(ViewGroup) getWindow().getDecorView())
		{
			((ViewGroup) getWindow().getDecorView()).removeAllViews();
		}
		if(anyError == true)
			setResult(Activity.RESULT_CANCELED, intent);
		else
			setResult(Activity.RESULT_OK, intent);
		super.finish();
	}

}
