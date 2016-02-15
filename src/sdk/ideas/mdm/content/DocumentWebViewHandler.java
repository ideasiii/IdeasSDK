package sdk.ideas.mdm.content;

import android.content.Context;
import android.content.Intent;

public class DocumentWebViewHandler
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

		if (null != mContext && null != uRL)
		{
			
			intent.putExtra("linkURL", uRL);
			mContext.startActivity(intent);
		}
	}
	
	public void setZoomControls(boolean zoomControls)
	{
		if (null != intent)
		{
			intent.putExtra("zoomControls", zoomControls);
		}
	}

}
