package sdk.ideas.pptcontrol;

/**
 * 請留意：這裡的 CMD_XXX 常數前面沒有 CMD_PREFIX，送訊息時需附加
 *
 */
public final class Consts
{
	public final static String CMD_PARAM_SEPERATOR = ","; // 參數之間的分隔符號
	public final static String CMD_PREFIX = "~"; // 每條控制訊息的前綴

	// 控制訊息，格式為 "CMD_PREFIX + 要控制的部位 + {參數分隔符號 + 參數}"
	// 參數數量隨意
	private final static String CMD_LEFT_BUTTON_PREFIX = "left"; // 滑鼠左鍵
	private final static String CMD_RIGHT_BUTTON_PREFIX = "right"; // 滑鼠右鍵
	private final static String CMD_UP = "up"; // 釋放按鍵
	private final static String CMD_DOWN = "down"; // 壓下按鍵

	public final static int CMD_VWHEEL_ROTATE_AWAY_USER = 1; // 滾輪遠離使用者方向滾動
	public final static int CMD_VWHEEL_ROTATE_TO_USER = -1; // 滾輪朝使用者方向滾動

	// command when using touch pad
	public final static String CMD_LEFT_DOWN = CMD_LEFT_BUTTON_PREFIX + CMD_PARAM_SEPERATOR + CMD_DOWN;
	public final static String CMD_RIGHT_DOWN = CMD_RIGHT_BUTTON_PREFIX + CMD_PARAM_SEPERATOR + CMD_DOWN;
	public final static String CMD_LEFT_UP = CMD_LEFT_BUTTON_PREFIX + CMD_PARAM_SEPERATOR + CMD_UP;
	public final static String CMD_RIGHT_UP = CMD_RIGHT_BUTTON_PREFIX + CMD_PARAM_SEPERATOR + CMD_UP;
	public final static String CMD_MOVE_CURSOR_PREFIX = "move" + CMD_PARAM_SEPERATOR;
	public final static String CMD_MOVE_LAZER_PREFIX = "laze" + CMD_PARAM_SEPERATOR;
	public final static String CMD_ROLL_VWHEEL_PREFIX = "vwheel" + CMD_PARAM_SEPERATOR;

	public final static String CMD_LAZER_SHOW = "lazs";
	public final static String CMD_LAZER_OFF = "lazf";
	
	// 選擇檔案時的指令
	public final static String CMD_OPEN = "open"; // 開啟 or 關閉簡報檔案
	public final static String CMD_DELETE = "delt"; // 刪除簡報檔案

	// command when presenting
	public final static String CMD_SHOW = "show"; // 進入全螢幕放映
	public final static String CMD_STOP = "stop"; // 結束全螢幕放映
	public final static String CMD_SHOW_STOP = "shst"; // 全螢幕放映時=CMD_STOP,未在全螢幕放映時=CMD_SHOW
	public final static String CMD_PAGE_UP = "pgup"; // 全螢幕放映：上一頁
	public final static String CMD_PAGE_DOWN = "pgdn"; // 全螢幕放映：下一頁

	// command when selecting presentation file
	public final static String CMD_LIST_SHOW = "flis"; // 開啟簡報檔案列表
	public final static String CMD_LIST_CLOSE = "flic"; // 關閉簡報檔案列表
	public final static String CMD_LIST_UP = "fiup"; // 簡報檔案列表游標向上
	public final static String CMD_LIST_DOWN = "fidn"; // 簡報檔案列表游標向下

	// 非命令的訊息
	public final static String MSG_ABLE_TO_CONNECT = "ableToConnect?"; // 詢問伺服器是否可連線
	public final static String MSG_CONFIRM = "allowedToConnect"; // 伺服器回應可連線
	public final static String MSG_RECEIVE_PORT = "recv"; // 告知伺服器傳送訊息應送至哪個連接埠
	public final static String FROM_SERVER_MSG_PPT_PAGE = "page"; // 伺服器傳送簡報目前頁數
	public final static String FROM_SERVER_MSG_SEND_COMMAND_ACK = "cmd_ack"; // 伺服器收到命令傳回的
																				// ack
	public final static String FROM_SERVER_MSG_PPT_PAGE_COUNT = "count"; // 簡報總共頁數
	public final static String FROM_SERVER_MSG_PPT_PAGE_INDEX = "index"; // 簡報目前顯示頁面頁碼

	private Consts()
	{
	}
}
