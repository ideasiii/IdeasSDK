package sdk.ideas.ads;

import java.util.Collections;
import java.util.List;


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
import sdk.ideas.common.Common;
import sdk.ideas.common.Logs;
import com.android.vending.billing.*;


public class AdInterstitialPlayStorePurchase extends RelativeLayout implements PlayStorePurchaseListener
{
	public static final int BILLING_RESPONSE_RESULT_OK = 0;

	private InterstitialAd mInterstitialAd;
	private IInAppBillingService mService;
	private AdListeners mListener = null;
	private PlayStorePurchaseListener mPlayStorePurchaseListener = null;
	private Context mContext = null;
	private String mPublicKey = null;
	private int mError = 1;

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

		if (mInterstitialAd.isLoaded())
		{
			mInterstitialAd.show();

			return true;
		} else if (mError < 1)
		{
			mListener.showAdResult(mError, AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "some error to show ads");
			requestNewInterstitial();
			return false;
		} else
		{
			mListener.showAdResult(AdErrorCode.ERROR_CODE_AD_STILL_LOADING,
					AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "interstitial Ad still loading!");
			return false;
		}
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
				mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE,
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
					mError = AdErrorCode.ERROR_CODE_INTERNAL_ERROR;
					mListener.showAdResult(AdErrorCode.ERROR_CODE_INTERNAL_ERROR,
							AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "ERROR_CODE_INTERNAL_ERROR");

					break;
				case 1:
					mError = AdErrorCode.ERROR_CODE_INVALID_REQUEST;
					mListener.showAdResult(AdErrorCode.ERROR_CODE_INVALID_REQUEST,
							AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "ERROR_CODE_INVALID_REQUEST");

					break;
				case 2:
					mError = AdErrorCode.ERROR_CODE_NETWORK_ERROR;
					mListener.showAdResult(AdErrorCode.ERROR_CODE_NETWORK_ERROR,
							AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "ERROR_CODE_NETWORK_ERROR");

					break;
				case 3:
					mError = AdErrorCode.ERROR_CODE_NO_FILL;
					mListener.showAdResult(AdErrorCode.ERROR_CODE_NO_FILL,
							AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "ERROR_CODE_NO_FILL");

					break;
				default:
					mError = -999;
					mListener.showAdResult(errorCode, AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE, "some error");

					break;

				}
			}

			@Override
			public void onAdOpened()
			{
				Logs.showTrace("Ad opened.");
				mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE,
						"Ad open!");
			}

			@Override
			public void onAdLeftApplication()
			{
				mListener.showAdResult(AdErrorCode.ERROR_CODE_SUCCESS, AdErrorCode.AD_INTERSTITIAL_PLAY_STORE_PURCHASE,
						"Ad left application.");
				Logs.showTrace("Ad left application.");
			}

		});

	}

	public void createADInterstitial(final String adID)
	{
		if (mContext == null)
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
		AdRequest adRequest = new AdRequest.Builder().build();

		mInterstitialAd.loadAd(adRequest);
	}

	public void setPurchaseParams(String publicKey)
	{
		mPublicKey = publicKey;
		Logs.showTrace("201");

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
		} catch (RemoteException e)
		{
			Logs.showTrace("Query purchased product failed.");
			return false;
		} catch (NullPointerException e)
		{
			Logs.showTrace(e.toString());
		} catch (Exception e)
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
				 
				 // Optional: your custom process goes here, e.g., add coins after
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

	private List getOwnedProducts() throws RemoteException
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
		
		return Collections.emptyList();
	}

}
