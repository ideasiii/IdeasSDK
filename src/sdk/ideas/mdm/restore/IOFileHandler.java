package sdk.ideas.mdm.restore;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import android.content.Context;
import android.os.Environment;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.applist.ApplicationList.AppInfo;
import sdk.ideas.mdm.restore.RecordAllDataInfo.FileData;

public class IOFileHandler
{
	
	/**
	 *save in /data/data/MDM/files/filename.data 
	 * */
	
	public static void writeToInternalFile(Context mContext, String fileName, ArrayList<String> datas)
	{
		try
		{
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					mContext.openFileOutput(fileName, Context.MODE_PRIVATE));

			for (int i = 0; i < datas.size(); i++)
			{
				outputStreamWriter.write(datas.get(i) + "\n");
			}

			outputStreamWriter.close();
		}
		catch (IOException e)
		{
			Logs.showTrace("File write failed: " + e.toString());
		}
		catch (Exception e)
		{
			Logs.showTrace(e.toString());
		}

	}
	/**
	 *load from /data/data/MDM/files/filename.data 
	 * */
	public static ArrayList<String> readFromInternalFile(Context mContext, String fileName)
	{
		ArrayList<String> data = new ArrayList<String>();
		try
		{
			InputStream inputStream = mContext.openFileInput(fileName);

			if (inputStream != null)
			{
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";

				while ((receiveString = bufferedReader.readLine()) != null)
				{
					data.add(receiveString);
				}

				inputStream.close();
			}
		}
		catch (FileNotFoundException e)
		{
			Logs.showTrace("File not found: " + e.toString());
		}
		catch (IOException e)
		{
			Logs.showTrace("Can not read file: " + e.toString());
		}
		catch (Exception e)
		{
			Logs.showTrace(e.toString());
		}
		return data;

	}
	
	
	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable()
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			return true;
		}
		return false;
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable()
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		{
			return true;
		}
		return false;
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
	public static ArrayList<String> AppInfoConvertToArrayListString( ArrayList<AppInfo>  tmp)
	{
		ArrayList <String> data = new ArrayList<String > ();
		for(int i = 0;i<tmp.size();i++)
		{
			data.add(tmp.get(i).appPackageName);
		}
		return data;
	}
	
}
