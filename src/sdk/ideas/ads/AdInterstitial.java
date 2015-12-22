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
	private AdListeners mListener = null;
	public AdInterstitial(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		
	}
	
	public boolean interstitialAdShow()
	{
		 if (mInterstitialAd.isLoaded())
		 {
			 mInterstitialAd.show();
			
			 return true;
          }
		 else
		 {
			 mListener.showAdResult(AdErrorCode.ERROR_CODE_AD_STILL_LOADING, AdErrorCode.AD_INTERSTITIAL,
						"interstitial Ad still loading!");
			 return false;
		 }
		
	}
	
	public AdInterstitial(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;

	}
	
	public void setOnAdListener(AdListeners listener)
	{
		if (null != listener)
		{
			mListener = listener;
		}
		

		mInterstitialAd.setAdListener(new AdListener()
		{
			@Override
			public void onAdClosed()
			{
				requestNewInterstitial();
				mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_INTERSTITIAL,
						"Ad close successful");
				Logs.showTrace("Ad closed.");
			}

			@Override
			public void onAdFailedToLoad(int errorCode)
			{
				switch (errorCode)
				{
				case 0:
					Logs.showTrace("error ad cause ERROR_CODE_INTERNAL_ERROR");
					mListener.showAdResult(AdErrorCode.ERROR_CODE_INTERNAL_ERROR, AdErrorCode.AD_INTERSTITIAL,
							"ERROR_CODE_INTERNAL_ERROR");
					break;
				case 1:
					mListener.showAdResult(AdErrorCode.ERROR_CODE_INVALID_REQUEST, AdErrorCode.AD_INTERSTITIAL,
							"ERROR_CODE_INVALID_REQUEST");

					break;
				case 2:
					mListener.showAdResult(AdErrorCode.ERROR_CODE_NETWORK_ERROR, AdErrorCode.AD_INTERSTITIAL,
							"ERROR_CODE_NETWORK_ERROR");

					break;
				case 3:
					mListener.showAdResult(AdErrorCode.ERROR_CODE_NO_FILL, AdErrorCode.AD_INTERSTITIAL,
							"ERROR_CODE_NO_FILL");

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
				Logs.showTrace("Ad opened.");
				 mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_INTERSTITIAL,
							"Ad open!");
			}

			@Override
			public void onAdLeftApplication()
			{
				 mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_INTERSTITIAL,
							"Ad left application.");
				Logs.showTrace("Ad left application.");
			}

		});
		
		
	}

	public AdInterstitial(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;

	}
	public void createADInterstitial()
	{
		createADInterstitial(Common.INTERSTITIAL_AD_UNIT_ID);
		
	}
	public void createADInterstitial(final String adID)
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
