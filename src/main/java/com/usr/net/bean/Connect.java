package com.usr.net.bean;


import android.os.Handler;
import android.util.Log;

import com.usr.net.ConnectListener;
import com.usr.net.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Administrator on 2015-07-30.
 */
public abstract class Connect implements Runnable {
    private final String TAG = Connect.class.getSimpleName();
    public static final int CONNECT_STATE_UNCONNECT = 0;
    public static final int CONNECT_STETE_CONNECTING = 1;
    public static final int CONNECT_STATE_CONNECTED = 2;

    public ConnectConfiguration configuration;
    protected Handler handler;
    protected RemoveSocketListener removeSocketListener;
    protected ConnectListener connectListener;
    protected CONNECT_TYPE connectType;
    protected int connState = CONNECT_STATE_UNCONNECT;
    protected Socket socket;
    protected OutputStream os;
    protected InputStream is;
    protected abstract void build();

    /**
     * Break this connection
     */
    public void breakConnect() {
        connState = CONNECT_STATE_UNCONNECT;

        try {
            if (is != null)
                is.close();
            if (os != null)
                os.close();
            if (socket != null)
                socket.close();

        }catch (IOException e) {
            e.printStackTrace();
        }


        if (connectListener != null && handler != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    connectListener.connectBreak(configuration);
                }
            });
        }

        Log.d(TAG,"------------------>breakConnect");

        if (removeSocketListener != null)
            removeSocketListener.onRemove(this);

    }


    public void send(byte[] data) {
        if (os == null)
            return;
        try {
            os.write(data);
            os.flush();
            Log.d(TAG, "------------------>send Data:"+ Utils.bytesToHexString(data));
        }catch (IOException ex){
            ex.printStackTrace();
        }

    }


    protected void onReceived(final byte[] data) {
        Log.d(TAG, "------------------>onReceived:"+Utils.bytesToHexString(data));
        if (connectListener != null && handler != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    connectListener.onReceviceData(configuration,data);
                }
            });
        }
    }

    @Override
    public void run() {
        build();
        int length;
        byte[] data = new byte[1024];
        while (connState == CONNECT_STATE_CONNECTED){
            try {
                length = is.read(data, 0, data.length);
                if (length >0){
                    byte[] tmp = new byte[length];
                    System.arraycopy(data, 0, tmp, 0, length);
                    onReceived(tmp);
                }else {
                    breakConnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (connState == CONNECT_STATE_CONNECTED)
                    breakConnect();
            }
        }

        Log.d(TAG,"----------------------->while break");
    }




    public interface RemoveSocketListener{
        public void onRemove(Connect connect);
    }

    public static enum CONNECT_TYPE{
        TCP_SERVER,TCP_CLIENT;
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public ConnectListener getConnectListener() {
        return connectListener;
    }


    public void setRemoveSocketListener(RemoveSocketListener removeSocketListener) {
        this.removeSocketListener = removeSocketListener;
    }


    public RemoveSocketListener getRemoveSocketListener() {
        return removeSocketListener;
    }

    public CONNECT_TYPE getConnectType() {
        return connectType;
    }

    public void setConnectType(CONNECT_TYPE connectType) {
        this.connectType = connectType;
    }

    public ConnectConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConnectConfiguration configuration) {
        this.configuration = configuration;
    }
}
