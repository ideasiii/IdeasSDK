package sdk.ideas.common;

import java.util.HashMap;

public interface OnCallbackResult
{
	void onCallbackResult(int result,int what, int from, HashMap<String,String> message);

}
