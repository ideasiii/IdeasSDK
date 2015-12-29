package sdk.ideas.ads;



import com.google.android.gms.ads.AdSize;


public abstract class AdBannerSize
{

	public final static AdSize BANNER 			= AdSize.BANNER;
	public final static AdSize LARGE_BANNER 	= AdSize.LARGE_BANNER;
	public final static AdSize MEDIUM_RECTANGLE = AdSize.MEDIUM_RECTANGLE;
	public final static AdSize FULL_BANNER 		= AdSize.FULL_BANNER;
	public final static AdSize LEADERBOARD 		= AdSize.LEADERBOARD;
	public final static AdSize SMART_BANNER 	= AdSize.SMART_BANNER;
	
	public static AdSize calculateAdSize(int width, int height) 
	{
		
		//width *=scaleSize;
		//height*=scaleSize;
        // Use the smallest AdSize that will properly contain the adView
        if (width <= BANNER.getWidth() && height <= BANNER.getHeight()) 
        {
            return BANNER;
        }
        else if(width <= LARGE_BANNER.getWidth() && height <= LARGE_BANNER.getHeight())
        {
        	return LARGE_BANNER;
        }
        else if (width <= MEDIUM_RECTANGLE.getWidth() && height <= MEDIUM_RECTANGLE.getHeight()) 
        {
            return MEDIUM_RECTANGLE;
        } 
        else if (width <= FULL_BANNER.getWidth() && height <= FULL_BANNER.getHeight()) 
        {
            return FULL_BANNER;
        } 
        else if (width <= LEADERBOARD.getWidth() && height <= LEADERBOARD.getHeight()) 
        {
            return LEADERBOARD;
        } 
        else 
        {
        	return null;
        }
    }
	
	
}
