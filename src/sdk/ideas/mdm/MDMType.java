package sdk.ideas.mdm;

public abstract class MDMType
{
	public final static int DISABLE = -1;
	public final static int ENABLE  =  0;

	public final static int REQUEST_CODE_ENABLE_ADMIN  = 3333;
	public final static int REQUEST_CODE_INSTALL_APP   = 3334;
	public final static int REQUEST_CODE_UNINSTALL_APP = 3335;
	
	public final static int CONTROL_CAMERA 		 = 1;
	public final static int CONTROL_WIFI 		 = 2;
	public final static int CONTROL_BANUNINSTALL = 3;
	
	public final static String INIT_LOCAL_MDM_APP_PATH = "MDM_APP_INIT.data";
	public final static String INIT_LOCAL_MDM_SDCARD_PATH = "MDM_SDCARD_INIT.data";
	public final static String URL_MDM_APP_DOWNLOAD = "http://54.199.198.94:8080/app/android/";
	public final static String URL_MDM_PROFILE = "http://54.199.198.94:8080/mdm/profile/";
	public final static String MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH = "Download/";
	public final static String INIT_SERVER_MDM_APP_PATH = "app_init.txt";
	public final static String INIT_SERVER_MDM_SDCARD_PATH = "sdcard_file_path_record.txt";
	public final static String URL_DocumentWebViewer = "https://docs.google.com/gview?embedded=true&url=";
	
	
	public final static int MDM_MSG_RESPONSE      = 1030;
	
	public final static int MDM_MSG_RESPONSE_CAMERA_HANDLER = 1031;
	
	public final static int MDM_MSG_RESPONSE_VOLUME_HANDLER = 1032;
	
	public final static int MDM_MSG_RESPONSE_APPLICATION_HANDLER = 1033;
	
	public final static int MDM_MSG_RESPONSE_RECORD_HANDLER = 1034;
}
