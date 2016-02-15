/**
 * @author Louis Ju
 * @since 2016-02-15
 * @permission: <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 * @permission: <uses-permission android:name="android.permission.INTERNET"/>
 */
package sdk.ideas.module;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import sdk.ideas.common.Logs;

public class HttpClient
{
	public static int	ERR_FAIL							= 0;
	public static int	ERR_SUCCESS							= 1;
	public static int	ERR_INVALID_CONNECTIVITY_SERVICE	= -1;

	private String								mstrNetworkName			= null;
	private SparseArray<HttpResponseListener>	listResponseListener	= null;
	private int									mnReadTimeout			= 5000;
	private int									mnConnectTimeout		= 10000;

	enum HTTP_METHOD
	{
		POST, GET
	}

	public static interface HttpResponseListener
	{
		public void response(final int nId, final int nCode, final String strContent);
	}

	public void setOnHttpResponseListener(HttpResponseListener listener)
	{
		if (null != listener)
		{
			listResponseListener.append(listResponseListener.size(), listener);
		}
	}

	private class HttpResponse
	{
		public int		nCode;
		public String	strContent;
	}

	public HttpClient()
	{
		listResponseListener = new SparseArray<HttpResponseListener>();
	}

	@Override
	protected void finalize() throws Throwable
	{
		listResponseListener.clear();
		listResponseListener = null;
		super.finalize();
	}

	/**
	 * 
	 * @param android context
	 * @return result code
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public int checkingNetwork(Context context)
	{
		int nRet = ERR_FAIL;
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
						mstrNetworkName = networkInfo.getTypeName();
						return ERR_SUCCESS;
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
							mstrNetworkName = anInfo.getTypeName();
							return ERR_SUCCESS;
						}
					}
				}
			}
		}
		else
		{
			return ERR_INVALID_CONNECTIVITY_SERVICE;
		}
		return nRet;
	}

	public String getNetworkName()
	{
		return mstrNetworkName;
	}

	private void callback(final int nId, final int nCode, final String strContent)
	{
		for (int i = 0; i < listResponseListener.size(); ++i)
		{
			listResponseListener.get(i).response(nId, nCode, strContent);
		}
	}

	/**
	 * @param nId: for sync callback function
	 * @param targetURL
	 * @param urlParameters
	 * @return HTTP response
	 */
	public String httpPost(final int nId, final String strTargetURL, final HashMap<String, String> mapParameters)
	{
		String strResponse = null;
		if (null == strTargetURL)
		{
			postMsg(0, nId, -1, "Invalid Target URL");
		}
		else
		{
			try
			{
				HttpThread httpThread = new HttpThread(nId, HTTP_METHOD.POST, strTargetURL, mapParameters);
				httpThread.start();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return strResponse;
	}

	private class HttpThread extends Thread
	{
		private HTTP_METHOD	method;
		private String		mstrTargetURL	= null;
		private String		mstrParameters	= null;
		private int			mnId			= -1;

		@Override
		public void run()
		{
			HttpResponse httpResponse = new HttpResponse();
			switch(method)
			{
			case POST:
				Logs.showTrace("HTTP POST:" + mstrTargetURL + " PARAMETERS:" + mstrParameters);
				runPOST(mstrTargetURL, mstrParameters, httpResponse);
				postMsg(0, mnId, httpResponse.nCode, httpResponse.strContent);
				break;
			case GET:
				break;
			}
			super.run();

			Logs.showTrace("HTTP Response:" + httpResponse.strContent);
		}

		public HttpThread(final int nId, final HTTP_METHOD enuMethod, final String strTargetURL,
				final HashMap<String, String> mapParameters) throws UnsupportedEncodingException
		{
			mnId = nId;
			method = enuMethod;
			mstrTargetURL = strTargetURL;
			boolean bFirst = true;
			String strKey = null;
			String strValue = null;
			for (Object key : mapParameters.keySet())
			{
				strKey = ((String) key) + "=";
				strValue = mapParameters.get(key);

				if (bFirst)
				{
					bFirst = false;
					mstrParameters = strKey + URLEncoder.encode(strValue, "UTF-8");
				}
				else
				{
					mstrParameters = mstrParameters + "&" + strKey + URLEncoder.encode(strValue, "UTF-8");
				}
			}
		}
	}

	private int runPOST(final String strTargetURL, final String strParameters, HttpResponse httpResponse)
	{
		URL url;
		HttpURLConnection connection = null;
		httpResponse.nCode = -1;

		try
		{
			url = new URL(strTargetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setReadTimeout(mnReadTimeout);
			connection.setConnectTimeout(mnConnectTimeout);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + Integer.toString(strParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "UTF-8");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(strParameters);
			wr.flush();
			wr.close();

			httpResponse.nCode = connection.getResponseCode();
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null)
			{
				response.append(line);
				response.append('\r');
			}
			rd.close();

			httpResponse.strContent = response.toString();
		}
		catch (Exception e)
		{
			httpResponse.strContent = e.getMessage();
			e.printStackTrace();
			Logs.showError("HTTP POST Exception:" + e.toString());
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}

		return httpResponse.nCode;

	}

	protected void postMsg(int nWhat, int nArg1, int nArg2, Object obj)
	{
		if (null != theHandler)
		{
			Thread t = new Thread(new postMsgRunnable(nWhat, nArg1, nArg2, obj));
			t.start();
		}
	}

	class postMsgRunnable implements Runnable
	{
		private Message message = null;

		@Override
		public void run()
		{
			if (null == message)
				return;
			theHandler.sendMessage(message);
		}

		public postMsgRunnable(int nWhat, int nArg1, int nArg2, Object obj)
		{
			message = new Message();
			message.what = nWhat;
			message.arg1 = nArg1;
			message.arg2 = nArg2;
			message.obj = obj;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler theHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			callback(msg.arg1, msg.arg2, (String) msg.obj);
		}

	};

	public void setReadTimeout(final int nMilliSecond)
	{
		mnReadTimeout = nMilliSecond;
	}

	public void setConnectTimeout(final int nMilliSecond)
	{
		mnConnectTimeout = nMilliSecond;
	}

}
