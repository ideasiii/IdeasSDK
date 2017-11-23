/**
 * AiPlug智慧插座API，主要呼叫台灣智慧AiPlug API
 */
package sdk.ideas.aiplug;

import java.util.ArrayList;
import java.util.HashMap;

import sdk.ideas.common.Logs;
import sdk.ideas.module.HttpClient;

/**
 * @author Louis Ju
 * @since 2016-02-16
 */
public class AiPlug
{
	private final String						TARGET_HOST			= "http://testserver.tiscservice.com:8080";
	private final String						PATH_API_REGISTER	= "/AiPlugOpenAPI/Register";
	private final String						PATH_API_VERIFY		= "/AiPlugOpenAPI/Verify";
	private final String						PATH_API_LIST		= "/AiPlugOpenAPI/List";
	private final String						PATH_API_CONFIGURE	= "/AiPlugOpenAPI/Configure";

	private HttpClient							httpClient			= null;
	private ArrayList<AiPlugResponseListener>	listListener		= null;

	private final int							ID_REGISTER			= 1;
	private final int							ID_VERIFY			= 2;
	private final int							ID_LIST				= 3;
	private final int							ID_CONFIGURE		= 4;

	public AiPlug()
	{
		httpClient = new HttpClient();
		httpClient.setOnHttpResponseListener(httpListener);
		listListener = new ArrayList<AiPlugResponseListener>();
	}

	public static interface AiPlugResponseListener
	{
		public void onResponse(final int nApiId, final int nCode, final String strContent);
	}

	/**
	 * Set callback
	 * 
	 * @param listener
	 */
	public void setAiPlugResponseListener(AiPlugResponseListener listener)
	{
		if (null != listener)
		{
			listListener.add(listener);
		}
	}

	private void callback(final int nApiId, final int nCode, final String strContent)
	{
		for (int i = 0; i < listListener.size(); ++i)
		{
			listListener.get(i).onResponse(nApiId, nCode, strContent);
		}
	}

	public int register(final String strOwner, final String strMail)
	{
		HashMap<String, String> mapParam = new HashMap<String, String>();
		mapParam.put("owner", strOwner);
		mapParam.put("mail", strMail);
		httpClient.httpPost(ID_REGISTER, TARGET_HOST + PATH_API_REGISTER, mapParam);
		return 0;
	}

	public int verify(final String strOwner, final String strSmsCode)
	{
		HashMap<String, String> mapParam = new HashMap<String, String>();
		mapParam.put("owner", strOwner);
		mapParam.put("smscode", strSmsCode);
		httpClient.httpPost(ID_VERIFY, TARGET_HOST + PATH_API_VERIFY, mapParam);
		return 0;
	}

	public int list(final String strOwner, final String strToken)
	{
		HashMap<String, String> mapParam = new HashMap<String, String>();
		mapParam.put("owner", strOwner);
		mapParam.put("token", strToken);
		httpClient.httpPost(ID_LIST, TARGET_HOST + PATH_API_LIST, mapParam);
		return 0;
	}

	public int configure(final String strOutletId, final String strStatus, final String strOwner, final String strToken)
	{
		HashMap<String, String> mapParam = new HashMap<String, String>();
		mapParam.put("outletid", strOutletId);
		mapParam.put("status", strStatus);
		mapParam.put("owner", strOwner);
		mapParam.put("token", strToken);
		httpClient.httpPost(ID_CONFIGURE, TARGET_HOST + PATH_API_CONFIGURE, mapParam);
		return 0;
	}

	private HttpClient.HttpResponseListener httpListener = new HttpClient.HttpResponseListener()
	{
		@Override
		public void response(int nId, int nCode, String strContent)
		{
			Logs.showTrace("HTTP RESPONSE - Code:" + String.valueOf(nCode) + " Content:" + strContent + " ID: "
					+ String.valueOf(nId));
			switch(nId)
			{
			case ID_REGISTER:
				callback(ID_REGISTER, nCode, strContent);
				break;
			case ID_VERIFY:
				callback(ID_VERIFY, nCode, strContent);
				break;
			case ID_LIST:
				callback(ID_LIST, nCode, strContent);
				break;
			case ID_CONFIGURE:
				callback(ID_CONFIGURE, nCode, strContent);
				break;
			default:
				callback(-1, nCode, strContent);
				break;
			}
		}
	};
}
