package sdk.ideas.ctrl.space;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.IOFileHandler;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

public class StorageSpaceHandler extends BaseHandler implements ListenReceiverAction
{
	// default 1 min check
	private int checkTime = 60000;

	// default 50 mb update data
	private int diffStorageSpace = 50;
	private Timer timer = null;

	private double externalMemOLD = 0;
	private double removableExternalMemOLD = 0;
	private boolean isStorageSpaceOn = false;

	public StorageSpaceHandler(Context context)
	{
		super(context);
		timer = new Timer();

	}
	/**
	 * checkTime : millisecond
	 * */
	public void setCheckTime(int checkTime)
	{
		this.checkTime = checkTime;

	}

	/**
	 * diffStorageSpace: MB
	 * */
	public void setDiffStorageSpace(int diffStorageSpace)
	{
		this.diffStorageSpace = diffStorageSpace;
	}

	public class CheckSpaceTimer extends TimerTask
	{
		private int diffStorageSpace;

		private HashMap<String, String> message = null;

		public CheckSpaceTimer(int diffStorageSpace)
		{
			this.diffStorageSpace = diffStorageSpace;
			message = new HashMap<String, String>();
		}

		public void run()
		{
			Logs.showTrace("in run");
			message.clear();
			double externalMem = IOFileHandler.getAvailableExternalMemorySize(false);
			double removableExternalMem = IOFileHandler.getAvailableRemovableExternalMemorySize(false);

			// for external memory
			if (Math.abs(externalMemOLD - externalMem) > diffStorageSpace)
			{
				double totalExternalMem = IOFileHandler.getTotalExternalMemorySize(false);
				if (totalExternalMem != -1)
				{
					message.put("externalMemory", String.valueOf((externalMem * 100) / totalExternalMem));
					setResponseMessage(ResponseCode.ERR_SUCCESS, message);
					returnRespose(CtrlType.MSG_RESPONSE_STORAGE_SPACE_HANDLER,
							ResponseCode.METHOD_EXTERNAL_MEMORY);

					externalMemOLD = externalMem;
					message.clear();
				}
			}
			// for removable external memory
			if (Math.abs(removableExternalMemOLD - removableExternalMem) >= diffStorageSpace)
			{
				double totalRemovableExternalMem = IOFileHandler.getTotalRemovableExternalMemorySize(false);
				if (totalRemovableExternalMem != -1)
				{
					message.put("removableExternalMemory",
							String.valueOf((removableExternalMem * 100) / totalRemovableExternalMem));
					setResponseMessage(ResponseCode.ERR_SUCCESS, message);
					returnRespose(CtrlType.MSG_RESPONSE_STORAGE_SPACE_HANDLER,
							ResponseCode.METHOD_REMOVABLE_EXTERNAL_MEMORY);

					removableExternalMemOLD = removableExternalMem;
					message.clear();

				}
			}
		}
	}

	@Override
	public void startListenAction()
	{
		if (isStorageSpaceOn == false)
		{
			isStorageSpaceOn = true;
			timer.schedule(new CheckSpaceTimer(diffStorageSpace), 0, checkTime);
		}
	}

	@Override
	public void stopListenAction()
	{
		if (isStorageSpaceOn == true)
		{
			isStorageSpaceOn = false;
			timer.cancel();
		}
	}
}