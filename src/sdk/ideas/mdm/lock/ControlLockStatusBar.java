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

	public static void fullSceen(Context context)
	{
		((Activity) context).requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove
																			// title
																			// bar
		((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// Remove
															// notification
															// bar*/
	}

	public static void preventStatusBarExpansion(Context context)
	{
		WindowManager manager = ((WindowManager) context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE));

		WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
		localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		localLayoutParams.gravity = Gravity.TOP;
		localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

		// this is to enable the notification to recieve touch events
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

		// Draws over status bar
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		localLayoutParams.height = (int) (50 * context.getResources().getDisplayMetrics().scaledDensity);
		localLayoutParams.format = PixelFormat.TRANSPARENT;

		CustomViewGroup view = new CustomViewGroup(context);

		manager.addView(view, localLayoutParams);

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
