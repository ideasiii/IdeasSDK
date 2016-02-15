package sdk.ideas.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import sdk.ideas.mdm.applist.ApplicationList.AppInfo;

public class ArrayListUtility
{
	public static boolean findContainAndRemove(ArrayList<String> data,String keyword)
	{
		Iterator<String> iter = data.iterator();
        while(iter.hasNext())
        {
            if(iter.next().contains(keyword))
            {
            	iter.remove();
            	return true;
            }
        }
		return false;
		
	}
	
	
	public static ReturnColectionData ArrayListDifference(ArrayList<String> a,ArrayList<String> b)
	{
		Collection<String> ca = new ArrayList<String> (a);
		Collection<String> cb = new ArrayList<String> (b);
		
		ca.removeAll(b);
		cb.removeAll(a);
		
		ReturnColectionData returnValue = new ReturnColectionData(ca,cb);
		return returnValue;
	}
	
	public static ArrayList<String> FileDataConvertToArrayListString(ArrayList<FileData> tmp)
	{
		ArrayList <String> data = new ArrayList<String > ();
		for(int i = 0;i<tmp.size();i++)
		{
			data.add(tmp.get(i).filePath);
		}
		return data;
	}
	public static ArrayList<String> AppInfoConvertToArrayListString( ArrayList<AppInfo> tmp)
	{
		ArrayList <String> data = new ArrayList<String > ();
		for(int i = 0;i<tmp.size();i++)
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
