package sdk.ideas.tool.premisson;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

public class RuntimePermissionHandler extends BaseHandler
{
	private Activity mActivity = null;

	// key: permission
	// ex: key:Manifest.permission.GET_ACCOUNTS

	private ArrayList<PremissionParameter> permissionData = null;

	public RuntimePermissionHandler(Activity mActivity, ArrayList<String> permissionsData)
	{
		super(mActivity);
		if (null != mActivity && null != permissionsData)
		{
			
			this.mActivity = mActivity;
			init(permissionsData);
		}

	}

	private void init(ArrayList<String> permissionsArrayData)
	{
		permissionData = new ArrayList<PremissionParameter>();

		for (int i = 0; i < permissionsArrayData.size(); i++)
		{
			permissionData
					.add(new PremissionParameter(permissionsArrayData.get(i), PremissionParameter.PERMISSION_DENIED));
		}

	}

	public void startRequestPermissions()
	{
		checkPermissions();
		requestPermission();
	}

	private void checkPermissions()
	{
		if (null != permissionData && permissionData.size() != 0)
		{
			for (int i = 0; i < permissionData.size(); i++)
			{
				if (ContextCompat.checkSelfPermission(mActivity,
						permissionData.get(i).permissionName) == PackageManager.PERMISSION_DENIED)
				{
					
						if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
								permissionData.get(i).permissionName) == false)
						{
							permissionData.get(i).permissionState = PremissionParameter.PERMISSION_NEVER_ASK_AGAIN;
						}
						else
						{
							permissionData.get(i).permissionState = PremissionParameter.PERMISSION_DENIED;
						}
					
				}
				else
				{
					permissionData.get(i).permissionState = PremissionParameter.PERMISSION_GRANT;
				}
			}

		}
	}

	public void print()
	{
		for (int i = 0; i < permissionData.size(); i++)
		{
			Logs.showTrace("PermissionName: " + permissionData.get(i).permissionName + " PermissionState: "
					+ permissionData.get(i).permissionState);
		}
	}

	private void requestPermission()
	{
		ArrayList<String> requestPermissions = new ArrayList<String>();
		for (int i = 0; i < permissionData.size(); i++)
		{
			if (permissionData.get(i).permissionState != PremissionParameter.PERMISSION_GRANT)
			{
				requestPermissions.add(permissionData.get(i).permissionName);
			}
		}

		if (requestPermissions.size() != 0)
		{
			String[] requestPermissionsString = requestPermissions.toArray(new String[requestPermissions.size()]);
			ActivityCompat.requestPermissions(mActivity, requestPermissionsString,
					CtrlType.REQUEST_CODE_RUNTIME_PERMISSIONS);
		}
		else
		{
			onRequestPermissionsResult(CtrlType.REQUEST_CODE_RUNTIME_PERMISSIONS, null,null);
		}
	}

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		if (requestCode == CtrlType.REQUEST_CODE_RUNTIME_PERMISSIONS)
		{
			try
			{
				checkPermissions();
				HashMap<String,String> message= new HashMap<String,String>();
				for(int i=0;i<this.permissionData.size();i++)
				{
					message.put(permissionData.get(i).permissionName, String.valueOf(permissionData.get(i).permissionState));
				}
				super.callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_PERMISSION_HANDLER , 0, message);
				
				
			}
			catch (Exception e)
			{
				Logs.showTrace(e.toString());
			}
		}
	}
}
