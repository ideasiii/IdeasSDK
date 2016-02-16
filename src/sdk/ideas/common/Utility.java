package sdk.ideas.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

public abstract class Utility
{
	public Utility()
	{

	}

	/**
	 * 
	 * @param android context
	 * @return result code
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int checkingNetwork(Context context)
	{
		int nRet = Type.FALSE;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (null != connectivityManager)
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			{
				Network[] networks = connectivityManager.getAllNetworks();
				NetworkInfo networkInfo;
				for (Network mNetwork : networks)
				{
					networkInfo = connectivityManager.getNetworkInfo(mNetwork);
					if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
					{
						nRet = Type.TRUE;
					}
				}
			}
			else
			{
				NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
				if (null != info)
				{
					for (NetworkInfo anInfo : info)
					{
						if (anInfo.getState() == NetworkInfo.State.CONNECTED)
						{
							nRet = Type.TRUE;
						}
					}
				}
			}
		}
		else
		{
			nRet = Type.INVALID;
		}
		return nRet;
	}

}
