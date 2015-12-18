package sdk.ideas.ads;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import sdk.ideas.common.Common;
import sdk.ideas.common.Logs;

public class AdInterstitial extends RelativeLayout
{
	private Context mContext = null;
	private InterstitialAd mInterstitialAd = null;
	public AdInterstitial(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		
	}
	
	public boolean InterstitialAdShow()
	{
		 if (mInterstitialAd.isLoaded())
		 {
			 mInterstitialAd.show();
			 return true;
          }
		 else
		 {
			 return false;
		 }
		
	}
	
	public AdInterstitial(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;

	}
	
	public InterstitialAd getMInterstitialAd()
	{
		return mInterstitialAd;
	}

	public AdInterstitial(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;

	}
	public void CreateADInterstitial(final String adID)
	{
		((Activity) mContext).runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// Code for the UiThread
				mInterstitialAd = new InterstitialAd(mContext);
				if (adID != null)
				{
					mInterstitialAd.setAdUnitId(adID);
				} else
				{
					mInterstitialAd.setAdUnitId(Common.INTERSTITIAL_AD_UNIT_ID);
				}
				requestNewInterstitial();

				mInterstitialAd.setAdListener(new AdListener()
				{
					@Override
					public void onAdClosed()
					{
						requestNewInterstitial();
						Logs.showTrace("Ad closed.");
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
					public void onAdLeftApplication()
					{
						Logs.showTrace("Ad left application.");
					}

				});
			}
		});
		   
        
		
	}
	
	 private void requestNewInterstitial() 
	 {
	        AdRequest adRequest = new AdRequest.Builder()
	        		.build();
	      
	        mInterstitialAd.loadAd(adRequest);
	 }

	
	
	
}
