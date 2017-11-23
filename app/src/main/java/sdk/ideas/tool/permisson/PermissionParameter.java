package sdk.ideas.tool.permisson;

public class PermissionParameter
{
	public static final int PERMISSION_GRANT = 1;
	public static final int PERMISSION_DENIED = 0;
	public static final int PERMISSION_NEVER_ASK_AGAIN = -1;
	
	public String permissionName = null;
	public int permissionState = 0;
	
	public PermissionParameter(String permissionName, int permissionState)
	{
		this.permissionName = permissionName;
		this.permissionState = permissionState;
	}
	
	
}
