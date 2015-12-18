package sdk.ideas.ads;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import sdk.ideas.common.Common;
import sdk.ideas.common.Logs;

public class AdBanner extends RelativeLayout
{
	private Context mContext = null;
	private AdView mAdView = null;
	
	public AdBanner(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	
	}

	public AdBanner(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;

	}

	public AdBanner(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;

	}
	
	
	public void CreateAdBanner( final AdSize  adsize, final String  adID)
	{
		//Thread banner = new Thread(new AdBannerRunnable(adsize,adID));
		//banner.start();
		
		((Activity) mContext).runOnUiThread(new Runnable() {
		    @Override
		    public void run() {
		        //Code for the UiThread
		    	
				 mAdView = new AdView(mContext);
				
				mAdView.setAdSize(adsize);
				if(adID ==null)
				{
					mAdView.setAdUnitId(Common.BANNER_AD_UNIT_ID);
				} else
				{
					mAdView.setAdUnitId(adID);
				}
				addView(mAdView);	
				mAdView.loadAd(new AdRequest.Builder()
						.build());
				
				mAdView.setAdListener(new AdListener() 
				{
		            @Override
		            public void onAdLoaded() 
		            {
		                Logs.showTrace("Ad loaded.");
		            }

		            @Override
		            public void onAdFailedToLoad(int errorCode) 
		            {
		            	switch (errorCode)
		            	{
		            	case AdErrorCode.ERROR_CODE_INTERNAL_ERROR:
		            		Logs.showTrace("error ad cause ERROR_CODE_INTERNAL_ERROR");
		            		break;
		            	case AdErrorCode.ERROR_CODE_INVALID_REQUEST:
		            		Logs.showTrace("error ad cause ERROR_CODE_INVALID_REQUEST");
		            		break;
		            	case AdErrorCode.ERROR_CODE_NETWORK_ERROR:
		            		Logs.showTrace("error ad cause ERROR_CODE_NETWORK_ERROR");
		            		break;
		            	case AdErrorCode.ERROR_CODE_NO_FILL:
		            	Logs.showTrace("error ad cause ERROR_CODE_NO_FILL");
		            		break;
		            	default:
		            	Logs.showTrace("error ad cause Unknown");
		            		break;
		            	}
		            }

		            @Override
		            public void onAdOpened()
		            {
		            	 Logs.showTrace("Ad opened.");
		            }

		            @Override
		            public void onAdClosed() 
		            {
		            	 Logs.showTrace("Ad closed.");
		            }

		            @Override
		            public void onAdLeftApplication() 
		            {
		            	 Logs.showTrace("Ad left application.");
		            }
		           
		        });
		    }
		});
		
	}
	
	class AdBannerRunnable implements Runnable
	{
		
		private AdSize adsize;
		private String adID;
		
		public AdBannerRunnable(AdSize adsize, String id)
		{
			this.adsize = adsize;
			this.adID = id;
			
		}
		@Override
		public void run()
		{
			// TODO Auto-generated method stub
			 mAdView = new AdView(mContext);
			
			mAdView.setAdSize(adsize);
			if(adID ==null)
			{
				mAdView.setAdUnitId(Common.BANNER_AD_UNIT_ID);
			} else
			{
				mAdView.setAdUnitId(adID);
			}
			addView(mAdView);	
			mAdView.loadAd(new AdRequest.Builder()
					.build());
			
			mAdView.setAdListener(new AdListener() 
			{
	            @Override
	            public void onAdLoaded() 
	            {
	                Logs.showTrace("Ad loaded.");
	            }

	            @Override
	            public void onAdFailedToLoad(int errorCode) 
	            {
	            	switch (errorCode)
	            	{
	            	case AdErrorCode.ERROR_CODE_INTERNAL_ERROR:
	            		Logs.showTrace("error ad cause ERROR_CODE_INTERNAL_ERROR");
	            		break;
	            	case AdErrorCode.ERROR_CODE_INVALID_REQUEST:
	            		Logs.showTrace("error ad cause ERROR_CODE_INVALID_REQUEST");
	            		break;
	            	case AdErrorCode.ERROR_CODE_NETWORK_ERROR:
	            		Logs.showTrace("error ad cause ERROR_CODE_NETWORK_ERROR");
	            		break;
	            	case AdErrorCode.ERROR_CODE_NO_FILL:
	            	Logs.showTrace("error ad cause ERROR_CODE_NO_FILL");
	            		break;
	            	default:
	            	Logs.showTrace("error ad cause Unknown");
	            		break;
	            	}
	            }

	            @Override
	            public void onAdOpened()
	            {
	            	 Logs.showTrace("Ad opened.");
	            }

	            @Override
	            public void onAdClosed() 
	            {
	            	 Logs.showTrace("Ad closed.");
	            }

	            @Override
	            public void onAdLeftApplication() 
	            {
	            	 Logs.showTrace("Ad left application.");
	            }
	           
	        });
		} 
		
		
	}
	
	
	
}
	
	
	
	
	
	
	
	

