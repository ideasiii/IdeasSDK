package sdk.ideas.mdm.lock;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import sdk.ideas.common.Logs;

public class ControlLockStatusBar
{
	private static WindowManager manager = null;
	private static CustomViewGroup view = null;
	private static Context mContext = null;
	private static WindowManager.LayoutParams localLayoutParams = null;
	private static boolean isLockStatusBar = false;

	public static void controlLockStatusBarInit(final Context context)
	{
		mContext = context;
		manager = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
		view = new CustomViewGroup(context);
		localLayoutParams = new WindowManager.LayoutParams();
	}
	
	
	private static void fullSceen(Context context)
	{
		// Remove title bar
		((Activity) context).requestWindowFeature(Window.FEATURE_NO_TITLE); 
		
		// Remove notification bar
		((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	public static void preventStatusBarExpansion()
	{
		localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		localLayoutParams.gravity = Gravity.TOP;
		localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
				// this is to enable the notification to recieve touch events
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
				// Draws over status bar
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		localLayoutParams.height = (int) (50 * mContext.getResources().getDisplayMetrics().scaledDensity);
		localLayoutParams.format = PixelFormat.TRANSPARENT;

		manager.addView(view, localLayoutParams);
		
		isLockStatusBar = true;
	}

	public static void unLockStatusBarExpansion()
	{
		if (isLockStatusBar == true)
		{
			if (null != manager && null != view)
			{
				try
				{
					manager.removeView(view);
					isLockStatusBar = false;
				}
				catch (Exception e)
				{
					Logs.showTrace(e.toString());
				}
			}
		}
	}

	public static class CustomViewGroup extends ViewGroup
	{

		public CustomViewGroup(Context context)
		{
			super(context);
		}

		@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b)
		{
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev)
		{
			Logs.showTrace("customViewGroup\n**********Intercepted");
			return true;
		}
	}

}
