package com.usr.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.usr.net.bean.BaseUdpConnect;
import com.usr.net.bean.Connect;
import com.usr.net.bean.TcpClientConnect;
import com.usr.net.bean.UdpConnect;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2015-07-30.
 */
public class ConnectManager implements Connect.RemoveSocketListener{
    private final String TAG = ConnectManager.class.getSimpleName();
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static ConnectManager instance;
    private TcpServer tcpServer ;

//    private final CopyOnWriteArrayList<Connect> connectsList = new CopyOnWriteArrayList<>();

    private ConnectManager(){
    }

    public static ConnectManager getInstance(){
        if (instance == null)
            instance = new ConnectManager();
        return instance;
    }


    public TcpServer createTcpServer(int port,ConnectListener connectListener,
                                TcpServer.OnAddTcpServerConnectListener onAddTcpServerConnectListener){
        if (tcpServer == null){
            tcpServer = new TcpServer(port,handler,pool,connectListener,onAddTcpServerConnectListener);
            pool.execute(tcpServer);
            return tcpServer;
        }

        return null;
    }


    public void stopTcpServer(){
        if (tcpServer != null){
            tcpServer.stopServer();
            tcpServer = null;
        }
    }

    public Connect createTcpClient(String host,int port,ConnectListener connectListener){
        Connect tcpClientConnect = new TcpClientConnect(port,host,handler,connectListener);
//        tcpClientConnect.setRemoveSocketListener(this);
//        connectsList.add(tcpClientConnect);
        Log.d(TAG, "------------------>add");
        pool.execute(tcpClientConnect);
        return tcpClientConnect;
    }



//    public void breakTcpServerConn(Connect connect){
//        if (tcpServer != null){
//            tcpServer.breakServerTcpConnnect(connect);
//        }
//    }

//    public void breakTcpClient(Connect connect){
//        int index = connectsList.indexOf(connect);
//        if (index != -1){
//            connectsList.get(index).breakConnect();
//        }
//    }


    public UdpConnect createUdp(String ip,int targetPort,int localPort,ConnectListener connectListener){
        UdpConnect udpConnect = new BaseUdpConnect(ip,targetPort,localPort,handler,connectListener);
        pool.execute(udpConnect);
        return udpConnect;
    }

//    public void breakUdp(UdpConnect udpConnect){
//        udpConnect.breakConnect();
//    }

    @Override
    public void onRemove(Connect connect) {
//        int index = connectsList.indexOf(connect);
//        if (index != -1){
//            connectsList.remove(index);
//            Log.d(TAG, "------------------>remove");
//        }
//
//        System.out.println("ConnectManager---------------->size:"+connectsList.size());
    }
}
