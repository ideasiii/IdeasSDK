package sdk.ideas.ads;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import sdk.ideas.common.Logs;

public class AdBanner extends RelativeLayout
{
	private Context mContext = null;
	private AdView mAdView = null;
	private AdListeners mListener = null;
	private AdRequest mAdRequest   = null;
	
	public AdBanner(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		init();
	}

	public AdBanner(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		init();
	}

	public AdBanner(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		init();
	}
	
	private void init()
	{

	}

	public void adStart()
	{
		try
		{
			if (null != mAdView)
			{
				if (null != mAdRequest)
				{
					mAdView.loadAd(mAdRequest);
				} else
				{
					//run on default
					mAdView.loadAd(new AdRequest.Builder().build());
				}
			}
		}
		catch(NoClassDefFoundError e) 
		{
			if (null != mListener)
			{
				// This can be thrown by Play Services.
				mListener.showAdResult(AdErrorCode.ERROR_CODE_NO_FILL, AdErrorCode.AD_BANNER, "can not create ads");
			}
		}
		
	}
	
	public void adPause()
	{
		if (null != mListener && null != mAdView)
		{
			mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_BANNER, "Ad pause");
			Logs.showTrace("Ad pause");
			mAdView.pause();
		}
	}
	public void adResume()
	{
		if (null != mListener && null != mAdView)
		{
			mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_BANNER, "Ad resume");
			Logs.showTrace("Ad resume");
			mAdView.resume();
		}
	}
	public void adDestory()
	{
		if (null != mListener && null != mAdView)
		{
			mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_BANNER, "Ad destory");
			Logs.showTrace("Ad destory");
			mAdView.destroy();
		}
	}
	
	public void setAdRequire(AdRequire adrequire)
	{
		if(null != adrequire)
		{
			this.mAdRequest = adrequire.mAdBuilder.build();
		}
	}
	
	
	public void setOnAdListener(AdListeners listener)
	{
		if (null != listener)
		{
			mListener = listener;
		}
		else
		{
			return;
		}
		
		if(null != mAdView )
		{
			mAdView.setAdListener(new AdListener() 
			{
	            @Override
	            public void onAdLoaded() 
	            {
	            	mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_BANNER,
	        				"Ad loaded");
	            }
	           
	            @Override
	            public void onAdFailedToLoad(int errorCode) 
	            {
	            	
	            	switch (errorCode)
	            	{
	            		
					case AdRequest.ERROR_CODE_INTERNAL_ERROR:

						mListener.showAdResult(AdErrorCode.ERROR_CODE_INTERNAL_ERROR, AdErrorCode.AD_BANNER,
								"Something happened internally; for instance, an invalid response was received from the ad server.");
						break;
					case AdRequest.ERROR_CODE_INVALID_REQUEST:
						mListener.showAdResult(AdErrorCode.ERROR_CODE_INVALID_REQUEST, AdErrorCode.AD_BANNER,
								"The ad request was invalid; for instance, the ad unit ID was incorrect.");

						break;
					case AdRequest.ERROR_CODE_NETWORK_ERROR:
						mListener.showAdResult(AdErrorCode.ERROR_CODE_NETWORK_ERROR, AdErrorCode.AD_BANNER,
								" The ad request was unsuccessful due to network connectivity.");

						break;
					case AdRequest.ERROR_CODE_NO_FILL:
						mListener.showAdResult(AdErrorCode.ERROR_CODE_NO_FILL, AdErrorCode.AD_BANNER,
								"The ad request was successful, "
										+ "but no ad was returned due to lack of ad inventory.onAdOpened");

						break;
					default:
						mListener.showAdResult(errorCode, AdErrorCode.AD_BANNER, "unknown error");

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
	            }});
		}
		else
		{
			mListener.showAdResult(AdErrorCode.ERROR_CODE_AD_UNCREATED, AdErrorCode.AD_BANNER,
					"ads not created caused by ad size, ad uid set fail or null");
		}
	}
	
	
	public void createAdBanner(final String adID,final int width,final int height)
	{
		createAdBanner( adID, AdBannerSize.calculateAdSize( width, height));
		
	}
	public void createAdBanner(final String adID)
	{
		createAdBanner( adID, AdBannerSize.SMART_BANNER);
	}
		
	public void createAdBanner(final String adID,final AdSize adSize)
	{
		if(null == adSize || null == adID)
			return;
		
		((Activity) mContext).runOnUiThread(new Runnable() 
		{
		    @Override
		    public void run()
		    {
		        //Code for the UiThread
				mAdView = new AdView(mContext);
				mAdView.setAdSize(adSize);
				mAdView.setAdUnitId(adID);
				
				AdBanner.this.addView(mAdView);
		    }
		});
	}
	
}
	
	
	
	
	
	
	
	

