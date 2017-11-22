package sdk.ideas.common;

public abstract class Protocol
{
	/*
	 * Service Type
	 */
	static public final int	TYPE_MOBILE_TRACKER			= 1;
	static public final int	TYPE_POWER_STATION	 		= 2;
	static public final int	TYPE_SDK_TRACKER			= 3;

	/** CMP Command **/
	static public int		msnSequence			= 0;

	static public final int	CMP_HEADER_SIZE				= 16;

	static public final int	GENERIC_NACK				= 0x80000000;

	static public final int	BIND_REQUEST				= 0x00000001;
	static public final int	BIND_RESPONSE				= 0x80000001;

	static public final int	AUTHENTICATION_REQUEST			= 0x00000002;
	static public final int	AUTHENTICATION_RESPONSE			= 0x80000002;

	static public final int	ACCESS_LOG_REQUEST			= 0x00000003;
	static public final int	ACCESS_LOG_RESPONSE			= 0x80000003;

	static public final int	INITIAL_REQUEST				= 0x00000004;
	static public final int	INITIAL_RESPONSE			= 0x80000004;

	static public final int	SIGN_UP_REQUEST				= 0x00000005;
	static public final int	SIGN_UP_RESPONSE			= 0x80000005;

	static public final int	UNBIND_REQUEST				= 0x00000006;
	static public final int	UNBIND_RESPONSE				= 0x80000006;

	static public final int	UPDATE_REQUEST				= 0x00000007;
	static public final int	UPDATE_RESPONSE				= 0x80000007;

	static public final int	REBOOT_REQUEST				= 0x00000010;
	static public final int	REBOOT_RESPONSE				= 0x80000010;

	static public final int	CONFIGURATION_REQUEST			= 0x00000011;
	static public final int	CONFIGURATION_RESPONSE			= 0x80000011;

	static public final int	POWER_PORT_SETTING_REQUEST		= 0x00000012;
	static public final int	POWER_PORT_SETTING_RESPONSE		= 0x80000012;

	static public final int	POWER_PORT_STATE_REQUEST		= 0x00000013;
	static public final int	POWER_PORT_STATE_RESPONSE		= 0x80000013;

	static public final int	SER_API_SIGNIN_REQUEST			= 0x00000014;
	static public final int	SER_API_SIGNIN_RESPONSE			= 0x80000014;

	static public final int	ENQUIRE_LINK_REQUEST			= 0x00000015;
	static public final int	ENQUIRE_LINK_RESPONSE			= 0x80000015;

	static public final int	MDM_LOGIN_REQUEST			= 0x00000016;
	static public final int	MDM_LOGIN_RESPONSE			= 0x80000016;

	static public final int	MDM_OPERATE_REQUEST			= 0x00000017;
	static public final int	MDM_OPERATE_RESPONSE			= 0x80000017;
	
	static public final int	MDM_LOGOUT_REQUEST			= 0x00000018;
	static public final int	MDM_LOGOUT_RESPONSE			= 0x80000018;
	
	static public final int	MDM_STATE_REQUEST			= 0x00000019;
	static public final int	MDM_STATE_RESPONSE			= 0x80000019;
	
	//despected
	static public final int	SDK_TRACKER_REQUEST			= 0x00000018;
	static public final int	SDK_TRACKER_RESPONSE			= 0x80000018;

	static public final int	SMART_BUILDING_DOOR_CONTROL_REQUEST	= 0x00000056;
	static public final int	SMART_BUILDING_DOOR_CONTROL_RESPONSE	= 0x80000056;

	
	static public final int	STATUS_ROK					= 0x00000000;
	static public final int	STATUS_RINVMSGLEN			= 0x00000001;
	static public final int	STATUS_RINVCMDLEN			= 0x00000002;
	static public final int	STATUS_RINVCMDID			= 0x00000003;
	static public final int	STATUS_RINVBNDSTS			= 0x00000004;
	static public final int	STATUS_RALYBND				= 0x00000005;
	static public final int	STATUS_SYSBUSY				= 0x00000006;
	static public final int	STATUS_RSYSERR				= 0x00000008;
	static public final int	STATUS_RBINDFAIL			= 0x00000010;
	static public final int	STATUS_RPPSFAIL				= 0x00000011;
	static public final int	STATUS_RPPSTAFAIL			= 0x00000012;
	static public final int	STATUS_RINVBODY				= 0x00000040;
	static public final int	STATUS_RINVCTRLID			= 0x00000041;

	static public class CMP_HEADER
	{
		public int	nLength;
		public int	nId;
		public int	nStatus;
		public int	nSequence;

		public void clean()
		{
			nLength = 0;
			nId = 0;
			nStatus = 0;
			nSequence = 0;
		}
	}

	static public class CMP_AUTHENTICATION_BODY
	{
		String	strClientMAC;
		String	strAuthStatus;
	}

	static public class CMP_BIND_RESP_BODY
	{
		String	strAuthPageUrl;
		String	strDefaultUrl;
	}

	static public class CMP_ACCESS_REQ_BODY
	{
		String	strClientMAC;
		String	strDestAddr;
		String	strDestPort;
		String	strWebUrl;
	}
}
