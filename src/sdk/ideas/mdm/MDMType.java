package sdk.ideas.mdm;

public abstract class MDMType
{
	public final static int DISABLE = -1;
	public final static int ENABLE  =  0;

	public final static int REQUEST_CODE_ENABLE_ADMIN = 3333;
	public final static int REQUEST_CODE_INSTALL_APP   = 3334;
	public final static int REQUEST_CODE_UNINSTALL_APP = 3335;
	public final static int CONTROL_CAMERA 		 = 1;
	public final static int CONTROL_WIFI 		 = 2;
	public final static int CONTROL_BANUNINSTALL = 3;
	public final static String MDM_APP_INIT = "MDM_APP_INIT.data";
	public final static String MDM_SDCARD_INIT = "MDM_SDCARD_INIT.data";

}
