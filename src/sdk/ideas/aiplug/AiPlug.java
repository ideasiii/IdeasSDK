/**
 * 提供智慧插座使用，主要呼叫SER API
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
	private final int	ID_LIST		= 0;
	private final int	ID_CONFIG	= 1;

	private final String TARGET_URL = "http://54.92.30.60:80/api/smarthome/list";

	private HttpClient							httpClient		= null;
	private ArrayList<AiPlugResponseListener>	listListener	= null;

	public AiPlug()
	{
		httpClient = new HttpClient();
		httpClient.setOnHttpResponseListener(httpListener);
	}

	public static interface AiPlugResponseListener
	{
		public void onResponse(final int nCode, final String strContent);
	}

	/**
	 * List all AiPlug device
	 * 
	 * @param strToken : API Token
	 * @return 0:success , -1:fail
	 */
	public int list(final String strToken)
	{
		if (null == strToken)
			return -1;

		HashMap<String, String> mapParam = new HashMap<String, String>();
		mapParam.put("token", strToken);
		httpClient.httpPost(ID_LIST, TARGET_URL, mapParam);
		return 0;
	}

	private HttpClient.HttpResponseListener httpListener = new HttpClient.HttpResponseListener()
	{
		@Override
		public void response(int nId, int nCode, String strContent)
		{
			switch(nId)
			{
			case ID_LIST:
				Logs.showTrace("HTTP RESPONSE - Code:" + String.valueOf(nCode) + " Content:" + strContent);
				break;
			case ID_CONFIG:
				break;
			default:
				break;
			}
		}
	};
}
