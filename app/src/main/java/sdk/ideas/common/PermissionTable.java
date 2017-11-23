package sdk.ideas.common;

public abstract class PermissionTable
{
	public final static String ACCESS_NETWORK_STATE 			= android.Manifest.permission.ACCESS_NETWORK_STATE;
	public final static String ACCESS_WIFI_STATE 				= android.Manifest.permission.ACCESS_WIFI_STATE;
	public final static String ACCESS_FINE_LOCATION 			= android.Manifest.permission.ACCESS_FINE_LOCATION;
	public final static String ACCESS_COARSE_LOCATION 			= android.Manifest.permission.ACCESS_COARSE_LOCATION;
	public final static String ACCESS_LOCATION_EXTRA_COMMANDS	= android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS;
	public final static String READ_PHONE_STATE 				= android.Manifest.permission.READ_PHONE_STATE;
	public final static String GET_ACCOUNTS 					= android.Manifest.permission.GET_ACCOUNTS;
	public final static String SYSTEM_ALERT_WINDOW 				= android.Manifest.permission.SYSTEM_ALERT_WINDOW;
	public final static String INTERNET 						= android.Manifest.permission.INTERNET;
	public final static String READ_EXTERNAL_STORAGE 			= android.Manifest.permission.READ_EXTERNAL_STORAGE;
	public final static String WRITE_EXTERNAL_STORAGE 			= android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
	public final static String REORDER_TASKS 					= android.Manifest.permission.REORDER_TASKS;
	public final static String BLUETOOTH 						= android.Manifest.permission.BLUETOOTH;
	public final static String BLUETOOTH_ADMIN 					= android.Manifest.permission.BLUETOOTH_ADMIN;
	
	public final static String[] TRACKER = { INTERNET, ACCESS_FINE_LOCATION, ACCESS_WIFI_STATE, ACCESS_COARSE_LOCATION,
			ACCESS_NETWORK_STATE, READ_PHONE_STATE, GET_ACCOUNTS, ACCESS_LOCATION_EXTRA_COMMANDS };
	
}
