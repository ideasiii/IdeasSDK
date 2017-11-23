package sdk.ideas.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import sdk.ideas.common.CommonClass.AppData;
import sdk.ideas.common.CommonClass.AppInfo;
import sdk.ideas.common.CommonClass.BluetoothDeviceLinkableDevice;

public class ArrayListUtility
{
	public static boolean findContainAndRemove(ArrayList<String> data, String keyword)
	{
		if(null == data)
			return false;
		Iterator<String> iter = data.iterator();
		while (iter.hasNext())
		{
			if (iter.next().contains(keyword))
			{
				iter.remove();
				return true;
			}
		}
		return false;

	}

	public static boolean findContainAndRemoveForAppDataClass(ArrayList<AppData> data, String keyword)
	{
		if(null == data)
			return false;
		Iterator<AppData> iter = data.iterator();
		while (iter.hasNext())
		{
			if (iter.next().packageName.contains(keyword))
			{
				iter.remove();
				return true;
			}
		}
		return false;

	}

	public static boolean findContainForAppDataClass(ArrayList<AppData> data, String keyword)
	{
		if(null == data)
			return false;
		Iterator<AppData> iter = data.iterator();
		while (iter.hasNext())
		{
			if (iter.next().packageName.contains(keyword))
			{
				return true;
			}
		}
		return false;

	}

	public static boolean findEqualAndRemoveForAppDataClass(ArrayList<AppData> data, String keyword)
	{
		if(null == data)
			return false;
		Iterator<AppData> iter = data.iterator();
		while (iter.hasNext())
		{
			if (iter.next().packageName.equals(keyword))
			{
				iter.remove();
				return true;
			}
		}
		return false;

	}

	public static AppData findEqualForAppDataClass(ArrayList<AppData> data, String keyword)
	{
		if(null == data)
			return null;
		Iterator<AppData> iter = data.iterator();
		while (iter.hasNext())
		{

			AppData tmp = iter.next();
			if (tmp.packageName.equals(keyword))
			{
				return tmp;
			}
		}
		return null;

	}

	public static BluetoothDeviceLinkableDevice findEqualForBluetoothDeviceLinkableDeviceClass(
			ArrayList<BluetoothDeviceLinkableDevice> data, String source)
	{
		if (null != data && null != source)
		{
			Iterator<BluetoothDeviceLinkableDevice> iter = data.iterator();
			while (iter.hasNext())
			{
				BluetoothDeviceLinkableDevice tmp = iter.next();
				if (null != tmp.address && tmp.address.equals(source))
				{
					return tmp;
				}
				else if(null != tmp.name && tmp.name.equals(source))
				{
					return tmp;
				}
			}
		}
		return null;
	}

	public static AppData findEqualForAppDataClass(ArrayList<AppData> data, int appID)
	{
		if(null == data)
			return null;
		Iterator<AppData> iter = data.iterator();
		while (iter.hasNext())
		{
			AppData tmp = iter.next();
			if (tmp.appID == appID)
			{
				return tmp;
			}
		}
		return null;

	}

	public static ReturnColectionData ArrayListDifference(ArrayList<String> a, ArrayList<String> b)
	{
		Collection<String> ca = new ArrayList<String>(a);
		Collection<String> cb = new ArrayList<String>(b);

		ca.removeAll(b);
		cb.removeAll(a);

		ReturnColectionData returnValue = new ReturnColectionData(ca, cb);
		return returnValue;
	}

	public static ArrayList<String> FileDataConvertToArrayListString(ArrayList<FileData> tmp)
	{
		ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; i < tmp.size(); i++)
		{
			data.add(tmp.get(i).filePath);
		}
		return data;
	}

	public static ArrayList<String> AppInfoConvertToArrayListString(ArrayList<AppInfo> tmp)
	{
		ArrayList<String> data = new ArrayList<String>();
		for (int i = 0; i < tmp.size(); i++)
		{
			data.add(tmp.get(i).appPackageName);
		}
		return data;
	}

	public static class FileData
	{
		public String fileName = "";
		public String filePath = "";
		public boolean isDir;
		// public String fileSize = "";

		public FileData(String fileName, String filePath, boolean isDir)
		{
			this.fileName = fileName;
			this.filePath = filePath;
			this.isDir = isDir;
		}

		public void print()
		{
			Logs.showTrace("name: " + fileName);
			Logs.showTrace("path: " + filePath);
			Logs.showTrace("is dir:" + String.valueOf(isDir));
			Logs.showTrace("*******************************");
		}

	}

	public static class ReturnColectionData
	{
		public Collection<String> new_a = null;
		public Collection<String> new_b = null;

		public ReturnColectionData(Collection<String> new_a, Collection<String> new_b)
		{
			this.new_a = new_a;
			this.new_b = new_b;
		}

	}

	

}
