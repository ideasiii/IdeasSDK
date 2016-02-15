package sdk.ideas.mdm.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MDMService extends Service
{
	public static int counter = 0;

	public MDMService() {

    }

	@Override
	public IBinder onBind(Intent intent)
	{
		return new Binder();
	}

	@Override
	public void onCreate()
	{
		Toast.makeText(this, "First Service was Created", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStart(Intent intent, int startId)
	{

		counter++;
		Toast.makeText(this, " First Service Started" + "  " + counter, Toast.LENGTH_SHORT).show();
		if(counter>3)
		onTaskRemoved(intent);
	}

	@Override
	public void onDestroy()
	{
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
	}

	public void onTaskRemoved (Intent rootIntent)
	{

        this.stopSelf();
      }
}