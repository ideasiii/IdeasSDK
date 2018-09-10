package sdk.ideas.cmp.semantic;

/**
 * Created by joe on 2017/4/19.
 */

public abstract class SemanticWordCMPParameters
{
    private static int wordID = 1;
    
    public static int getWordID()
    {
        return wordID++;
    }
    
    public static final int MAX_WORD_LEN = 1950;
    
    public static final int TYPE_REQUEST_UNKNOWN = 0;
    public static final int TYPE_REQUEST_CONTROL = 1;
    public static final int TYPE_REQUEST_CONVERSATION = 2;
    public static final int TYPE_REQUEST_RECORD = 3;
    public static final int TYPE_REQUEST_STORY = 4;
    public static final int TYPE_REQUEST_GAME = 5;
    public static final int TYPE_REQUEST_BLE = 6;
    
    public static final int TYPE_RESPONSE_UNKNOWN = 0;
    public static final int TYPE_RESPONSE_LOCAL = 1;
    public static final int TYPE_RESPONSE_SPOTIFY = 2;
    public static final int TYPE_RESPONSE_TTS = 3;
    
    public static final String STRING_JSON_KEY_TYPE = "type";
    public static final String STRING_JSON_KEY_HOST = "host";
    public static final String STRING_JSON_KEY_FILE = "file";
    
    public static final String STRING_JSON_KEY_ID = "id";
    public static final String STRING_JSON_KEY_TTS = "tts";
    public static final String STRING_JSON_KEY_LANG = "lang";
    public static final String STRING_JSON_KEY_EXTEND = "extend";
    
    
}
