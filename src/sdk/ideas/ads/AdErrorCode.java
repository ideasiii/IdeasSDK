package sdk.ideas.ads;

public abstract class AdErrorCode
{	
	public final static int  ERROR_SUM						=-1000;
	
	public final static int ERROR_CODE_SUCCESS 				= 1 ;
	public final static int ERROR_CODE_UNKNOWED 			= 0 + ERROR_SUM;
	public final static int ERROR_CODE_INTERNAL_ERROR 		= -1 + ERROR_SUM;
	public final static int ERROR_CODE_INVALID_REQUEST 		= -2 + ERROR_SUM;
	public final static int ERROR_CODE_NETWORK_ERROR 		= -3 + ERROR_SUM;
	public final static int ERROR_CODE_NO_FILL 				= -4 + ERROR_SUM;
	public final static int ERROR_CODE_AD_STILL_LOADING 	= -5 + ERROR_SUM;
	public final static int ERROR_CODE_AD_UNCREATED 		= -6 + ERROR_SUM;
	public final static int ERROR_MAX 						= -7 + ERROR_SUM;

	public final static int AD_BANNER                 		 	 = 0;
	public final static int AD_INTERSTITIAL            			 = 1;
	public final static int AD_INTERSTITIAL_PLAY_STORE_PURCHASE  = 2;
}
