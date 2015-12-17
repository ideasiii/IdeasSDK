package sdk.ideas.module;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Build;
import sdk.ideas.common.Logs;
import sdk.ideas.common.StringUtility;
import sdk.ideas.tracker.Tracker.TransferMessage;

public class HttpClient
{

	private final static int	TIMEOUT_CONNECT	= 3000;
	private final static int	TIMEOUT_SOCKET	= 3000;

	public static class Response
	{
		public int		mnCode		= 0;
		public String	mstrContent	= null;
	}

	public HttpClient()
	{
		super();
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
	}

	public static int sendPostData(final String strURL, Map<String, String> parm, Response response,
			TransferMessage transferMessage)
	{
		if (!StringUtility.isValid(strURL) || null == response)
		{
			response.mnCode = -1;
			response.mstrContent = "Invalid Function Paramters";
			return -1;
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO)
		{
			System.setProperty("http.keepAlive", "false");
		}

		response.mnCode = -1;
		try
		{
			// 建立HTTP Post連線
			HttpPost httpRequest = new HttpPost(strURL);

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECT);
			HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);

			// Post運作傳送變數必須用NameValuePair[]陣列儲存
			if (null != parm && parm.size() > 0)
			{
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				for (Entry<String, String> item : parm.entrySet())
				{
					params.add(new BasicNameValuePair(item.getKey(), item.getValue()));
				}
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
				httpRequest.setEntity(ent);
			}

			// 發出HTTP request
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse httpResponse = httpClient.execute(httpRequest);

			response.mnCode = httpResponse.getStatusLine().getStatusCode();
			// 若狀態碼為200 ok
			if (HttpURLConnection.HTTP_OK == response.mnCode)
			{
				// 取出回應字串 */
				response.mstrContent = EntityUtils.toString(httpResponse.getEntity());

			}
		}
		catch (IllegalArgumentException e)
		{
			// Logs.showTrace("HttpPost Exception:" + e.getMessage());
			response.mstrContent = "Invalid URL:" + strURL;
		}
		catch (UnsupportedEncodingException e)
		{
			// Logs.showTrace("UrlEncodedFormEntity Exception:" +
			// e.getMessage());
			response.mstrContent = "Unsupported Encoding Exception:" + e.getMessage();
		}
		catch (ClientProtocolException e)
		{
			// Logs.showTrace("DefaultHttpClient Exception:" + e.getMessage());
			response.mstrContent = "Client Protocol Exception:" + e.getMessage();
		}
		catch (IOException e)
		{
			// Logs.showTrace("DefaultHttpClient Exception:" + e.getMessage());
			response.mstrContent = "IO Exception:" + e.getMessage();
		}

		return response.mnCode;
	}

	public static int sendGetData(final String strURL, Map<String, String> parm, Response response)
	{
		if (!StringUtility.isValid(strURL) || null == response)
		{
			response.mnCode = -1;
			response.mstrContent = "Invalid Function Paramters";
			return -1;
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO)
		{
			System.setProperty("http.keepAlive", "false");
		}

		response.mnCode = -1;

		try
		{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECT);
			HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

			String strURLStr = strURL;
			if (null != parm && parm.size() > 0)
			{
				String strParam = "?";
				for (Entry<String, String> item : parm.entrySet())
				{
					strParam = strParam + item.getKey() + "=" + item.getValue();
				}
				strURLStr += strParam;
			}
			Logs.showTrace("Http Request:" + strURLStr);

			HttpGet httpGet = new HttpGet(strURLStr);
			HttpResponse httpResponse = httpClient.execute(httpGet);

			response.mnCode = httpResponse.getStatusLine().getStatusCode();
			// 若狀態碼為200 ok
			if (HttpURLConnection.HTTP_OK == response.mnCode)
			{
				// 取出回應字串 */
				response.mstrContent = EntityUtils.toString(httpResponse.getEntity());
				Logs.showTrace("Http Response:" + response.mstrContent);
			}

		}
		catch (Exception e)
		{
			Logs.showTrace("Http Get Exception:" + e.toString());
		}

		return response.mnCode;
	}
}
