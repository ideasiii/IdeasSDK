package sdk.ideas.module;

import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class Controller
{
	/*
	 * CMP body data length
	 */
	static public final int MAX_DATA_LEN = 2048;

	/*
	 * this define socket packet for CMP
	 */
	class CMP_HEADER
	{
		int	command_length;
		int	command_id;
		int	command_status;
		int	sequence_number;
	};

	class CMP_PACKET
	{
		CMP_HEADER	cmpHeader;
		String		cmpBody;
	};

	/*
	 * CMP Command set
	 */
	static public final int		generic_nack				= 0x80000000;
	static public final int		bind_request				= 0x00000001;
	static public final int		bind_response				= 0x80000001;
	static public final int		authentication_request		= 0x00000002;
	static public final int		authentication_response		= 0x80000002;
	static public final int		access_log_request			= 0x00000003;
	static public final int		access_log_response			= 0x80000003;
	static public final int		initial_request				= 0x00000004;
	static public final int		initial_response			= 0x80000004;
	static public final int		sign_up_request				= 0x00000005;
	static public final int		sign_up_response			= 0x80000005;
	static public final int		unbind_request				= 0x00000006;
	static public final int		unbind_response				= 0x80000006;
	static public final int		update_request				= 0x00000007;
	static public final int		update_response				= 0x80000007;
	static public final int		reboot_request				= 0x00000010;
	static public final int		reboot_response				= 0x80000010;
	static public final int		config_request				= 0x00000011;
	static public final int		config_response				= 0x80000011;
	static public final int		power_port_set_request		= 0x00000012;
	static public final int		power_port_set_response		= 0x80000012;
	static public final int		power_port_state_request	= 0x00000013;
	static public final int		power_port_state_response	= 0x80000013;
	static public final int		ser_api_signin_request		= 0x00000014;
	static public final int		ser_api_signin_response		= 0x80000014;
	static public final int		enquire_link_request		= 0x00000015;
	static public final int		enquire_link_response		= 0x80000015;
	static public final int		rdm_login_request			= 0x00000016;
	static public final int		rdm_login_response			= 0x80000016;
	static public final int		rdm_operate_request			= 0x00000017;
	static public final int		rdm_operate_response		= 0x80000017;
	static public final int		rdm_logout_request			= 0x00000018;
	static public final int		rdm_logout_response			= 0x80000018;
	static public final int		device_control_request		= 0x00000019;
	static public final int		device_control_response		= 0x80000019;
	static public final int		device_state_request		= 0x00000020;
	static public final int		device_state_response		= 0x80000020;
	static public final int		semantic_request			= 0x00000030;
	static public final int		semantic_response			= 0x80000030;

	/*
	 * CMP status set
	 */
	static public final int		STATUS_ROK					= 0x00000000;	//No Error
	static public final int		STATUS_RINVMSGLEN			= 0x00000001;	//Message Length is invalid
	static public final int		STATUS_RINVCMDLEN			= 0x00000002;	//Command Length is invalid
	static public final int		STATUS_RINVCMDID			= 0x00000003;	//Invalid Command ID
	static public final int		STATUS_RINVBNDSTS			= 0x00000004;	//Incorrect BIND Status for given command
	static public final int		STATUS_RALYBND				= 0x00000005;	//Already in Bound State
	static public final int		STATUS_RSYSERR				= 0x00000008;	//System Error
	static public final int		STATUS_RBINDFAIL			= 0x00000010;	//Bind Failed
	static public final int		STATUS_RINVBODY				= 0x00000040;	//Invalid Packet Body Data
	static public final int		STATUS_RINVCTRLID			= 0x00000041;	//Invalid Controller ID
	static public final int		STATUS_RINVJSON				= 0x00000042;	//Invalid JSON Data

	private static int			msnSequence					= 0;
	private static final int	nConnectTimeOut				= 3000;			// Socket Connect Timeout
	private static final int	nReceiveTimeOut				= 3000;			// Socket Read IO Timeout

	public static final int		ERR_CMP						= -1000;
	public static final int		ERR_PACKET_LENGTH			= -6 + ERR_CMP;
	public static final int		ERR_PACKET_SEQUENCE			= -7 + ERR_CMP;
	public static final int		ERR_REQUEST_FAIL			= -8 + ERR_CMP;
	public static final int		ERR_SOCKET_INVALID			= -9 + ERR_CMP;
	public static final int		ERR_INVALID_PARAM			= -10 + ERR_CMP;
	public static final int		ERR_LOG_DATA_LENGTH			= -11 + ERR_CMP;
	public static final int		ERR_EXCEPTION				= -12 + ERR_CMP;

	private static boolean validSocket(Socket msocket)
	{
		if (null == msocket || msocket.isClosed())
			return false;
		return true;
	}

	private static int getSequence()
	{
		if (0x7FFFFFFF <= ++msnSequence)
		{
			msnSequence = 0x00000001;
		}
		return msnSequence;
	}

	/**
	 * Controller Message Request Protocol
	 * @param strIP : Socket Connect IP.
	 * @param nPort : Socket Connect Port.
	 * @param nCommand : CMP Command. Ref. CMP Document.
	 * @param strBody : CMP Request Body, If packet is no body that will set to null.
	 * @param respPacket : CMP response packet.
	 * @return : CMP Status, Ref.CMP Document.
	 */
	public static int cmpRequest(final String strIP, final int nPort, final int nCommand, final String strBody,
			CMP_PACKET respPacket)
	{
		int nCmpStatus = STATUS_ROK;

		try
		{
			Socket msocket = new Socket();
			msocket.connect(new InetSocketAddress(strIP, nPort), nConnectTimeOut);
			msocket.setSoTimeout(nReceiveTimeOut);
			if (!validSocket(msocket))
			{
				return ERR_SOCKET_INVALID;
			}
			final int nSequence = getSequence();
			
		}
		catch (Exception e)
		{
			return ERR_EXCEPTION;
		}
		return nCmpStatus;
	}

}
