package com.usr.net;


import android.os.Handler;
import android.util.Log;

import com.usr.net.bean.Connect;
import com.usr.net.bean.TcpServerConnect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * Created by Administrator on 2015-07-30.
 */
public class TcpServer extends Thread implements TcpServerConnect.RemoveSocketListener {
    private final String TAG = TcpServer.class.getSimpleName();
    private static final int STATE_NOT_START = 0;
    private static final int STATE_STARTED = 1;
    private int currentState = STATE_NOT_START;
    private Handler handler;

    private int port;
    private ServerSocket serverSocket;
    private final CopyOnWriteArrayList<Connect> connectsList = new CopyOnWriteArrayList<>();

    private ConnectListener connectListener;
    private OnAddTcpServerConnectListener onAddTcpServerConnectListener;
    private ExecutorService pool;

    public TcpServer(int port, Handler handler, ExecutorService pool, ConnectListener connectListener,
                     OnAddTcpServerConnectListener onAddTcpServerConnectListener) {
        this.port = port;
        this.handler = handler;
        this.connectListener = connectListener;
        this.pool = pool;
        this.onAddTcpServerConnectListener = onAddTcpServerConnectListener;
    }

    public void build() {
        try {
            serverSocket = new ServerSocket(port);
            currentState = STATE_STARTED;
            Log.d(TAG, "------------------>build");
        } catch (IOException e) {
            e.printStackTrace();
            serverSocket = null;
            currentState = STATE_NOT_START;
        }
    }

    @Override
    public void run() {
        build();
        while (currentState == STATE_STARTED) {
            try {
                Socket socket = serverSocket.accept();
                if (socket != null) {
                    final Connect connect = new TcpServerConnect(socket, handler, connectListener, this);
                    connectsList.add(connect);
                    pool.execute(connect);
                    Log.d(TAG, "------------------>add");
                    if (onAddTcpServerConnectListener != null && handler != null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onAddTcpServerConnectListener.onAdd(connect);
                            }
                        });
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void breakServerTcpConnnect(Connect connect) {
        int index = connectsList.indexOf(connect);
        if (index != -1) {
            connectsList.get(index).breakConnect();
        }
    }


    public void stopServer() {
        for (Connect connect : connectsList) {
            connect.breakConnect();
        }
        connectsList.clear();
        currentState = STATE_NOT_START;
        try {
            serverSocket.close();
            Log.d(TAG, "------------------>server stop");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setOnAddTcpServerConnectListener(OnAddTcpServerConnectListener onAddTcpServerConnectListener) {
        this.onAddTcpServerConnectListener = onAddTcpServerConnectListener;
    }

    @Override
    public void onRemove(Connect connect) {
        int index = connectsList.indexOf(connect);
        if (index != -1) {
            connectsList.remove(index);
            Log.d(TAG, "------------------>remove");
        }
    }

    public interface OnAddTcpServerConnectListener {
        public void onAdd(Connect connect);
    }

    public CopyOnWriteArrayList<Connect> getConnectsList() {
        return connectsList;
    }
}
