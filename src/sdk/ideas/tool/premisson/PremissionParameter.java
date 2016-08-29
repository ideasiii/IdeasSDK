package sdk.ideas.tool.premisson;

public class PremissionParameter
{
	public static final int PERMISSION_GRANT = 1;
	public static final int PERMISSION_DENIED = 0;
	public static final int PERMISSION_NEVER_ASK_AGAIN = -1;
	
	public String permissionName = null;
	public int permissionState = 0;
	
	public PremissionParameter(String permissionName,int permissionState)
	{
		this.permissionName = permissionName;
		this.permissionState = permissionState;
	}
	
	
}
