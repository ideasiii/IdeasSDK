package sdk.ideas.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import android.content.Context;
import android.os.Environment;

public class IOFileHandler
{

	/**
	 * save in /data/data/apk_name/files/filename.data
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */

	public static void writeToInternalFile(Context mContext, String fileName, ArrayList<String> datas) throws IOException,FileNotFoundException
	{
		
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
					mContext.openFileOutput(fileName, Context.MODE_PRIVATE));

			for (int i = 0; i < datas.size(); i++)
			{
				if (i == datas.size() - 1)
					outputStreamWriter.write(datas.get(i));
				else
					outputStreamWriter.write(datas.get(i) + "\n");
			}

			outputStreamWriter.close();
		

	}

	/**
	 * load from /data/data/apk_name/files/filename.data
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	public static ArrayList<String> readFromInternalFile(Context mContext, String fileName) throws FileNotFoundException,IOException
	{
		ArrayList<String> data = new ArrayList<String>();
		
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
		
		return data;

	}

	/**
	 * Checks if external storage is available for read and write
	 */
	public static boolean isExternalStorageWritable()
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			return true;
		}
		return false;
	}

	/**
	 * Checks if external storage is available to at least read
	 */
	public static boolean isExternalStorageReadable()
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		{
			return true;
		}
		return false;
	}

	/**
	 * write in external space writeToSDCardPath: you do not need to add
	 * External Storage Directory Absolute Path, just simple ex. save to
	 * download ==> writeToSDCardPath = "Download/"
	 * 
	 * @throws FileNotFoundException,IOException
	 */
	public static void writeToExternalFile(String writeToSDCardPath, String fileName, ArrayList<String> datas)
			throws FileNotFoundException, IOException
	{
		// get the path to sdcard
		File sdcard = Environment.getExternalStorageDirectory();
		// to this path add a new directory path
		if (isExternalStorageWritable() == false)
		{
			return;
		}
		File dir = null;
		if (null != writeToSDCardPath)
		{
			dir = new File(sdcard.getAbsolutePath() + "/" + writeToSDCardPath);
			// create this directory if not already created
			dir.mkdir();
		}
		else
		{
			dir = new File(sdcard.getAbsolutePath() + "/");
		}
		// create the file in which we will write the contents
		if (null != fileName && null != datas)
		{
			File file = new File(dir, fileName);
			FileOutputStream os = new FileOutputStream(file);
			for (int i = 0; i < datas.size(); i++)
			{
				os.write(datas.get(i).getBytes());
				if (i != datas.size() - 1)
					os.write((new String("\n")).getBytes());
			}
			os.close();
		}
	}

	/**
	 * filePath: you do not need to add External Storage Directory Absolute
	 * Path, just simple ex. read from Download ==> filePath = "Download/"
	 * return null if can not find data
	 * 
	 * @throws IOException,FileNotFoundException
	 */

	public static ArrayList<String> readFromExteralFile(String filePath, String fileName)
			throws IOException, FileNotFoundException
	{
		ArrayList<String> data = new ArrayList<String>();
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath();

		// Get the text file
		File file = new File(dir + "/" + filePath, fileName);

		if (file.exists())
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			
			while ((line = br.readLine()) != null)
			{
				data.add(line);
			}
			br.close();
		}
		else
		{
			return null;
		}
		return data;
	}

	public static String getExternalStorageDirectory()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * use this method need to thread run ,or if you will have blocking main
	 * thread exception; write in external space savePath: you do not need to
	 * add External Storage Directory Absolute Path, just simple, ex. save to SD
	 * Card download ==> savePath = "Download/"
	 * @throws MalformedURLException  
	 * @throws ProtocolException 
	 * @throws IOException 
	 * 
	 * 
	 */
	public static boolean urlDownloader(String uRLPath, String savePath, String fileName) throws MalformedURLException, ProtocolException,IOException
	{
		
			URL url = new URL(uRLPath);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();

			Logs.showTrace("uRLPath: " + uRLPath);
			Logs.showTrace("fileName: " + fileName);
			Logs.showTrace("savePath: " + savePath);

			Logs.showTrace("now downloading");
			File file = new File(IOFileHandler.getExternalStorageDirectory() + "/" + savePath);
			file.mkdirs();
			File outputFile = new File(file, fileName);
			if (outputFile.exists())
			{
				outputFile.delete();
			}

			FileOutputStream fos = new FileOutputStream(outputFile);

			InputStream is = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1)
			{
				fos.write(buffer, 0, len1);
			}
			fos.close();
			is.close();
			Logs.showTrace("download finish");
		
		return true;

	}

	public static boolean deleteFile(String needToDeleteFile)
	{
		File tmp = new File(needToDeleteFile);
		return tmp.delete();
	}

	public static boolean isDirectory(String filePath)
	{
		File tmp = new File(filePath);
		return tmp.isDirectory();
	}

	public static boolean deleteFileList(ArrayList<String> needToDeleteFileList)
	{
		boolean deleteOK = true;
		Logs.showTrace("need to delete number: " + String.valueOf(needToDeleteFileList.size()));
		
		 /*use for debuging 
		 Logs.showTrace("<<<need To Delete File List>>>");
		 
		 for(int i=0;i<needToDeleteFileList.size();i++)
		 {
			 Logs.showTrace(needToDeleteFileList.get(i)); 
		 }*/
		 
		ArrayList<String> needToDeleteDir = new ArrayList<String>();

		for (int i = 0; i < needToDeleteFileList.size(); i++)
		{

			if (IOFileHandler.isDirectory(needToDeleteFileList.get(i)))
			{
				needToDeleteDir.add(needToDeleteFileList.get(i));
			}
			else
			{
				if (false == IOFileHandler.deleteFile(needToDeleteFileList.get(i)))
				{
					Logs.showTrace(" delete file fail :" + needToDeleteFileList.get(i));
					deleteOK = false;
				}
			}
		}
		for (int i = 0; i < needToDeleteDir.size(); i++)
		{
			if (false == IOFileHandler.deleteFile(needToDeleteDir.get(i)))
			{
				Logs.showTrace("delete dir fail" + needToDeleteFileList.get(i));
				deleteOK = false;
			}
		}
		return deleteOK;
	}

}
