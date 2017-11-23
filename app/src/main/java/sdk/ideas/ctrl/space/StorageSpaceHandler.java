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
	private long checkTime = 60000;

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
	public void setCheckTime(long millisecondTime)
	{
		if(millisecondTime > 0)
			this.checkTime = millisecondTime;

	}

	/**
	 * diffStorageSpace: MB
	 * */
	public void setDiffStorageSpace(int diffStorageSpaceMB)
	{
		if(diffStorageSpaceMB > 0)
			this.diffStorageSpace = diffStorageSpaceMB;
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
			// for external memory
			try
			{
				double externalMem = IOFileHandler.getAvailableExternalMemorySize(false);
				
				if (Math.abs(externalMemOLD - externalMem) > diffStorageSpace)
				{
					double totalExternalMem = IOFileHandler.getTotalExternalMemorySize(false);
					if (totalExternalMem != -1)
					{
						//using debug
						//Logs.showTrace("externalMem"+ String.valueOf(externalMem)+" totalExternalMem"+String.valueOf(totalExternalMem));
						
						message.put("message", "success");
						message.put("availablememory", String.valueOf(externalMem));
						message.put("totalmemory", String.valueOf(totalExternalMem));
						//message.put("externalMemory", String.valueOf((externalMem * 100) / totalExternalMem));
						
						callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_STORAGE_SPACE_HANDLER, ResponseCode.METHOD_EXTERNAL_MEMORY, message);
					
						externalMemOLD = externalMem;
					}
					else
					{
						message.put("message", "the external memory not available");
						callBackMessage(ResponseCode.ERR_EXTERNAL_MEMORY_UNAVAILABLE,CtrlType.MSG_RESPONSE_STORAGE_SPACE_HANDLER, ResponseCode.METHOD_EXTERNAL_MEMORY, message);
					}
				}
			}
			catch (Exception e)
			{
				message.put("message", e.toString());
				callBackMessage(ResponseCode.ERR_UNKNOWN,CtrlType.MSG_RESPONSE_STORAGE_SPACE_HANDLER, ResponseCode.METHOD_EXTERNAL_MEMORY,message);
			}
			finally
			{
				message.clear();
			}
			
			// for removable external memory
			
			try
			{
				//Logs.showTrace("start get removableExternalMem");
				double removableExternalMem = IOFileHandler.getAvailableRemovableExternalMemorySize(false);
				//Logs.showTrace("end get removableExternalMem : "+String.valueOf(removableExternalMem));
				if (Math.abs(removableExternalMemOLD - removableExternalMem) >= diffStorageSpace)
				{
					
					double totalRemovableExternalMem = IOFileHandler.getTotalRemovableExternalMemorySize(false);
					
					if (totalRemovableExternalMem != -1)
					{
						message.put("message", "success");
						message.put("availablememory", String.valueOf(removableExternalMem));
						message.put("totalmemory", String.valueOf(totalRemovableExternalMem));
						
						
						//message.put("removableExternalMemory",
						//		String.valueOf((removableExternalMem * 100) / totalRemovableExternalMem));
						callBackMessage(ResponseCode.ERR_SUCCESS,CtrlType.MSG_RESPONSE_STORAGE_SPACE_HANDLER,
								ResponseCode.METHOD_REMOVABLE_EXTERNAL_MEMORY, message);

						removableExternalMemOLD = removableExternalMem;
					}
				}
			}
			catch (Exception e)
			{
				message.put("message", e.toString());
				callBackMessage(ResponseCode.ERR_UNKNOWN,CtrlType.MSG_RESPONSE_STORAGE_SPACE_HANDLER, ResponseCode.METHOD_REMOVABLE_EXTERNAL_MEMORY, message);
			}
			finally
			{
				message.clear();
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
