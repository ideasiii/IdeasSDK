package sdk.ideas.common;

import java.util.HashMap;

public abstract class ResponseCode
{
	public static final int ERR_SUCCESS                        = 1;
	static public final int ERR_UNKNOWN                        = 0;
	public static final int ERR_IO_EXCEPTION                   = -1;
	public static final int ERR_ILLEGAL_ARGUMENT_EXCEPTION     = -2;
	public static final int ERR_CLIENT_PROTOCOL_EXCEPTION      = -3;
	public static final int ERR_UNSUPPORTED_ENCODING_EXCEPTION = -4;
	public static final int ERR_ILLEGAL_STRING_LENGTH_OR_NULL  = -5;
	public static final int ERR_NOT_INIT                       = -6;

	public static final int ERR_OS_BUILD_VERSION_SDK_INT       = -7;
	public static final int ERR_ADMIN_POLICY_INACTIVE          = -8;
	
	public static final int ERR_MALFORMED_URL_EXCEPTION        = -9;
	public static final int ERR_PROTOCOL_EXCEPTION             =-10;
	
	public static final int ERR_PACKAGE_NOT_FIND               = -11;
	public static final int ERR_INTERRUPTED_EXCEPTION          = -12;
	
	public static final int ERR_FILE_NOT_FOUND_EXCEPTION       = -13;
	
	public static final int ERR_UNREADABLE_EXTERNAL_STORAGE    = -14;
	
	public final static int ERR_URL_UNLINKABLE                 = -15;
	
	public final static int ERR_GPS_INACTIVE                   = -16;
	
	public final static int ERR_NO_SPECIFY_USE_POLICY          = -17;
	public final static int ERR_NO_SPECIFY_USE_PERMISSION      = -18;
	
	public final static int ERR_EXTERNAL_MEMORY_UNAVAILABLE    = -19 ;
	public static final int ERR_MAX                            = -100;

	/* FROM which method */
	
	//ctrl device admin
	public final static int METHOD_ADMIN_CREATE_POLICY = 0;
	public final static int METHOD_ADMIN_REMOVE_POLICY = 1;
	
	// tracker
	public static final int METHOLD_START_TRACKER = 0;
	public static final int METHOLD_TRACKER       = 1;
	public static final int METHOLD_STOP_TRACKER  = 2;

	// ctrl camera
	public static final int METHOLD_CAMERA_DISABLE       = 0;
	public static final int METHOLD_CAMERA_ENABLE        = 1;

	// ctrl volume
	public static final int METHOLD_VOLUME_MUTE_DISABLE  = 1;
	public static final int METHOLD_VOLUME_MUTE_ENABLE   = 0;

	// ctrl application
	public static final int METHOD_APPLICATION_INSTALL_SYSTEM   = 1;
	public static final int METHOD_APPLICATION_UNINSTALL_SYSTEM = 0;
	public static final int METHOD_APPLICATION_INSTALL_USER     = 2;
	public static final int METHOD_APPLICATION_UNINSTALL_USER   = 3;
	
	// ctrl record
	public static final int METHOD_RECORD_APPLICATION = 0;
	public static final int METHOD_RECORD_SDCARD_FILE = 1;
	
	// ctrl restore
	public static final int METHOD_RESTORE_APPLICATION = 0;
	public static final int METHOD_RESTORE_SDCARD_FILE = 1;
	
	//ctrl battery
	public static final int METHOD_BATTERY = 0;
	
	//ctrl storage space
	public static final int METHOD_EXTERNAL_MEMORY           = 0;
	public static final int METHOD_REMOVABLE_EXTERNAL_MEMORY = 1;
	
	//ctrl location 
	public static final int METHOD_UPDATE_LOCATION = 0;
	

	//ctrl documentWebViewer
	public final static int METHOD_START_WEBVIEW_INTENT = 0;
	
	//ctrl locker
	public final static int METHOD_RESET_SCREEN_LOCK_PASSWORD = 0;
	public final static int METHOD_LOCK_SCREEN_NOW            = 1;
	public final static int METHOD_LOCK_STATUS_BAR           = 2;
	public final static int METHOD_UNLOCK_STATUS_BAR         = 3;
	
	
	
	
	public static class ResponseMessage
	{
		public int mnCode = 0;
		public HashMap<String,String> mStrContent;
		
		public ResponseMessage(){};
		
		public ResponseMessage(ResponseMessage tmp)
		{
			mnCode = tmp.mnCode;
			mStrContent = new HashMap<String,String>(tmp.mStrContent);
		};
		
		
	}

}
