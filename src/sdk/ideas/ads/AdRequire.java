package sdk.ideas.ads;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import android.location.Location;
import sdk.ideas.common.Logs;


public class AdRequire
{
	public static int MALE    = 1;
	public static int FEMALE  = 2;
	public static int UNKNOWN = 3;
	protected Builder mAdBuilder = null;
	
	public AdRequire ()
	{
		mAdBuilder = new AdRequest.Builder();
		//mAdBuilder.addTestDevice("3824F2C0FA4171D11E30E354668FF971");
	}
	public boolean setUserBirthday(int year, int month, int dayOfMonth)
	{
		try
		{
			if(!isValidDate(year, month, dayOfMonth))
			{
				Logs.showTrace("date error");
				return false;
			}
			mAdBuilder.setBirthday(new GregorianCalendar(year, month, dayOfMonth).getTime());
		} 
		catch (Exception e)
		{
			Logs.showTrace(e.toString());
			return false;
		}
		return true;
	}
	public void setUserGender(int sex)
	{
		if(AdRequest.GENDER_FEMALE == sex|| AdRequest.GENDER_MALE ==sex )
			mAdBuilder.setGender(sex);
		else
			mAdBuilder.setGender(AdRequest.GENDER_UNKNOWN);
	}
	
	public void setForChildDirectedTreatment(boolean isChild)
	{
		mAdBuilder.tagForChildDirectedTreatment(isChild);
	} 
	public void setLocation(Location location)
	{
		if(null != location)
		{
			try
			{
				mAdBuilder.setLocation(location);
			}
			catch (Exception e)
			{
				Logs.showTrace(e.toString());
			}
		}
	}
	public boolean setAgent(String agent)
	{
		if(null != agent)
		{
			try
			{
				mAdBuilder.setRequestAgent(agent);
				return true;
			}
			catch (Exception e)
			{
				Logs.showTrace(e.toString());
				return false;
			}
		}
		return false;
	}

	public static boolean isValidDate(int year, int month, int dayOfMonth)
	{
		String date = String.valueOf(year)+"-";
		if(month <= 9)
		{
			date+="0"+String.valueOf(month)+"-";
		}	
		else
		{
			date+=String.valueOf(month)+"-";
		}
		if(dayOfMonth<=9)
		{
			date+="0"+String.valueOf(dayOfMonth);
		}
		else 
		{
			date+=String.valueOf(dayOfMonth);
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.TAIWAN);
		dateFormat.setLenient(false);
		try
		{
			dateFormat.parse(date.trim());
		} catch (ParseException pe)
		{
			return false;
		}
		return true;
	}
	
	
	
	
	
	
	
	

}
