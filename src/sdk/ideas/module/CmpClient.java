package sdk.ideas.module;

import java.nio.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import android.annotation.SuppressLint;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import sdk.ideas.common.Logs;
import sdk.ideas.common.Protocol;
import sdk.ideas.common.ResponseCode;

public class CmpClient
{
	private static final int	ERR_CMP				= -1000;
	public static final int		ERR_PACKET_LENGTH	= -6 + ERR_CMP;
	public static final int		ERR_PACKET_SEQUENCE	= -7 + ERR_CMP;
	public static final int		ERR_REQUEST_FAIL	= -8 + ERR_CMP;
	public static final int		ERR_SOCKET_INVALID	= -9 + ERR_CMP;
	public static final int		ERR_INVALID_PARAM	= -10 + ERR_CMP;
	public static final int		ERR_LOG_DATA_LENGTH	= -11 + ERR_CMP;

	private final String VERSION = "CMP Client Version 0.16.03.08";

	public static class Response
	{
		public int		mnCode		= 0;
		public String	mstrContent	= null;
	}

	public CmpClient()
	{

	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		// return super.toString();
		return VERSION;
	}

	private static boolean validSocket(Socket msocket)
	{
		if (null == msocket || msocket.isClosed())
			return false;
		return true;
	}

	private static int getSequence()
	{
		++Protocol.msnSequence;
		if (0x7FFFFFFF <= Protocol.msnSequence)
		{
			Protocol.msnSequence = 0x00000001;
		}
		return Protocol.msnSequence;
	}

	public static void init(final String strIP, final int nPort, String initType, HashMap<String, String> respData,
			Response response)
	{
		if (null == response)
			return;

		if (null == initType || null == respData)
		{
			response.mstrContent = "respData or initType is null";
			response.mnCode = ResponseCode.ERR_ILLEGAL_STRING_LENGTH_OR_NULL;
			return;
		}

		Socket msocket = null;
		response.mnCode = -1;

		try
		{
			msocket = new Socket(strIP, nPort);
			if (!validSocket(msocket))
			{
				response.mstrContent = "not validSocket";
				response.mnCode = ERR_SOCKET_INVALID;
				return;
			}
			final int nSequence = getSequence();
			OutputStream outSocket = null;
			outSocket = msocket.getOutputStream();

			InputStream inSocket = null;
			inSocket = msocket.getInputStream();

			int nLength = Protocol.CMP_HEADER_SIZE + 4 + 1;

			ByteBuffer buf = ByteBuffer.allocate(nLength);

			// Head Field Name Size octets
			// command length 4
			// command id 4
			// command status 4
			// sequence number 4

			buf.putInt(nLength);
			buf.putInt(Protocol.INITIAL_REQUEST);
			buf.putInt(0);
			buf.putInt(nSequence);

			respData.put("REQ_LENGTH", String.valueOf(nLength));
			respData.put("REQ_ID", "initial_request");
			respData.put("REQ_STATUS", "0");
			respData.put("REQ_SEQUENCE", String.valueOf(nSequence));

			// Body Field Name Size octets
			// InitType 4

			int initTypeInt = Integer.valueOf(initType);

			buf.putInt(initTypeInt);

			respData.put("REQ_BODY_INIT_TYPE", initType);

			buf.put((byte) 0);

			buf.flip();
			outSocket.write(buf.array());
			buf.clear();

			buf = ByteBuffer.allocate(Protocol.CMP_HEADER_SIZE + 255);

			nLength = inSocket.read(buf.array());

			buf.rewind();

			if (Protocol.CMP_HEADER_SIZE <= nLength)
			{
				response.mnCode = checkResponse(buf, nSequence);
				buf.order(ByteOrder.BIG_ENDIAN);

				respData.put("RESP_LENGTH", String.valueOf(buf.getInt(0)));
				//Logs.showTrace("REQ_LENGTH: " + respData.get("REQ_LENGTH"));
				respData.put("RESP_ID", String.valueOf(buf.getInt(4) & 0x00ffffff));

				respData.put("RESP_STATUS", String.valueOf(buf.getInt(8)));
				respData.put("RESP_SEQUENCE", String.valueOf(buf.getInt(12)));

				if (ResponseCode.ERR_SUCCESS == response.mnCode)
				{
					byte[] bytes = new byte[buf.getInt(0)];
					//Logs.showTrace(String.valueOf("remaining " + buf.remaining()));
					buf.get(bytes);
					String strTemp = new String(bytes, Charset.forName("UTF-8"));
					String strBody = strTemp.substring(16);
					respData.put("RESP_BODY", strBody);

					response.mstrContent = respData.get("RESP_BODY");
				}

				// debug

				/*
				 * Logs.showTrace("REQ_LENGTH: " + respData.get("REQ_LENGTH"));
				 * Logs.showTrace("REQ_ID: " + respData.get("REQ_ID"));
				 * Logs.showTrace("REQ_STATUS: " + respData.get("REQ_STATUS"));
				 * Logs.showTrace("REQ_SEQUENCE: " +
				 * respData.get("REQ_SEQUENCE")); Logs.showTrace(
				 * "REQ_BODY_INIT_TYPE: " + respData.get("REQ_BODY_INIT_TYPE"));
				 * 
				 * 
				 * Logs.showTrace("RESP_LENGTH: " +
				 * respData.get("RESP_LENGTH")); Logs.showTrace("RESP_ID: " +
				 * respData.get("RESP_ID")); Logs.showTrace("RESP_STATUS: " +
				 * respData.get("RESP_STATUS")); Logs.showTrace(
				 * "RESP_SEQUENCE: " + respData.get("RESP_SEQUENCE"));
				 * Logs.showTrace("RESP_BODY: " + respData.get("RESP_BODY"));
				 */

			}
			else
			{
				response.mnCode = ERR_PACKET_LENGTH;
				response.mstrContent = "ERR_PACKET_LENGTH !";
			}

			buf.clear();
			buf = null;
			msocket.close();

		}
		catch (Exception e)
		{
			//Logs.showTrace(e.getMessage());
			response.mnCode = -1;
			response.mstrContent = "connected fail, IO exception";
		}

	}

	public static void SignUpRequest(final String strIP, final int nPort, String signUpType, String signUpData,
			HashMap<String, String> respData, Response response)
	{
		if (null == response)
			return;

		if (null == signUpType || null == signUpData || null == respData)
		{
			response.mstrContent = "respData or signUpType or signUpData is null";
			response.mnCode = ResponseCode.ERR_ILLEGAL_STRING_LENGTH_OR_NULL;
			return;
		}
		try
		{
			if (signUpData.getBytes("US-ASCII").length > 2000)
			{
				response.mstrContent = "signUpData too much character";
				response.mnCode = ResponseCode.ERR_ILLEGAL_STRING_LENGTH_OR_NULL;
				return;
			}
		}
		catch (UnsupportedEncodingException e1)
		{
			response.mstrContent = "signUpData UnsupportedEncodingException";
			response.mnCode = ResponseCode.ERR_UNSUPPORTED_ENCODING_EXCEPTION;
			return;
		}

		Socket msocket = null;

		response.mnCode = -1;
		try
		{

			//Logs.showTrace("IP: " + strIP + " port: " + nPort);
			msocket = new Socket(strIP, nPort);

			if (!validSocket(msocket))
			{
				response.mstrContent = "not validSocket";
				response.mnCode = ERR_SOCKET_INVALID;
				return;
			}

			final int nSequence = getSequence();
			OutputStream outSocket = null;

			outSocket = msocket.getOutputStream();

			InputStream inSocket = null;

			inSocket = msocket.getInputStream();

			int nLength = Protocol.CMP_HEADER_SIZE + 4 + signUpData.length() + 1;

			ByteBuffer buf = ByteBuffer.allocate(nLength);
			// Logs.showTrace("test in 262");
			// Head Field Name |Size octets
			// command length | 4
			// command id | 4
			// command status | 4
			// sequence number | 4

			buf.putInt(nLength);
			buf.putInt(Protocol.SIGN_UP_REQUEST);
			buf.putInt(0);

			buf.putInt(nSequence);

			respData.put("REQ_LENGTH", String.valueOf(nLength));
			respData.put("REQ_ID", "signUp_request");
			respData.put("REQ_STATUS", "0");
			respData.put("REQ_SEQUENCE", String.valueOf(nSequence));

			// Body Field Name Size octets
			// signUpType 1
			// signUpDATA MAX 2000

			buf.putInt(Integer.valueOf(signUpType));
			respData.put("REQ_BODY_SIGN_UP_TYPE", signUpType);

			buf.put(signUpData.getBytes("US-ASCII"));
			respData.put("REQ_BODY_SIGN_UP_DATA", signUpData);

			buf.put((byte) 0);

			buf.flip();

			outSocket.write(buf.array());

			buf.clear();

			buf = ByteBuffer.allocate(Protocol.CMP_HEADER_SIZE);

			nLength = inSocket.read(buf.array());

			buf.rewind();
			// Logs.showTrace("test in 298");
			if (Protocol.CMP_HEADER_SIZE == nLength)
			{
				response.mnCode = checkResponse(buf, nSequence);
				buf.order(ByteOrder.BIG_ENDIAN);
				respData.put("RESP_LENGTH", String.valueOf(buf.getInt(0)));
				respData.put("RESP_ID", String.valueOf(buf.getInt(4) & 0x00ffffff));
				respData.put("RESP_STATUS", String.valueOf(buf.getInt(8)));
				respData.put("RESP_SEQUENCE", String.valueOf(buf.getInt(12)));

				// debug
				// Logs.showTrace(respData.get("REQ_BODY_SIGN_UP_DATA"));
				//Logs.showTrace(respData.get("RESP_LENGTH"));
				//Logs.showTrace(respData.get("RESP_ID"));
				//Logs.showTrace(respData.get("RESP_STATUS"));
				///Logs.showTrace(respData.get("RESP_SEQUENCE"));

			}
			else
			{
				response.mnCode = ERR_PACKET_LENGTH;
				response.mstrContent = "ERR_PACKET_LENGTH !";
			}

			buf.clear();
			buf = null;
			msocket.close();
			//Logs.showTrace("final sign up");

		}
		catch (IOException e)
		{
			response.mnCode = -1;
			response.mstrContent = "connected fail, IO exception";
		}

		//Logs.showTrace(String.valueOf(response.mnCode));
	}

	public static void accessLogRequest(final String strIP, final int nPort, String accessLogType, String accessLogData,
			HashMap<String, String> respData, Response response)
	{

		if (null == response)
			return;
		Socket msocket = null;

		if (null == respData || null == accessLogType || null == accessLogData)
		{
			response.mstrContent = "respData or accessLogType or accessLogData is null";
			response.mnCode = ResponseCode.ERR_ILLEGAL_STRING_LENGTH_OR_NULL;
			return;
		}
		try
		{
			if (accessLogData.getBytes("US-ASCII").length > 2000)
			{
				response.mstrContent = "accessLogData too much character";
				response.mnCode = ResponseCode.ERR_ILLEGAL_STRING_LENGTH_OR_NULL;
				return;
			}
		}
		catch (UnsupportedEncodingException e1)
		{
			response.mstrContent = "accessLogData UnsupportedEncodingException";
			response.mnCode = ResponseCode.ERR_UNSUPPORTED_ENCODING_EXCEPTION;
			return;
		}
		// Logs.showTrace("377");
		try
		{
			//Logs.showTrace("strIP: " + strIP + " port: " + nPort);
			msocket = new Socket(strIP, nPort);
			//Logs.showTrace("strIP: " + strIP + " port: " + nPort);
			if (!validSocket(msocket))
			{
				response.mstrContent = "not validSocket";
				response.mnCode = ERR_SOCKET_INVALID;
				return;
			}

			final int nSequence = getSequence();
			OutputStream outSocket = null;

			outSocket = msocket.getOutputStream();

			InputStream inSocket = null;

			inSocket = msocket.getInputStream();

			// Logs.showTrace("398");
			int nLength = Protocol.CMP_HEADER_SIZE + 1 + accessLogData.length() + 1;

			ByteBuffer buf = ByteBuffer.allocate(nLength);

			// Head Field Name Size octets
			// command length 4
			// command id 4
			// command status 4
			// sequence number 4

			buf.putInt(nLength);
			buf.putInt(Protocol.ACCESS_LOG_REQUEST);
			buf.putInt(0);
			buf.putInt(nSequence);

			respData.put("REQ_LENGTH", String.valueOf(nLength));
			respData.put("REQ_ID", "access_log_request");
			respData.put("REQ_STATUS", "0");
			respData.put("REQ_SEQUENCE", String.valueOf(nSequence));

			// Body Field Name Size octets
			// accessLogType 1
			// accessLogData MAX 2000

			buf.put(accessLogType.getBytes("US-ASCII"));

			respData.put("REQ_BODY_ACCESS_LOG_TYPE", accessLogType);

			buf.put(accessLogData.getBytes("US-ASCII"));

			buf.put((byte) 0);
			respData.put("REQ_BODY_ACCESS_LOG_DATA", accessLogData);

			buf.flip();
			// Logs.showTrace("434");

			outSocket.write(buf.array());

			buf.clear();

			buf = ByteBuffer.allocate(Protocol.CMP_HEADER_SIZE);

			nLength = inSocket.read(buf.array());

			buf.rewind();
			// Logs.showTrace("445");
			if (Protocol.CMP_HEADER_SIZE == nLength)
			{
				response.mnCode = checkResponse(buf, nSequence);
				buf.order(ByteOrder.BIG_ENDIAN);
				respData.put("RESP_LENGTH", String.valueOf(buf.getInt(0)));
				respData.put("RESP_ID", String.valueOf(buf.getInt(4) & 0x00ffffff));
				respData.put("RESP_STATUS", String.valueOf(buf.getInt(8)));
				respData.put("RESP_SEQUENCE", String.valueOf(buf.getInt(12)));

				//Logs.showTrace(respData.get("RESP_LENGTH"));
				//Logs.showTrace(respData.get("RESP_ID"));
				//Logs.showTrace(respData.get("RESP_STATUS"));
				//Logs.showTrace(respData.get("RESP_SEQUENCE"));

			}
			else
			{
				response.mnCode = ERR_PACKET_LENGTH;
				response.mstrContent = "ERR_PACKET_LENGTH !";
			}

			buf.clear();
			buf = null;
			msocket.close();

			// Logs.showTrace("final acess log");
		}
		catch (IOException e)
		{
			response.mnCode = -1;
			response.mstrContent = "IOException, network inconnect";
		}

	}
	
	public static void powerPortSettingRequest(final String strIP, final int nPort, int wireNum, int portNum,
			String powerState, String controllerID, HashMap<String, String> respData, Response response)
	{
		if (null == response)
			return;
		Socket msocket = null;
		if(null == respData || null == powerState || null == controllerID)
		{
			response.mstrContent = "respData or powerState or controllerID is null";
			response.mnCode = ResponseCode.ERR_ILLEGAL_STRING_LENGTH_OR_NULL;
			return;
		}
		try
		{
			//Logs.showTrace("strIP: " + strIP + " port: " + nPort);
			msocket = new Socket(strIP, nPort);
			//Logs.showTrace("strIP: " + strIP + " port: " + nPort);
			if (!validSocket(msocket))
			{
				response.mstrContent = "not validSocket";
				response.mnCode = ERR_SOCKET_INVALID;
				return;
			}
			
			final int nSequence = getSequence();
			OutputStream outSocket = null;

			outSocket = msocket.getOutputStream();

			InputStream inSocket = null;

			inSocket = msocket.getInputStream();
			
			int nLength = Protocol.CMP_HEADER_SIZE + 4 + 4 + 1 + controllerID.length() + 1;
			
			ByteBuffer buf = ByteBuffer.allocate(nLength);
			
			//header
			buf.putInt(nLength);
			buf.putInt(Protocol.POWER_PORT_SETTING_REQUEST);
			buf.putInt(0);
			buf.putInt(nSequence);
			
			respData.put("REQ_LENGTH", String.valueOf(nLength));
			respData.put("REQ_ID", "PowerPortSetting_request");
			respData.put("REQ_STATUS", "0");
			respData.put("REQ_SEQUENCE", String.valueOf(nSequence));
			//end header

			//body
			buf.putInt(wireNum);
			buf.putInt(portNum);
			buf.put(powerState.getBytes("US-ASCII"));
			buf.put(controllerID.getBytes("US-ASCII"));
			buf.put((byte) 0);
			respData.put("REQ_BODY_WIRE_NUMBER", String.valueOf(wireNum));
			respData.put("REQ_BODY_PORT_NUMBER", String.valueOf(portNum));
			respData.put("REQ_BODY_POWER_STATE", powerState);
			respData.put("REQ_BODY_CONTROLLER_ID", controllerID);
			//end body
			
			buf.flip();
			outSocket.write(buf.array());
			buf.clear();
			buf = ByteBuffer.allocate(Protocol.CMP_HEADER_SIZE + 10);
			nLength = inSocket.read(buf.array());
			buf.rewind();
			
			
			response.mnCode = checkResponse(buf, nSequence);
			buf.order(ByteOrder.BIG_ENDIAN);
			respData.put("RESP_LENGTH", String.valueOf(buf.getInt(0)));
			respData.put("RESP_ID", String.valueOf(buf.getInt(4) & 0x00ffffff));
			respData.put("RESP_STATUS", String.valueOf(buf.getInt(8)));
			respData.put("RESP_SEQUENCE", String.valueOf(buf.getInt(12)));

			// for debuging
			//Logs.showTrace("RESP_LENGTH "+ respData.get("RESP_LENGTH"));
			//Logs.showTrace("RESP_ID "  + respData.get("RESP_ID"));
			//Logs.showTrace("RESP_STATUS " + respData.get("RESP_STATUS"));
			//Logs.showTrace("RESP_SEQUENCE "+ respData.get("RESP_SEQUENCE"));
		
			buf.clear();
			buf = null;
			msocket.close();
			
			
		}
		catch (IOException e)
		{
			response.mnCode = -1;
			response.mstrContent = "IOException, network inconnect";
			Logs.showTrace(e.toString());
		}
		catch (Exception e)
		{
			response.mnCode = 0;
			response.mstrContent = e.toString();
			Logs.showTrace(e.toString());
		}
	
	}
	
	public static void powerPortStateRequest(final String strIP, final int nPort, int wireNum, String controllerID, HashMap<String, String> respData, Response response)
	{
		if (null == response)
			return;
		Socket msocket = null;
		if(null == respData || null == controllerID)
		{
			response.mstrContent = "respData or controllerID is null";
			response.mnCode = ResponseCode.ERR_ILLEGAL_STRING_LENGTH_OR_NULL;
			return;
		}
		try
		{
			//Logs.showTrace("strIP: " + strIP + " port: " + nPort);
			msocket = new Socket(strIP, nPort);
			//Logs.showTrace("strIP: " + strIP + " port: " + nPort);
			if (!validSocket(msocket))
			{
				response.mstrContent = "not validSocket";
				response.mnCode = ERR_SOCKET_INVALID;
				return;
			}
			final int nSequence = getSequence();
			OutputStream outSocket = null;

			outSocket = msocket.getOutputStream();

			InputStream inSocket = null;

			inSocket = msocket.getInputStream();
			
			int nLength = Protocol.CMP_HEADER_SIZE + 4 + controllerID.length() + 1;
			
			ByteBuffer buf = ByteBuffer.allocate(nLength);
			
			//header 
			buf.putInt(nLength);
			buf.putInt(Protocol.POWER_PORT_STATE_REQUEST);
			buf.putInt(0);
			buf.putInt(nSequence);
			
			respData.put("REQ_LENGTH", String.valueOf(nLength));
			respData.put("REQ_ID", "power_port_state_request");
			respData.put("REQ_STATUS", "0");
			respData.put("REQ_SEQUENCE", String.valueOf(nSequence));
			//header end
			
			//body
			buf.putInt(wireNum);
			buf.put(controllerID.getBytes("US-ASCII"));
			buf.put((byte) 0);
			
			respData.put("REQ_BODY_WIRE_NUMBER", String.valueOf(wireNum));
			respData.put("REQ_BODY_CONTROLLER_ID", controllerID);
			//body end
			
			buf.flip();
			outSocket.write(buf.array());
			buf.clear();
			buf = ByteBuffer.allocate(Protocol.CMP_HEADER_SIZE + 255);
			nLength = inSocket.read(buf.array());
			//Logs.showTrace(String.valueOf(nLength));
			buf.rewind();
			
			//Logs.showTrace("State length: "+String.valueOf(nLength));
			//Logs.showTrace(new String(buf.array()));
			
			response.mnCode = checkResponse(buf, nSequence);
			buf.order(ByteOrder.BIG_ENDIAN);
			respData.put("RESP_LENGTH", String.valueOf(buf.getInt(0)));
			respData.put("RESP_ID", String.valueOf(buf.getInt(4) & 0x00ffffff));
			respData.put("RESP_STATUS", String.valueOf(buf.getInt(8)));
			respData.put("RESP_SEQUENCE", String.valueOf(buf.getInt(12)));

			if (ResponseCode.ERR_SUCCESS == response.mnCode)
			{
				
				byte[] bytes = new byte[buf.getInt(0)];

			    buf.get(bytes);
			    
			    String strTemp = new String(bytes, Charset.forName("UTF-8"));

			    String strBody = strTemp.substring(16);

			    byte[] bBody = strBody.getBytes();

			    int i;
				for (i = 0; i < bBody.length; ++i)
				{
					if (0 == bBody[i])
					{
						break;
					}
				}

			    strBody = strBody.substring(0, i);
			    
			    respData.put("RESP_BODY", strBody);
			    response.mstrContent = strBody;
			}
			
			// for debuging
			// Logs.showTrace("RESP_LENGTH "+ respData.get("RESP_LENGTH"));
			// Logs.showTrace("RESP_ID " + respData.get("RESP_ID"));
			// Logs.showTrace("RESP_STATUS " + respData.get("RESP_STATUS"));
			// Logs.showTrace("RESP_SEQUENCE "+ respData.get("RESP_SEQUENCE"));

			buf.clear();
			buf = null;
			msocket.close();
	
		}
		catch (IOException e)
		{
			//Logs.showError(e.toString());
			response.mnCode = -1;
			response.mstrContent = "IOException, network inconnect";
		}
		catch (Exception e)
		{
			response.mnCode = 0;
			response.mstrContent = e.toString();
		}
		
		
		
	}
	
	
	
	
	public static void mdmRequest(final String strIP, final int nPort, String mdmType, String mdmData,
			HashMap<String, String> respData, Response response)
	{
		
		//....
		
	}
	
	

	private static int checkResponse(ByteBuffer buf, int nSequence)
	{
		int nResult = ResponseCode.ERR_UNKNOWN;

		Protocol.CMP_HEADER cmpResp = new Protocol.CMP_HEADER();
		buf.order(ByteOrder.BIG_ENDIAN);
		cmpResp.nLength = buf.getInt(0); // offset
		cmpResp.nId = buf.getInt(4) & 0x00ffffff;
		cmpResp.nStatus = buf.getInt(8);
		cmpResp.nSequence = buf.getInt(12);

		if (cmpResp.nSequence != nSequence)
		{
		//	Logs.showTrace(String.valueOf(cmpResp.nSequence)+"   "+String.valueOf(nSequence));
			nResult = ERR_PACKET_SEQUENCE;
		}
		else
		{
			//Logs.showTrace(String.valueOf(cmpResp.nStatus));
			if (Protocol.STATUS_ROK == cmpResp.nStatus)
			{
				nResult = ResponseCode.ERR_SUCCESS;
			}
			else if (Protocol.STATUS_RPPSFAIL == cmpResp.nStatus)
			{
				nResult = ResponseCode.ERR_POWER_PORT_SETTING_FAIL;
			}
			else if (Protocol.STATUS_RPPSTAFAIL == cmpResp.nStatus)
			{
				nResult = ResponseCode.ERR_GET_POWER_STATE_FAIL; 
			}
			else
			{
				nResult = ERR_REQUEST_FAIL;
			}
		}
		cmpResp = null;
		return nResult;
	}

}
