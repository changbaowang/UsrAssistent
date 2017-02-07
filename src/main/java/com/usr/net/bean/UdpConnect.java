package com.usr.net.bean;

import android.os.Handler;
import android.util.Log;

import com.usr.net.ConnectListener;
import com.usr.net.utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by liu on 15/8/2.
 */
public abstract class UdpConnect implements Runnable ,SendMsgThread.OnSendListener{
    private final String TAG = UdpConnect.class.getSimpleName();
    public static int CONNECT_STATE_UNRECEIVED = 0;
    public static int CONNECT_STATE_RECEIVED = 1;

    private final static int DEFAULT_PORT = 5000;
    private final static String DEFAULT_IP = "255.255.255.255";

    protected int currentState = CONNECT_STATE_UNRECEIVED;
    private ConnectConfiguration configuration;
    protected Handler handler;
    protected ConnectListener connectListener;
    protected DatagramSocket datagramSocket;

    protected int targetPort = DEFAULT_PORT;
    protected int localPort = DEFAULT_PORT;
    protected String ip = DEFAULT_IP;

    protected SendMsgThread sendMsgThread ;

    protected abstract void build();

    public void send(byte[] data) {
        if (sendMsgThread != null){
            sendMsgThread.putMsg(data);
        }
    };


    @Override
    public void onSend(byte[] data) {
        if (datagramSocket != null) {
            try {
                DatagramPacket sendPacket = new DatagramPacket(data, data.length,
                        InetAddress.getByName(ip), targetPort);
                datagramSocket.send(sendPacket);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.d(TAG, "--------------->发送数据失败");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "--------------->发送数据失败");
            }
        }
    }

    protected void onReceived(final byte[] data, final ConnectConfiguration configuration) {
        Log.d(TAG, "------------------>onReceived:"+ Utils.bytesToHexString(data));
        if (connectListener!=null && handler != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    connectListener.onReceviceData(configuration,data);
                }
            });
        }
    };


    /**
     * Break this connection
     */
    public void breakConnect(){
        currentState = CONNECT_STATE_UNRECEIVED;
        if (datagramSocket != null){
            datagramSocket.close();
            datagramSocket = null;

            if (connectListener != null && handler != null)
                connectListener.connectBreak(configuration);
        }

        if (sendMsgThread != null){
            sendMsgThread.stopSend();
        }
    }

    @Override
    public void run() {
        try {
            byte[] data = new byte[1024];
            DatagramPacket revPacket = new DatagramPacket(data, data.length);
            while (currentState == CONNECT_STATE_RECEIVED) {
                System.out.println("udp--------------->run");
                datagramSocket.receive(revPacket);
                final byte[] realData = new byte[revPacket.getLength()];
                System.arraycopy(data, 0, realData, 0, realData.length);
                ConnectConfiguration configuration = new ConnectConfiguration(revPacket.getAddress().getHostAddress(),revPacket.getPort());
                onReceived(realData,configuration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public ConnectListener getConnectListener() {
        return connectListener;
    }


    public void setTargetPort(int port) {
        this.targetPort = port;
        if (configuration != null)
            configuration.setPort(port);
    }

    public void setIp(String ip) {
        this.ip = ip;
        if (configuration != null)
            configuration.setHost(ip);
    }

    public ConnectConfiguration getConfiguration() {
        return configuration;
    }

    public String getIp() {
        return ip;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public int getLocalPort() {
        return localPort;
    }
}
