package sdk.ideas.common;

import android.os.Build;

public class DeviceInfo
{
	// 模組號碼
	public static String getModel()
	{
		return Build.MODEL;
	}
	// 品牌名稱
	public static String getBrand()
	{
		return Build.BRAND;
	}
	// 主機版名稱
	public static String getBoard()
	{
		return Build.BOARD;
	}
	
	
	
}
