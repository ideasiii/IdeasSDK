package sdk.ideas.ads;

import java.util.ArrayList;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.purchase.InAppPurchaseResult;
import com.google.android.gms.ads.purchase.PlayStorePurchaseListener;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import sdk.ideas.common.Logs;
import com.android.vending.billing.*;

public class AdInterstitialPlayStorePurchase extends RelativeLayout implements PlayStorePurchaseListener
{
	public static final int BILLING_RESPONSE_RESULT_OK = 0;

	private InterstitialAd				mInterstitialAd;
	private IInAppBillingService		mService;
	private AdListeners					mListener					= null;
	private PlayStorePurchaseListener	mPlayStorePurchaseListener	= null;
	private Context						mContext					= null;
	private String						mPublicKey					= null;
	private AdRequest					mAdRequest					= null;
	private int							mError						= 1;

	public AdInterstitialPlayStorePurchase(Context context)
	{
		super(context);
		this.mContext = context;
		mPlayStorePurchaseListener = this;
		// TODO Auto-generated constructor stub
	}

	public AdInterstitialPlayStorePurchase(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		mPlayStorePurchaseListener = this;

	}

	public AdInterstitialPlayStorePurchase(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		mPlayStorePurchaseListener = this;

	}

	public boolean interstitialAdShow()
	{
		if (null != mInterstitialAd && null != mListener)
		{
			if (mError < 1)
			{
				mListener.showAdResult(mError, AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE,
						"some error to show ads");
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
				mListener.showAdResult(AdErrorCode.ERROR_CODE_AD_STILL_LOADING,
						AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "interstitial Ad still loading!");
				return false;
			}
		}
		else
		{
			if (null == mInterstitialAd && null != mListener)
			{
				mListener.showAdResult(AdErrorCode.ERROR_CODE_AD_UNCREATED,
						AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE,
						"purchase interstitial Ad not created caused by uid is null!");
			}
			return false;
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

		if (null != mInterstitialAd)
		{
			mInterstitialAd.setAdListener(new AdListener()
			{
				@Override
				public void onAdClosed()
				{
					requestNewInterstitial();
					mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS,
							AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "Ad close successful");
					Logs.showTrace("Ad closed.");
				}

				@Override
				public void onAdFailedToLoad(int errorCode)
				{

					switch(errorCode)
					{
					case AdRequest.ERROR_CODE_INTERNAL_ERROR:
						mError = AdErrorCode.ERROR_CODE_INTERNAL_ERROR;
						mListener.showAdResult(AdErrorCode.ERROR_CODE_INTERNAL_ERROR,
								AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE,
								"Something happened internally; for instance, an invalid response was received from the ad server.");

						break;
					case AdRequest.ERROR_CODE_INVALID_REQUEST:
						mError = AdErrorCode.ERROR_CODE_INVALID_REQUEST;
						mListener.showAdResult(AdErrorCode.ERROR_CODE_INVALID_REQUEST,
								AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE,
								"The ad request was invalid; for instance, the ad unit ID was incorrect.");

						break;
					case AdRequest.ERROR_CODE_NETWORK_ERROR:
						mError = AdErrorCode.ERROR_CODE_NETWORK_ERROR;
						mListener.showAdResult(AdErrorCode.ERROR_CODE_NETWORK_ERROR,
								AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE,
								"The ad request was unsuccessful due to network connectivity.");

						break;
					case AdRequest.ERROR_CODE_NO_FILL:
						mError = AdErrorCode.ERROR_CODE_NO_FILL;
						mListener.showAdResult(AdErrorCode.ERROR_CODE_NO_FILL,
								AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE,
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
					mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS,
							AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "Ad open!");
				}

				@Override
				public void onAdLeftApplication()
				{
					mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS,
							AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "Ad left application.");
					Logs.showTrace("Ad left application.");
				}

			});
		}
		else
		{
			mError = AdErrorCode.ERROR_CODE_AD_UNCREATED;
			mListener.showAdResult(AdErrorCode.ERROR_CODE_AD_UNCREATED, AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE,
					"purchase interstitial Ad not created caused by uid is null");

		}

	}

	public void createADInterstitialPlayStorePurchase(final String adID)
	{
		if (null == adID)
		{
			return;
		}
		((Activity) mContext).runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				// Code for the UiThread
				mInterstitialAd = new InterstitialAd(mContext);
				mInterstitialAd.setPlayStorePurchaseParams(mPlayStorePurchaseListener, mPublicKey);
				mInterstitialAd.setAdUnitId(adID);

				requestNewInterstitial();
			}

		});

	}

	private void requestNewInterstitial()
	{
		if (null != mInterstitialAd)
		{
			if (null != this.mAdRequest)
			{
				mInterstitialAd.loadAd(mAdRequest);
			}
			else
			{
				mInterstitialAd.loadAd(new AdRequest.Builder().build());
			}
		}
		else
		{
			mListener.showAdResult(AdErrorCode.ERROR_CODE_AD_UNCREATED, AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE,
					"purchase interstitial Ad not created ");
		}
	}

	public void setPurchaseParams(String publicKey)
	{
		mPublicKey = publicKey;
	}

	@Override
	public boolean isValidPurchase(String sku)
	{
		// Optional: check if the product has already been purchased.
		try
		{
			if (null != getOwnedProducts() && getOwnedProducts().contains(sku))
			{
				// Handle the case if product is already purchased.
				return false;
			}
		}
		catch (RemoteException e)
		{
			Logs.showTrace("Query purchased product failed.");
			return false;
		}
		catch (NullPointerException e)
		{
			Logs.showTrace(e.toString());
		}
		catch (Exception e)
		{
			Logs.showTrace(e.getMessage());
		}
		return true;
	}

	@Override
	public void onInAppPurchaseFinished(InAppPurchaseResult result)
	{

		Logs.showTrace("onInAppPurchaseFinished Start");
		int resultCode = result.getResultCode();
		Logs.showTrace("result code: " + resultCode);
		String sku = result.getProductId();
		if (resultCode == Activity.RESULT_OK)
		{
			Logs.showTrace("purchased product id: " + sku);
			int responseCode = result.getPurchaseData().getIntExtra("RESPONSE_CODE", BILLING_RESPONSE_RESULT_OK);
			String purchaseData = result.getPurchaseData().getStringExtra("INAPP_PURCHASE_DATA");
			Logs.showTrace("response code: " + responseCode);
			Logs.showTrace("purchase data: " + purchaseData);

			// Finish purchase and consume product.
			result.finishPurchase();
			if (responseCode == BILLING_RESPONSE_RESULT_OK)
			{

				// Optional: your custom process goes here, e.g., add coins
				// after
				// purchase.
				Logs.showTrace("success to purchase product: " + sku);
			}
		}
		else
		{
			Logs.showTrace("Failed to purchase product: " + sku);
		}
		Logs.showTrace("onInAppPurchaseFinished End");

	}

	private ArrayList<String> getOwnedProducts() throws RemoteException
	{
		// Query for purchased items.
		// See
		// http://developer.android.com/google/play/billing/billing_reference.html
		// and
		// http://developer.android.com/google/play/billing/billing_integrate.html

		Bundle ownedItems = mService.getPurchases(3, mContext.getPackageName(), "inapp", null);

		int response = ownedItems.getInt("RESPONSE_CODE");
		Logs.showTrace("Response code of purchased item query");
		if (response == 0)
		{
			return ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
		}
		ArrayList<String> emptyList = new ArrayList<String>();

		return emptyList;
	}

}
