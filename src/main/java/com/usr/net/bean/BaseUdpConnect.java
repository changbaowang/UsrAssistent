package com.usr.net.bean;

import android.os.Handler;

import com.usr.net.ConnectListener;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


/**
 * Created by Administrator on 2015-08-03.
 */
public class BaseUdpConnect extends UdpConnect {

    public BaseUdpConnect(String ip, int targetPort,int localPort, Handler handler, ConnectListener connectListener){
        this.ip = ip;
        this.targetPort = targetPort;
        this.localPort = localPort;
        this.handler = handler;
        this.connectListener = connectListener;
        build();
    }

    @Override
    protected void build() {
        try {
            datagramSocket = new DatagramSocket(null);
            datagramSocket.setBroadcast(true);
            datagramSocket.setReuseAddress(true);
            datagramSocket.bind(new InetSocketAddress(localPort));
            currentState = CONNECT_STATE_RECEIVED;
            sendMsgThread = new SendMsgThread();
            sendMsgThread.setOnSendListener(this);
            sendMsgThread.start();
        }catch (SocketException ex){
            ex.printStackTrace();
            currentState = CONNECT_STATE_UNRECEIVED;
            breakConnect();
        }

    }
}
