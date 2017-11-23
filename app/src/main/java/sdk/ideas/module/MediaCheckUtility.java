package sdk.ideas.module;

import java.io.File;
import java.io.IOException;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaRecorder;
import android.speech.RecognizerIntent;

public class MediaCheckUtility
{
	// returns whether a microphone exists
	public static boolean getMicrophoneExists(Context context)
	{
		boolean available = false;
		try
		{
			PackageManager packageManager = context.getPackageManager();
			available = packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
		}
		catch (Exception e)
		{
			available = false;
		}
		return available;
	}
	// returns whether the microphone is available
	public static boolean getMicrophoneAvailable(Context context)
	{
		boolean available = true;
		try
		{
			MediaRecorder recorder = new MediaRecorder();
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			recorder.setOutputFile(new File(context.getCacheDir(), "MediaUtil#micAvailTestFile").getAbsolutePath());

			recorder.prepare();
			recorder.release();
		}
		catch (IOException exception)
		{
			available = false;
		}
		return available;
	}

	// returns whether text to speech is available
	public static boolean getSpeechRecognizeAvailable(Context context)
	{
		boolean available = false;
		try
		{
			PackageManager packageManager = context.getPackageManager();
			Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			List<ResolveInfo> speechActivities = packageManager.queryIntentActivities(speechIntent, 0);
			if (speechActivities.size() != 0)
			{
				available = true;
			}
		}
		catch (Exception e)
		{
			available = false;
		}
		
		return available;
	}
}