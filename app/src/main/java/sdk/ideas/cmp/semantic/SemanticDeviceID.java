package sdk.ideas.cmp.semantic;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by joe on 2017/6/16.
 */

public class SemanticDeviceID
{
    private static final String SEMANTIC_USING = "Semantic_using";
    private static final String DEVICE_ID_KEY = "deviceIdKey";
    
    public static String getDeviceID(Context mContext)
    {
        String deviceID = getKey(mContext, DEVICE_ID_KEY);
        if (null == deviceID)
        {
            UUID uuid = UUID.randomUUID();
            saveKey(mContext, DEVICE_ID_KEY, uuid.toString());
            return uuid.toString();
        }
        else
        {
            return deviceID;
        }
        
    }

    private static void saveKey(Context mContext, String key, String deviceID)
    {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SEMANTIC_USING, Context.MODE_PRIVATE).edit();
        editor.putString(key, deviceID);
        editor.apply();
        
    }
    
    private static String getKey(Context mContext, String key)
    {
        SharedPreferences prefs = mContext.getSharedPreferences(SEMANTIC_USING, Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }
}
