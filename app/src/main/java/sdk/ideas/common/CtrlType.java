package sdk.ideas.common;

public abstract class CtrlType
{

	public final static int REQUEST_CODE_WEBVIEW = 500;
	public final static String URL_GOOGLE_DOC_VIEWER = "https://docs.google.com/gview?embedded=true&url=";

	public final static int REQUEST_CODE_ENABLE_ADMIN = 3333;
	public final static int REQUEST_CODE_ENABLE_BLUETOOTH = 3334;
	public final static int REQUEST_CODE_DISCOVERABLE_BLUETOOTH = 3335;

	/* Google 語音response code */
	public final static int REQUEST_CODE_GOOGLE_SPEECH_SIMPLE = 3336;
	public final static int REQUEST_CODE_GOOGLE_SPEECH_CUSTOM = 3337;

	/* Android 6.0 以上 permission request code */
	public final static int REQUEST_CODE_RUNTIME_PERMISSIONS = 3338;

	public final static int MSG_RESPONSE_TRACKER_HANDLER = 1029;
	public final static int MSG_RESPONSE_DEVICE_ADMIN_HANDLER = 1030;
	public final static int MSG_RESPONSE_CAMERA_HANDLER = 1031;
	public final static int MSG_RESPONSE_VOLUME_HANDLER = 1032;
	public final static int MSG_RESPONSE_APPLICATION_HANDLER = 1033;
	public final static int MSG_RESPONSE_RECORD_HANDLER = 1034;
	public final static int MSG_RESPONSE_BATTERY_HANDLER = 1035;
	public final static int MSG_RESPONSE_STORAGE_SPACE_HANDLER = 1036;
	public final static int MSG_RESPONSE_LOCATION_HANDLER = 1037;
	public final static int MSG_RESPONSE_RESTORE_HANDLER = 1038;
	public final static int MSG_RESPONSE_DOCUMENT_WEBVIEW_HANDLER = 1039;
	public final static int MSG_RESPONSE_LOCK_HANDLER = 1040;

	public final static int MSG_RESPONSE_WIFI_HANDLER = 1041;

	public final static int MSG_RESPONSE_BLUETOOTH_HANDLER = 1042;
	public final static int MSG_RESPONSE_SPEECH_RECOGNITION_HANDLER = 1043;

	public final static int MSG_RESPONSE_FUSED_LOCATION_HANDLER = 1044;
	public final static int MSG_RESPONSE_PERMISSION_HANDLER = 1045;
	public final static int MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER = 1046;

	public final static int MSG_RESPONSE_AMXDATA_TRANSMIT_HANDLER = 1050;
	public final static int MSG_RESPONSE_AMXBROADCAST_TRANSMIT_HANDLER = 1049;
	
	public final static int MSG_RESPONSE_AMX_SYSTEMPOWER_HANDLER = 1051;
	public final static int MSG_RESPONSE_AMX_MODE_HANDLER = 1052;
	public final static int MSG_RESPONSE_AMX_MATRIX_HANDLER = 1053;
	public final static int MSG_RESPONSE_AMX_PROJECT_HANDLER = 1054;
	public final static int MSG_RESPONSE_AMX_VOLUME_HANDLER = 1055;
	public final static int MSG_RESPONSE_AMX_CURTAIN_HANDLER = 1056;
	public final static int MSG_RESPONSE_AMX_LIGHT_HANDLER = 1057;
	public final static int MSG_RESPONSE_AMX_BDPLAYER_HANDLER = 1058;
	
	public final static int MSG_RESPONSE_VOICE_RECOGNITION_HANDLER = 1070;
	
	public final static int MSG_RESPONSE_PRESENTATION_HELPER_HANDLER = 1090;
}
