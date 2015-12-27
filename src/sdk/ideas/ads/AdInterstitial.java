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
	private AdRequest mAdRequest = null;
	private int mError = AdErrorCode.ERROR_CODE_SUCCESS;
	
	public AdInterstitial(Context context)
	{
		super(context);
		mContext = context;
	}
	public AdInterstitial(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
	}
	
	public AdInterstitial(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mContext = context;
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
		if(null!= mInterstitialAd)
		{
			mInterstitialAd.setAdListener(new AdListener()
			{
				@Override
				public void onAdClosed()
				{
					requestNewInterstitial();
					mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_INTERSTITIAL,
							"Ad close successful");
					
				}
	
				@Override
				public void onAdFailedToLoad(int errorCode)
				{
					switch (errorCode)
					{
					case AdRequest.ERROR_CODE_INTERNAL_ERROR:
						mError = AdErrorCode.ERROR_CODE_INTERNAL_ERROR;
						Logs.showTrace("error ad cause ERROR_CODE_INTERNAL_ERROR");
						mListener.showAdResult(AdErrorCode.ERROR_CODE_INTERNAL_ERROR, AdErrorCode.AD_INTERSTITIAL,
								"Something happened internally; for instance, an invalid response was received from the ad server.");
						break;
					case AdRequest.ERROR_CODE_INVALID_REQUEST:
						mError = AdErrorCode.ERROR_CODE_INVALID_REQUEST;
						mListener.showAdResult(AdErrorCode.ERROR_CODE_INVALID_REQUEST, AdErrorCode.AD_INTERSTITIAL,
								"The ad request was invalid; for instance, the ad unit ID was incorrect.");

						break;
					case AdRequest.ERROR_CODE_NETWORK_ERROR:
						mError = AdErrorCode.ERROR_CODE_NETWORK_ERROR;
						mListener.showAdResult(AdErrorCode.ERROR_CODE_NETWORK_ERROR, AdErrorCode.AD_INTERSTITIAL,
								" The ad request was unsuccessful due to network connectivity.");

						break;
					case AdRequest.ERROR_CODE_NO_FILL:
						mError = AdErrorCode.ERROR_CODE_NO_FILL;
						mListener.showAdResult(AdErrorCode.ERROR_CODE_NO_FILL, AdErrorCode.AD_INTERSTITIAL,
								"The ad request was successful, but no ad was returned due to lack of ad inventory.");

						break;
					default:
						mError = AdErrorCode.ERROR_CODE_UNKNOWED;
						mListener.showAdResult(mError, AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "some error");

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
		else
		{
			mError = AdErrorCode.ERROR_CODE_AD_UNCREATED;
			mListener.showAdResult(AdErrorCode.ERROR_CODE_AD_UNCREATED, AdErrorCode.AD_INTERSTITIAL,
					"interstitial Ad not created caused by uid is null");
		}
		
	}
	
	public void setAdRequire(AdRequire adRequire)
	{
		if(null!= adRequire)
		{
			this.mAdRequest = adRequire.mAdBuilder.build();
		}
	}
	
	public boolean interstitialAdShow()
	{
		if (null != mListener && null != mInterstitialAd)
		{
			if (mError != AdErrorCode.ERROR_CODE_SUCCESS)
			{
				mListener.showAdResult(mError, AdErrorCode.AD_INTERSTITIAL, "some error to show ads");
				requestNewInterstitial();
				
				return false;
			}
			else if (mInterstitialAd.isLoaded())
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
		else
		{
			if (null == mInterstitialAd && null != mListener)
			{
				mListener.showAdResult(AdErrorCode.ERROR_CODE_AD_UNCREATED, AdErrorCode.AD_INTERSTITIAL,
						"interstitial Ad not created caused by uid is null");
			}
			return false;
		}
	}
	
	public void createADInterstitial(final String adID)
	{
		if (null != adID)
		{
			((Activity) mContext).runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					mInterstitialAd = new InterstitialAd(mContext);
					mInterstitialAd.setAdUnitId(adID);
					requestNewInterstitial();
				}
			});
		}
		
	}
	
	 private void requestNewInterstitial() 
	 {
		   try
		   {
			   if (null != mInterstitialAd)
				{
					if (null != this.mAdRequest)
					{
						mInterstitialAd.loadAd(mAdRequest);
					} else
					{
						//run on defalut
						mInterstitialAd.loadAd(new AdRequest.Builder().build());
					}
				}
	        } 
		   catch (NoClassDefFoundError e) 
		   {
	            // This can be thrown by Play Services on Honeycomb.
	         Logs.showTrace(e.toString());
	        }
		
	 }

	
	
	
}
