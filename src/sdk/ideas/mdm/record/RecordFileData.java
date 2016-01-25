package sdk.ideas.mdm.record;


import java.io.File;
import java.util.ArrayList;
import android.content.Context;
import android.os.Environment;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.mdm.MDMType;
import sdk.ideas.common.ArrayListUtility;
import sdk.ideas.common.ArrayListUtility.FileData;

public class RecordFileData implements Runnable
{

	private ArrayList<FileData> datas = null;
	private boolean isInit = true;
	private ArrayList<String> particularlyPath = null;
	private Context mContext  =null;
	@Override
	public void run()
	{
	
		if (isInit == true)
		{
			//create data list
			recordAllFilePath();
			
			//add writes data
			String SDCardPath = Environment.getExternalStorageDirectory().toString();
			File f = new File(SDCardPath + "/MDM");
			if (!f.exists())
				f.mkdir();
			
			IOFileHandler.writeToInternalFile(mContext ,MDMType.INIT_LOCAL_MDM_SDCARD_PATH, ArrayListUtility.FileDataConvertToArrayListString(datas));
			
			/*ArrayList<String > tmp = IOFileHandler.readFromInternalFile(mContext, "MDM_INIT.data");
			for (int i = 0; i < tmp.size(); i++)
			{
				if(tmp.get(i).contains("Download"))
					Logs.showTrace(tmp.get(i));
			}
			Logs.showTrace(String.valueOf(tmp.size()));*/
		}
		else
		{
			recordAllFilePath();
			try
			{
				IOFileHandler.writeToExternalFile("Download/", "fileList.txt", ArrayListUtility.FileDataConvertToArrayListString(datas));
				
			}
			catch (Exception e)
			{
				Logs.showTrace(e.toString());
			}
		}	
		
	}
	
	public RecordFileData(Context context,boolean isInit)
	{
		mContext = context;
		datas = new ArrayList<FileData>();
		this.isInit = isInit;
	}
	
	public void setScanPath(ArrayList<String> particularlyPath)
	{
		if(null!=particularlyPath)
			this.particularlyPath = particularlyPath;
	}
	
	
	
	/**
	 * record all file data info
	 * */
	private void recordAllFilePath()
	{
		if (IOFileHandler.isExternalStorageReadable())
		{
			String path = Environment.getExternalStorageDirectory().toString();
			scanDIR(datas, path);

			Logs.showTrace("total data numbers" + String.valueOf(datas.size()));
		}
	}
	private void scanDIR(ArrayList<FileData> datas, String path)
	{
		File f = new File(path);
		File file[] = f.listFiles();
		for (int i = 0; i < file.length; i++)
		{
			if (file[i].isDirectory() == true)
			{
				datas.add(new FileData(file[i].getName(), file[i].getPath(), true));
				scanDIR(datas, file[i].getPath());
			}
			else
			{
				datas.add(new FileData(file[i].getName(), file[i].getPath(), false));
			}
		}

	}
	
}
