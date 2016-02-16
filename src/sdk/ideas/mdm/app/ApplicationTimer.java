package sdk.ideas.mdm.app;

public class ApplicationTimer
{
	private final int defaultWaitInstallTime = 60000;
	private final int defaultWaitUnInstallTime = 60000;
	private int waitInstallTime;
	private int waitUnInstallTime;

	public ApplicationTimer(int waitInstallTime, int waitUnInstallTime)
	{
		this.waitInstallTime = waitInstallTime;
		this.waitUnInstallTime = waitUnInstallTime;
	}
	public ApplicationTimer()
	{
		waitInstallTime = defaultWaitInstallTime;
		waitUnInstallTime = defaultWaitUnInstallTime;
	}
	
	public void setOnInstallAppTimer(String packageName,String apkSavePath)
	{
		
	}
	
	public void setOnUnInstallAppTimer(String packageName)
	{
		
		
	}
	
	
	
	
	
	
	
	
	

}
