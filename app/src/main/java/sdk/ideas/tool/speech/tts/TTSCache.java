package sdk.ideas.tool.speech.tts;


import java.util.HashMap;

/**
 * Created by joe on 2017/6/26.
 */

public class TTSCache
{
    private static HashMap<String, String> ttsCache = new HashMap<>();

    static synchronized boolean setTTSCache(String setTTSString, String textID, String pitch, String rate)
    {
        if (!ttsCache.isEmpty())
        {
            return false;
        }
        else
        {
            ttsCache.put("tts", setTTSString);
            ttsCache.put("id", textID);
            ttsCache.put("pitch", pitch);
            ttsCache.put("rate", rate);

            return true;
        }
    }

    static synchronized HashMap<String, String> getTTSCache()
    {
        if (!ttsCache.isEmpty())
        {
            HashMap<String, String> returnTTSCache = new HashMap<>();
            returnTTSCache.put("tts", ttsCache.get("tts"));
            returnTTSCache.put("id", ttsCache.get("id"));
            returnTTSCache.put("pitch", ttsCache.get("pitch"));
            returnTTSCache.put("rate", ttsCache.get("rate"));

            ttsCache.clear();

            return returnTTSCache;
        }

        return null;
    }
}
