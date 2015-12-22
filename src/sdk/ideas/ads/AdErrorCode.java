package sdk.ideas.ads;

public abstract class AdErrorCode
{
	public final static int ERROR_CODE_SUCCESS          = 1;
	public final static int ERROR_CODE_UNKNOWED         = 0;
	public final static int ERROR_CODE_INTERNAL_ERROR   = -1;
	public final static int ERROR_CODE_INVALID_REQUEST  = -2;
	public final static int ERROR_CODE_NETWORK_ERROR    = -3;
	public final static int ERROR_CODE_NO_FILL          = -4;
	public final static int ERROR_CODE_AD_STILL_LOADING = -5;

	public final static int AD_BANNER                 		 	 = 0;
	public final static int AD_INTERSTITIAL            			 = 1;
	public final static int AD_INTERSTITIAL_PLAY_STORE_PURCHASE  = 2;
}
