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
	public final static int ERR_DOWNLOAD_ON_MAIN_THREAD        = -20 ;
	
	public final static int ERR_WIFI_SAVE_WIFICONFIG_OPERATION_EXECUTE_FAIL = -21;
	
	public final static int ERR_WIFI_SSID_NOT_FOUND = -22;
	
	public final static int ERR_POWER_PORT_SETTING_FAIL = -23;
	public final static int ERR_GET_POWER_STATE_FAIL    = -24;
	
	public final static int ERR_DEVICE_NOT_SUPPORT_BLUETOOTH = -25;
	
	public final static int ERR_BLUETOOTH_CANCELLED_BY_USER = -26;
	public final static int ERR_BLUETOOTH_DISCOVERABLE_CANCELLED_BY_USER = -27;

	public final static int ERR_BLUETOOTH_DEVICE_NOT_FOUND = -28;
	public final static int ERR_BLUETOOTH_DEVICE_BOND_FAIL = -29;
	
	public final static int ERR_SDK_APP_ID_INVAILD = -31;
	public final static int ERR_ACTIVITY_NOT_FOUND = -32;
	public final static int ERR_DEVICE_NOT_SUPPORT = -33;
	public final static int ERR_PASSWORD_FORMAT_NOT_SUPPORT = -34;
	
	public final static int ERR_MICROPHONE_NOT_EXISTS = -35;
	
	public final static int ERR_SPEECH_ERRORMESSAGE = -36;
	
	public final static int ERR_SYSTEM_BUSY = -40;
		
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
	public static final int METHOD_APPLICATION_DOWNLOAD_APP     = 4;
	
	
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
	public final static int METHOD_FINISH_WEBVIEW_INTENT = 1;
	//ctrl locker
	public final static int METHOD_RESET_SCREEN_LOCK_PASSWORD = 0;
	public final static int METHOD_LOCK_SCREEN_NOW            = 1;
	public final static int METHOD_LOCK_STATUS_BAR           = 2;
	public final static int METHOD_UNLOCK_STATUS_BAR         = 3;

    //ctrl wifi
	public final static int METHOD_WIFI_LOCK = 0;
	public final static int METHOD_REMOVE_WIFI_CONFIG = 1;
	public final static int METHOD_SAVE_WIFI_CONFIG = 2;
	public final static int METHOD_DISCONNECT_SSID = 3;
	
	
	//ctrl bluetooth
	public final static int METHOD_SETUP_BLUETOOTH = 0;
	
	public final static int METHOD_BLUETOOTH_DISCOVERING_NEW_DEVICE = 1;
	public final static int METHOD_BLUETOOTH_DISCOVER_FINISHED = 2;
	
	public final static int BLUETOOTH_IS_ON = 3;
	public final static int BLUETOOTH_IS_OFF = 4;
	
	public final static int METHOD_DISCOVERABLE_BLUETOOTH = 5;
	public final static int METHOD_SCAN_MODE_CHANGE_BLUETOOTH = 6;
	public final static int METHOD_BOND_STATE_CHANGE_BLUETOOTH = 7;
	
	public final static int METHOD_OPEN_BLUETOOTH_CONNECTED_LINK = 8;
	public final static int METHOD_CLOSE_BLUETOOTH_CONNECTED_LINK = 9;
	
	public final static int METHOD_SEND_MESSAGE_BLUETOOTH = 10;
	public final static int METHOD_GET_MESSAGE_BLUETOOTH = 11;
	
	//tool google speech recognizer
	public final static int METHOD_START_SPEECH_RECOGNIZER_SIMPLE = 0;
	public final static int METHOD_RETURN_TEXT_SPEECH_RECOGNIZER_SIMPLE = 1;
	
	//tool google text to speech 
	public final static int METHOD_TEXT_TO_SPEECH_INIT = 0;
	public final static int METHOD_TEXT_TO_SPEECH_SPEECHING = 1;
	
	public final static int METHOD_AMX_COTROL_COMMAND = 0;
	public final static int METHOD_AMX_STATUS_COMMAND = 1;
	public final static int METHOD_AMX_STATUS_RESPONSE_COMMAND = 2;
	
	public final static int METHOD_START_VOICE_RECOGNIZER = 0;
	public final static int METHOD_RETURN_TEXT_VOICE_RECOGNIZER = 1;
	public final static int METHOD_RETURN_RMS_VOICE_RECOGNIZER = 2;
	public final static int METHOD_RETURN_BUFF_VOICE_RECOGNIZER = 3;
	//presentation helper
	public static final int METHOD_PRES_HELPER_CONNECT_TO_SERVER = 0;
	public static final int METHOD_PRES_HELPER_SEND_COMMAND = 1;
	public static final int METHOD_PRES_HELPER_RECV_MSG = 2;
	public static final int METHOD_PRES_HELPER_RECV_MSG_SLIDE_INDEX = 3;
	public static final int METHOD_PRES_HELPER_RECV_MSG_CMD_ACK = 4;
	public static final int METHOD_PRES_HELPER_RECEIVER_DISCOVERY = 15;
	
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
