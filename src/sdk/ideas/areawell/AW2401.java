package sdk.ideas.areawell;

import java.util.HashMap;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import sdk.ideas.common.Common;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.module.CmpClient;

public class AW2401 extends BaseHandlerDL 
{
	private String IP = null;
	private int PORT = -1; 
	
	
	//for outside message begin
	//what class messgae response
	public final static int MSG_RESPONSE_AW2401 = 1;

	//from which method
	public final static int METHOD_SET_IP_AND_PORT = 0;
	public final static int METHOD_SET_POWER_PORT_SETTING = 1;
	public final static int METHOD_GET_POWER_PORT_STATE = 2;
	//for outside message end
	
	
	//for inside message begin
	private final static int MSG_AW2401_RESPONSE = 1001;
	private final static int TAG_SET_POWER_PORT_SETTING = 0;
	private final static int TAG_GET_POWER_PORT_STATE = 1;
	
	
	
	//for inside message end
	
	
	
	private Handler theHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			
			if (MSG_AW2401_RESPONSE == msg.what)
			{
				switch (msg.arg2)
				{
				case TAG_SET_POWER_PORT_SETTING:

					if (msg.arg1 == ResponseCode.ERR_SUCCESS)
					{
						message.put("message", "success");
						setResponseMessage(ResponseCode.ERR_SUCCESS, message);
						returnRespose(MSG_RESPONSE_AW2401, METHOD_SET_POWER_PORT_SETTING);
						message.clear();
					}
					else if (msg.arg1 == ResponseCode.ERR_POWER_PORT_SETTING_FAIL)
					{
						message.put("message", "power port setting fail");
						setResponseMessage(ResponseCode.ERR_POWER_PORT_SETTING_FAIL, message);
						returnRespose(MSG_RESPONSE_AW2401, METHOD_SET_POWER_PORT_SETTING);
						message.clear();
						
					}
					
					else 
					{
						message.put("message", (String)msg.obj);
						setResponseMessage(msg.arg1, message);
						returnRespose(MSG_RESPONSE_AW2401, METHOD_SET_POWER_PORT_SETTING);
						message.clear();
					}
					break;
				case TAG_GET_POWER_PORT_STATE:
					
					if (msg.arg1 == ResponseCode.ERR_SUCCESS)
					{
						message.put("message", (String)msg.obj);
						setResponseMessage(ResponseCode.ERR_SUCCESS, message);
						returnRespose(MSG_RESPONSE_AW2401, METHOD_GET_POWER_PORT_STATE);
						message.clear();
					}
					else if (msg.arg1 == ResponseCode.ERR_GET_POWER_STATE_FAIL)
					{
						message.put("message", "get power state fail");
						setResponseMessage(ResponseCode.ERR_POWER_PORT_SETTING_FAIL, message);
						returnRespose(MSG_RESPONSE_AW2401, METHOD_GET_POWER_PORT_STATE);
						message.clear();
					}
					else
					{
						message.put("message", (String)msg.obj);
						setResponseMessage(msg.arg1, message);
						returnRespose(MSG_RESPONSE_AW2401, METHOD_GET_POWER_PORT_STATE);
						message.clear();
						
					}
				
					break;
				}
			}
		}
	};
	
	private HashMap<String,String>message = null;
	
	public AW2401(Context context) 
	{
		super(context);
		message = new HashMap<String,String>();
		
	}

	public void setIPAndPort(String ip, int port)
	{
		if (null != ip )
		{
			IP = ip;
			
		}
		else
		{
			message.put("message", "ip is null");
			super.setResponseMessage(ResponseCode.ERR_ILLEGAL_ARGUMENT_EXCEPTION, message);
			super.returnRespose(MSG_RESPONSE_AW2401, METHOD_SET_IP_AND_PORT);
			message.clear();
		}
		if(port >= 0)
		{
			PORT = port;
		}
		else
		{
			message.put("message", "port can not small than 0");
			super.setResponseMessage(ResponseCode.ERR_ILLEGAL_ARGUMENT_EXCEPTION, message);
			super.returnRespose(MSG_RESPONSE_AW2401, METHOD_SET_IP_AND_PORT);
			message.clear();
		}
	}
	
	public void setPowerPortSetting(String controllerID, int powerWire, int powerPort, boolean powerSetOn)
	{
		if(null == controllerID || null == IP || (PORT < 0))
		{
			message.put("message", "controllerID or IP is null");
			super.setResponseMessage(ResponseCode.ERR_ILLEGAL_ARGUMENT_EXCEPTION, message);
			super.returnRespose(MSG_RESPONSE_AW2401, METHOD_SET_POWER_PORT_SETTING);
			message.clear();
		}
		else
		{
			sendEvent(TAG_SET_POWER_PORT_SETTING,controllerID,  powerWire,  powerPort, powerSetOn);
		}
	
	}

	public void getPowerPortState(String controllerID, int powerWire)
	{
		if (null == controllerID || null == IP || (PORT < 0))
		{
			message.put("message", "controllerID or ip or port is null");
			super.setResponseMessage(ResponseCode.ERR_ILLEGAL_STRING_LENGTH_OR_NULL, message);
			super.returnRespose(MSG_RESPONSE_AW2401, METHOD_GET_POWER_PORT_STATE);
			message.clear();
		}
		else
		{
			sendEvent(TAG_GET_POWER_PORT_STATE, controllerID, powerWire, -1, false);
		}

	}
	
	
	
	private void sendEvent(int nTag, String controllerID, int powerWireNumber, int powerPortNumber, boolean powerSetOn )
	{

		
		Thread t = new Thread(new sendSocketRunnable(nTag, controllerID, powerWireNumber, powerPortNumber, powerSetOn));
		t.start();
		
	}

	private int sendSocketData(final int mnTag, String controllerID, int powerWireNumber, int powerPortNumber,
			boolean powerSetOn, HashMap<String, String> respData, CmpClient.Response response)
	{

		if (mnTag == TAG_SET_POWER_PORT_SETTING)
		{
			String powerSetOnString = null;
			try
			{
				if (powerSetOn == true)
				{
					powerSetOnString = "1";
				}
				else
				{
					powerSetOnString = "0";
				}
				CmpClient.powerPortSettingRequest(IP, PORT, powerWireNumber, powerPortNumber, powerSetOnString, controllerID, respData, response);
			}
			catch (Exception e)
			{
				Logs.showTrace("Exception:" + e.getMessage());
			}
		}
		else if (mnTag == TAG_GET_POWER_PORT_STATE)
		{
			try
			{
				CmpClient.powerPortStateRequest(IP, PORT, powerWireNumber, controllerID, respData, response);
			}
			catch (Exception e)
			{
				Logs.showTrace("Exception:" + e.getMessage());
			}

		}

		return response.mnCode;
	}
	
	
	
	
	
	class sendSocketRunnable implements Runnable
	{

		private int		mnTag	= -1;
		private String controllerID = null;
		private int powerWireNumber = -1;
		private int powerPortNumber = -1;
		private boolean powerSetOn = false;
		
		@Override
		public void run()
		{
			HashMap<String, String> respData = new HashMap<String, String>();
			CmpClient.Response response = new CmpClient.Response();
		
			sendSocketData(mnTag,controllerID,powerWireNumber,powerPortNumber,powerSetOn,respData,response);
				
			Common.postMessage(theHandler, MSG_AW2401_RESPONSE, response.mnCode, mnTag, response.mstrContent);

		}

		
		public sendSocketRunnable(final int nTag,String controllerID, int powerWireNumber, int powerPortNumber, boolean powerSetOn )
		{
				mnTag = nTag;
				this.controllerID = controllerID;
				this.powerWireNumber = powerWireNumber;
				this.powerPortNumber = powerPortNumber;
				this.powerSetOn = powerSetOn;
		}
		
		

	}
	
	
	
	
	
	
}
