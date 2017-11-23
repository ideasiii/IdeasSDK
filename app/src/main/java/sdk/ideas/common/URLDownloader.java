package sdk.ideas.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;

public class URLDownloader extends AsyncTask<String, Void, Void>
{
	private String uRLPath = null;
	private String savePath = null;
	private String fileName = null;

	public URLDownloader(String uRLPath, String savePath, String fileName)
	{
		this.uRLPath = uRLPath;
		this.savePath = savePath;
		this.fileName = fileName;
	}

	@Override
	protected Void doInBackground(String... params)
	{

		try
		{
			
			Logs.showTrace("uRLPath: "+uRLPath);
			Logs.showTrace("fileName: "+fileName);
			Logs.showTrace("savePath: "+savePath);
			URL url = new URL(uRLPath);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();

			File file = new File(IOFileHandler.getExternalStorageDirectory() + "/" + savePath);
			file.mkdirs();
			Logs.showTrace(String.valueOf(41));
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
			Logs.showTrace(String.valueOf(60));
		}
		catch (Exception e)
		{
			Logs.showTrace(e.getMessage());
		}

		return null;

	}
}
