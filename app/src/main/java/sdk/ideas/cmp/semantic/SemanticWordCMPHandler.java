package sdk.ideas.cmp.semantic;

import android.content.Context;

import sdk.ideas.cmp.CMPHandler;
import sdk.ideas.cmp.CMPParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import sdk.ideas.common.Logs;
import sdk.ideas.module.Controller;

/**
 * Created by joe on 2017/4/18.
 */

public class SemanticWordCMPHandler extends CMPHandler
{
    
    
    public SemanticWordCMPHandler(Context context)
    {
        super(CMPParameters.CLASS_CMP_SEMANTIC_WORD, context);
    }
    
    
    public void sendSemanticWordCommand(int id, int type, String words)
    {
        if (null != words && !words.isEmpty())
        {
            int wordByteLen = -1;
            try
            {
                wordByteLen = words.getBytes(CMPParameters.CODE_TYPE).length;
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            if (wordByteLen > 0 && wordByteLen < SemanticWordCMPParameters.MAX_WORD_LEN)
            {
                
                JSONObject tmp = new JSONObject();
                try
                {
                    tmp.put("device_id", SemanticDeviceID.getDeviceID(mContext));
                    Logs.showTrace("deviceID" + SemanticDeviceID.getDeviceID(mContext));
                    tmp.put("id", id);
                    tmp.put("type", type);
                    tmp.put("total", 0);
                    tmp.put("num", 0);
                    tmp.put("word", words);
                }
                catch (JSONException e)
                {
                    Logs.showError(e.toString());
                }
                super.sendCommandSynchronize(Controller.semantic_word_request, tmp.toString());
            }
            else
            {
                if (wordByteLen >= SemanticWordCMPParameters.MAX_WORD_LEN)
                {
                    //handle long word solution
                    
                }
                else
                {
                    //ERROR get byte string len
                    
                }
            }
        }
        
    }
    
    
}
