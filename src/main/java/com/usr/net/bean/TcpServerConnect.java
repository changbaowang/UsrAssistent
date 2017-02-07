package com.usr.net.bean;

import android.os.Handler;
import android.util.Log;

import com.usr.net.ConnectListener;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Administrator on 2015-07-31.
 */
public class TcpServerConnect extends Connect {
    private final String TAG = TcpServerConnect.class.getSimpleName();

    public TcpServerConnect(Socket socket,Handler handler,final ConnectListener connectListener,RemoveSocketListener removeSocketListener){
        this.socket = socket;
        this.removeSocketListener = removeSocketListener;
        this.handler = handler;
        connectType = CONNECT_TYPE.TCP_SERVER;
        this.connState = CONNECT_STATE_CONNECTED;
        this.connectListener = connectListener;
        setConfiguration(new ConnectConfiguration(socket.getInetAddress().getHostAddress(),socket.getPort()));

        if (connectListener != null && handler != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    connectListener.connectSuccess(configuration);
                }
            });
        }

        System.out.println(TAG+"----------------------->ip:"+socket.getInetAddress()+" port:"+socket.getPort());

    }

    @Override
    protected void build() {
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();
            Log.d(TAG, "------------------>build");
        }catch (IOException ex){
            ex.printStackTrace();
            breakConnect();
        }
    }

}
