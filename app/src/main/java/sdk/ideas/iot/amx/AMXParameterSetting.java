package sdk.ideas.iot.amx;



public abstract class AMXParameterSetting
{
	/*區別命令TYPE*/
	public final static int TYPE_CONTROL_COMMAND = 0;
	public final static int TYPE_STATUS_COMMAND = 1;

	
	
	/* Define AMX Function */
	public final static int FUNCTION_SYSTEM_POWER = 1;
	public final static int FUNCTION_MODE_SWITCH = 2;
	public final static int FUNCTION_MATRIX_SWITCH = 3;
	public final static int FUNCTION_PROJECT = 4;
	public final static int FUNCTION_VOLUME = 5;
	public final static int FUNCTION_CURTAIN = 6;
	public final static int FUNCTION_LIGHT = 7;
	public final static int FUNCTION_BD_PLAYER = 8;

	/* Define AMX Device */
	// 演講模式
	public final static int DEVICE_MODE_SPEECH = 1;
	// 簡報模式
	public final static int DEVICE_MODE_BRIEF = 2;
	// 劇院模式
	public final static int DEVICE_MODE_CINEMA = 3;

	public final static int DEVICE_PROJECT_LEFT = 1;
	public final static int DEVICE_PROJECT_CENTER = 2;
	public final static int DEVICE_PROJECT_RIGHT = 3;

	public final static int DEVICE_VOLUME_INPUT_1 = 1;
	public final static int DEVICE_VOLUME_INPUT_2 = 2;
	public final static int DEVICE_VOLUME_INPUT_3 = 3;
	public final static int DEVICE_VOLUME_INPUT_4 = 4;
	public final static int DEVICE_VOLUME_INPUT_5 = 5;
	public final static int DEVICE_VOLUME_INPUT_6 = 6;
	public final static int DEVICE_VOLUME_INPUT_7 = 7;
	public final static int DEVICE_VOLUME_INPUT_9 = 8;
	public final static int DEVICE_VOLUME_INPUT_10 = 9;

	public final static int DEVICE_VOLUME_OUTPUT_1 = 10;
	public final static int DEVICE_VOLUME_OUTPUT_2 = 11;
	public final static int DEVICE_VOLUME_OUTPUT_3 = 12;
	public final static int DEVICE_VOLUME_OUTPUT_6 = 13;

	public final static int DEVICE_LIGHT_1 = 1;
	public final static int DEVICE_LIGHT_2 = 2;
	public final static int DEVICE_LIGHT_3 = 3;
	public final static int DEVICE_LIGHT_4 = 4;
	public final static int DEVICE_LIGHT_5 = 5;
	public final static int DEVICE_LIGHT_6 = 6;
	public final static int DEVICE_LIGHT_7 = 7;
	public final static int DEVICE_LIGHT_8 = 8;
	public final static int DEVICE_LIGHT_ALL = 99;

	/* Define AMX Control */
	public final static int CONTROL = 0;
	public final static int CONTROL_ON = 1;
	public final static int CONTROL_OFF = 2;
	public final static int CONTROL_MUTE = 3;
	public final static int CONTROL_UNMUTE = 4;
	public final static int CONTROL_UP = 5;
	public final static int CONTROL_DOWN = 6;
	public final static int CONTROL_PROJECT_HDMI = 7;
	public final static int CONTROL_PROJECT_VGA = 8;

	public final static int CONTROL_MATRIX_INPUT_1 = 9;
	public final static int CONTROL_MATRIX_INPUT_2 = 10;
	public final static int CONTROL_MATRIX_INPUT_3 = 11;
	public final static int CONTROL_MATRIX_INPUT_4 = 12;
	public final static int CONTROL_MATRIX_INPUT_5 = 13;
	public final static int CONTROL_MATRIX_INPUT_6 = 14;
	public final static int CONTROL_MATRIX_INPUT_7 = 15;
	public final static int CONTROL_MATRIX_INPUT_8 = 16;

	public final static int CONTROL_BD_POWER = 17;
	public final static int CONTROL_BD_OPEN = 18;
	public final static int CONTROL_BD_PLAY = 19;
	public final static int CONTROL_BD_STOP = 20;
	public final static int CONTROL_BD_PAUSE = 21;
	public final static int CONTROL_BD_NEXT = 22;
	public final static int CONTROL_BD_PREVIEW = 23;
	public final static int CONTROL_BD_FORWARD = 24;
	public final static int CONTROL_BD_REVIEW = 25;
	public final static int CONTROL_BD_UP = 26;
	public final static int CONTROL_BD_DOWN = 27;
	public final static int CONTROL_BD_LEFT = 28;
	public final static int CONTROL_BD_RIGHT = 29;
	public final static int CONTROL_BD_OK = 30;
	public final static int CONTROL_BD_BACK = 31;
	public final static int CONTROL_BD_MENU = 32;
	public final static int CONTROL_BD_TOPMENU = 33;
	public final static int CONTROL_BD_HOME = 34;
	public final static int CONTROL_BD_SUBTITLE = 35;

	/*智慧裝置端要求 AMX request status*/
	public final static int REQUEST_STATUS_POWER = 1;
	public final static int REQUEST_STATUS_SIGNAL = 2;
	public final static int REQUEST_STATUS_MUTE = 3;
	public final static int REQUEST_STATUS_MATRIX = 4;
	public final static int REQUEST_STATUS_LEVEL = 5;

	/*Server端回覆 AMX status*/
	public final static int STATUS_ON = 1;
	public final static int STATUS_OFF = 2;
	public final static int STATUS_MUTE = 3;
	public final static int STATUS_UNMUTE = 4;
	public final static int STATUS_PROJECT_HDMI = 7;
	public final static int STATUS_PROJECT_VGA = 8;

	public final static int STATUS_MATRIX_INPUT_1 = 9;
	public final static int STATUS_MATRIX_INPUT_2 = 10;
	public final static int STATUS_MATRIX_INPUT_3 = 11;
	public final static int STATUS_MATRIX_INPUT_4 = 12;
	public final static int STATUS_MATRIX_INPUT_5 = 13;
	public final static int STATUS_MATRIX_INPUT_6 = 14;
	public final static int STATUS_MATRIX_INPUT_7 = 15;
	public final static int STATUS_MATRIX_INPUT_8 = 16;

	
	
	
	




	

}
