package sdk.ideas.mdm.content;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;
import sdk.ideas.mdm.MDMType;

public class DocumentWebViewer extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String linkURL = intent.getStringExtra("linkURL");

		boolean zoomControls = intent.getBooleanExtra("zoomControls", false);
		WebView mWebView = new WebView(DocumentWebViewer.this);

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(zoomControls);

		mWebView.loadUrl(MDMType.URL_DocumentWebViewer + linkURL);
		setContentView(mWebView);
	}

	@Override
	public void finish()
	{
		ViewGroup view = (ViewGroup) getWindow().getDecorView();
		view.removeAllViews();
		super.finish();
	}
}
