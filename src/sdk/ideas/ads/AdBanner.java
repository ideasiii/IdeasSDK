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
import sdk.ideas.tracker.Tracker;

public class AdBanner extends RelativeLayout
{
	private Context mContext = null;
	private AdView mAdView = null;
	private AdListeners mListener = null;
	
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
	public void adPause()
	{
		mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_BANNER,
				"Ad pause");
		Logs.showTrace("Ad pause");
		mAdView.pause();
	}
	
	public void adResume()
	{
		mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_BANNER,
				"Ad resume");
		Logs.showTrace("Ad resume");
		mAdView.resume();
	}
	public void adDestory()
	{
		mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_BANNER,
				"Ad destory");
		Logs.showTrace("Ad destory");
		mAdView.destroy();
	}
	
	public void createAdBanner()
	{
		
		createAdBanner( AdBannerSize.SMART_BANNER, Common.BANNER_AD_UNIT_ID);
	}
	public void setOnAdListener(AdListeners listener)
	{
		if (null != listener)
		{
			mListener = listener;
		}
		
		mAdView.setAdListener(new AdListener() 
		{
            @Override
            public void onAdLoaded() 
            {
            	mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_BANNER,
        				"Ad loaded");
                Logs.showTrace("Ad loaded.");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) 
            {
            	switch (errorCode)
            	{
				case 0:
					Logs.showTrace("error ad cause ERROR_CODE_INTERNAL_ERROR");
					mListener.showAdResult(AdErrorCode.ERROR_CODE_INTERNAL_ERROR, AdErrorCode.AD_BANNER,
							"ERROR_CODE_INTERNAL_ERROR");
					break;
				case 1:
					mListener.showAdResult(AdErrorCode.ERROR_CODE_INVALID_REQUEST, AdErrorCode.AD_BANNER,
							"ERROR_CODE_INVALID_REQUEST");

					break;
				case 2:
					mListener.showAdResult(AdErrorCode.ERROR_CODE_NETWORK_ERROR, AdErrorCode.AD_BANNER,
							"ERROR_CODE_NETWORK_ERROR");

					break;
				case 3:
					mListener.showAdResult(AdErrorCode.ERROR_CODE_NO_FILL, AdErrorCode.AD_BANNER, "ERROR_CODE_NO_FILL");

					break;
				default:
					// mListener.showAdResult(AdErrorCode.ERROR_CODE_INTERNAL_ERROR,
					// AdErrorCode.AD_BANNER, "ERROR_CODE_INTERNAL_ERROR");

					break;
            	}
            }

            @Override
            public void onAdOpened()
            {
            	mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_BANNER,
        				"Ad opened");
            	 Logs.showTrace("Ad opened.");
            }

            @Override
            public void onAdClosed() 
            {
            	mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_BANNER,
        				"Ad closed");
            	 Logs.showTrace("Ad closed.");
            }

            @Override
            public void onAdLeftApplication() 
            {
            	mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_BANNER,
        				"Ad left application.");
            	 Logs.showTrace("Ad left application.");
            }
           
        });
	}
	
	public void createAdBanner( final AdSize  adsize, final String  adID)
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
		    }
		});
		
	}
	
}
	
	
	
	
	
	
	
	

