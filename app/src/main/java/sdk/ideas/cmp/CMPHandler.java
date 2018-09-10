package sdk.ideas.cmp;

import android.content.Context;

import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.module.Controller;

/**
 * Created by joe on 2017/4/18.
 */


public class CMPHandler extends BaseHandler
{
    private static int mPort;
    private static String mIP = "";
    private int mWhichClass = CMPParameters.CLASS_CMP;
    
    public CMPHandler(int whichClass, Context context)
    {
        super(context);
        mWhichClass = whichClass;
    }
    
    public CMPHandler(Context context)
    {
        super(context);
    }
    
    public static void setIPAndPort(String nIP, int nPort)
    {
        CMPHandler.mIP = nIP;
        CMPHandler.mPort = nPort;
    }
    
    public void sendCommandSynchronize(int nCommand, String bodyData)
    {
        Thread t = new Thread(new CMPSocketRunnable(nCommand, bodyData, CMPParameters.SYNCHRONIZE_MODE));
        t.start();
    }
    
    private void sendCommandAsynchronize(int nCommand, String bodyData)
    {
        Thread t = new Thread(new CMPSocketRunnable(nCommand, bodyData, CMPParameters.ASYNCHRONIZE_MODE));
        t.start();
        
    }
    
    
    private class CMPSocketRunnable implements Runnable
    {
        private String mData = "";
        private int mCommand;
        private int mTag = 0;
        
        public CMPSocketRunnable(int nCommand, String data, int nTag)
        {
            if (null != data && !data.isEmpty())
            {
                this.mData = data;
            }
            this.mCommand = nCommand;
            this.mTag = nTag;
        }
        
        @Override
        public void run()
        {
            Controller.CMP_PACKET respPacket = new Controller.CMP_PACKET();
            if (mTag == CMPParameters.SYNCHRONIZE_MODE)
            {
                int status = Controller.cmpRequest(CMPHandler.mIP, CMPHandler.mPort, mCommand, mData, respPacket);
                HashMap<String, String> message = new HashMap<>();
                
                
                Logs.showTrace("[CMPHandler] status:" + String.valueOf(status));
                switch (status)
                {
                    case Controller.STATUS_ROK:
                        
                        if (null != respPacket.cmpBody && !respPacket.cmpBody.isEmpty())
                        {
                            Logs.showTrace("[CMPHandler] respPacket.cmpBody" + String.valueOf(respPacket.cmpBody));
                            message.put("message", respPacket.cmpBody);
                            callBackMessage(ResponseCode.ERR_SUCCESS, mWhichClass, CMPParameters.CMP_RESPONSE_METHOD, message);
                        }
                        else
                        {
                            //message.put("message", "success!");
                            Logs.showError("[CMPHandler] null respPacket.cmpBody");
                            callBackMessage(ResponseCode.ERR_SUCCESS, mWhichClass, CMPParameters.CMP_RESPONSE_METHOD, message);
                            
                        }
                        break;
                    
                    
                    default:
                        Logs.showError("[CMPHandler] ERROR status: " + String.valueOf(status));
                        message.put("message", "IO Exception!");
                        callBackMessage(ResponseCode.ERR_IO_EXCEPTION, mWhichClass, CMPParameters.CMP_RESPONSE_METHOD, message);
                        
                        break;
                    
                    
                }
            }
            else
            {
                if (mTag == CMPParameters.ASYNCHRONIZE_MODE)
                {
                }
            }
            
            
        }
    }
    
    
}
