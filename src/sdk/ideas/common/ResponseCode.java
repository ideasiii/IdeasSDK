package sdk.ideas.common;

public abstract class ResponseCode
{
	public static final int ERR_SUCCESS = 1;
	static public final int ERR_UNKNOWN = 0;
	public static final int ERR_IO_EXCEPTION = -1;
	public static final int ERR_ILLEGAL_ARGUMENT_EXCEPTION = -2;
	public static final int ERR_CLIENT_PROTOCOL_EXCEPTION = -3;
	public static final int ERR_UNSUPPORTED_ENCODING_EXCEPTION = -4;
	public static final int ERR_ILLEGAL_STRING_LENGTH_OR_NULL = -5;
	public static final int ERR_NOT_INIT = -6;

	public static final int ERR_OS_BUILD_VERSION_SDK_INT = -7;
	public static final int ERR_ADMIN_POLICY_INACTIVE = -8;
	
	public static final int ERR_MALFORMED_URL_EXCEPTION = -9;
	public static final int ERR_PROTOCOL_EXCEPTION =-10;
	
	public static final int ERR_PACKAGE_NOT_FIND = -11;
	
	
	public static final int ERR_MAX = -100;

	/* FROM which method */
	// tracker
	public static final int METHOLD_START_TRACKER = 0;
	public static final int METHOLD_TRACKER = 1;
	public static final int METHOLD_STOP_TRACKER = 2;

	// mdm camera
	public static final int METHOLD_CAMERA_DISABLE       = 0;
	public static final int METHOLD_CAMERA_ENABLE        = 1;

	// mdm volume
	public static final int METHOLD_VOLUME_MUTE_DISABLE  = 1;
	public static final int METHOLD_VOLUME_MUTE_ENABLE   = 0;

	// mdm application
	public static final int METHOD_APPLICATION_INSTALL_SYSTEM  = 1;
	public static final int METHOD_APPLICATION_UNINSTALL_SYSTEM = 0;
	public static final int METHOD_APPLICATION_INSTALL_USER = 2;
	public static final int METHOD_APPLICATION_UNINSTALL_USER = 3;
	
	// mdm record
	public static final int METHOD_RECORD_APPLICATION = 0;
	public static final int METHOD_RECORD_SDCARD_FILE = 1;
	
	//mdm lock
	public static final int METHOD_LOCK_STATUS_BAR      = 0;
	public static final int METHOD_LOCK_SCREEN_PASSWORD = 1;
	

	public static class ResponseMessage
	{
		public int mnCode = 0;
		public String mStrContent = "";
	}

}
