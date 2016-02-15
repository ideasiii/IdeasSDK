
package sdk.ideas.mdm.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.common.ReturnIntentAction;
import sdk.ideas.mdm.MDMType;

public class ApplicationHandler extends BaseHandler
{
	private Context mContext = null;
	private PackageReceiver receiver = null;
	private ArrayList<String> installingPackage = null;
	private ArrayList<String> uninstallingPackage = null;

	public ApplicationHandler(Context context)
	{
		super(context);
		mContext = context;
		installingPackage = new ArrayList<String>();
		uninstallingPackage = new ArrayList<String>();
		listenPackageAction();
	}

	/**
	 * use thread to intstall
	 */
	public void installApplicationThread(String url, String apkName)
	{

		Thread install = new Thread(new InstallAppRunnable(url, apkName));
		install.start();

	}

	/**
	 * Installs an application to the device this method need use Thread run or
	 * will cause block main thread error
	 * 
	 * @param url
	 *            download
	 * @param apkName
	 *            fileName
	 */
	public void installApplication(String url, String apkName)
	{
		boolean anyError = true;
		try
		{
			InstallApp.installApplication(mContext, url, MDMType.MDM_PROFILE_DOWNLOAD_TEMPORARY_PATH, apkName,
					installingPackage);
			anyError = false;
		}
		catch (MalformedURLException e)
		{
			setResponseMessage(ResponseCode.ERR_MALFORMED_URL_EXCEPTION, e.toString());
		}
		catch (ProtocolException e)
		{
			setResponseMessage(ResponseCode.ERR_PROTOCOL_EXCEPTION, e.toString());
		}
		catch (IOException e)
		{
			setResponseMessage(ResponseCode.ERR_IO_EXCEPTION, e.toString());
		}
		catch (Exception e)
		{
			setResponseMessage(ResponseCode.ERR_UNKNOWN, e.toString());
		}
		finally
		{
			if (anyError == true)
			{
				returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
						ResponseCode.METHOD_APPLICATION_INSTALL_SYSTEM);
			}
		}

	}
	
	
	private boolean isAppInstalled(String packageName)
	{
		PackageManager pm = mContext.getPackageManager();
		boolean installed = false;
		try
		{
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			installed = true;
		}
		catch (PackageManager.NameNotFoundException e)
		{
			installed = false;
		}
		return installed;
	}

	/**
	 * Uninstalls an application from the device
	 * 
	 * @param url
	 *            - Application package name should be passed in as a String
	 */
	public void unInstallApplication(String packageName)// Specific package Name
	{
		uninstallingPackage.add(packageName);

		if (UninstallApp.unInstallApplication(mContext, packageName) == false)
		{
			ArrayListUtility.findContainAndRemove(uninstallingPackage, packageName);

			setResponseMessage(ResponseCode.ERR_PACKAGE_NOT_FIND, "not find the package which need to uninstall");

			returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
					ResponseCode.METHOD_APPLICATION_UNINSTALL_SYSTEM);
		}
	}

	public void listenPackageAction()
	{
		IntentFilter filter = new IntentFilter();

		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);

		filter.addDataScheme("package");
		receiver = new PackageReceiver();
		mContext.registerReceiver(receiver, filter);
		receiver.setOnReceiverListener(new ReturnIntentAction()
		{
			@Override
			public void returnIntentAction(HashMap<String, String> action)
			{
				String appAction = action.get("Action");
				String packageName = action.get("PackageName");
				if (null != listener)
				{
					if (appAction.equals("android.intent.action.PACKAGE_ADDED"))
					{
						if (ArrayListUtility.findContainAndRemove(installingPackage, packageName))
						{

							setResponseMessage(ResponseCode.ERR_SUCCESS, packageName);
							returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_INSTALL_SYSTEM);
						}
						else
						{
							setResponseMessage(ResponseCode.ERR_SUCCESS, packageName);
							returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_INSTALL_USER);
						}
					}
					else if (appAction.equals("android.intent.action.PACKAGE_REMOVED"))
					{
						if (ArrayListUtility.findContainAndRemove(uninstallingPackage, packageName))
						{
							setResponseMessage(ResponseCode.ERR_SUCCESS, packageName);
							returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_UNINSTALL_SYSTEM);
						}
						else
						{
							setResponseMessage(ResponseCode.ERR_SUCCESS, packageName);
							returnRespose(mResponseMessage, MDMType.MDM_MSG_RESPONSE_APPLICATION_HANDLER,
									ResponseCode.METHOD_APPLICATION_UNINSTALL_USER);
						}
					}
				}
			}
		});

	}

	public void stopListenAppAction()
	{
		if (null != receiver)
			mContext.unregisterReceiver(receiver);
	}

	private class InstallAppRunnable implements Runnable
	{
		private String uRLPath = null;
		private String fileName = null;

		@Override
		public void run()
		{
			installApplication(uRLPath, fileName);
		}

		public InstallAppRunnable(String uRLPath, String fileName)
		{
			this.fileName = fileName;
			this.uRLPath = uRLPath;
		}

	}

	/**
	 * important test
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void test() throws FileNotFoundException, IOException
	{
		ArrayList<String> tmp = IOFileHandler.readFromInternalFile(mContext, MDMType.INIT_LOCAL_MDM_APP_PATH);
		try
		{
			IOFileHandler.writeToExternalFile(null, "app_init.txt", tmp);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try
		{
			IOFileHandler.writeToExternalFile(null, "sdcard_file_path_record.txt",
					IOFileHandler.readFromInternalFile(mContext, MDMType.INIT_LOCAL_MDM_SDCARD_PATH));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
