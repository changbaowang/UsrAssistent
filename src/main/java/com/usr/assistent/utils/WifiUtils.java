package com.usr.assistent.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Created by Administrator on 2015-08-10.
 */
public class WifiUtils {
    private static WifiManager wifiManager;
    private static WifiManager.MulticastLock multicastLock;
    private static WifiManager.WifiLock wifiLock;

    public static String getIp(Context context){
       if (wifiManager == null)
           wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
       if (!wifiManager.isWifiEnabled())
           return "localhost";

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ip=intToIp(wifiInfo.getIpAddress());
        return ip;
    }

    private static  String intToIp(int i)  {
        return (i&0xFF)+"."+((i>>8)&0xFF)+"."+ ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF );
    }



    public static void lockWifi(Context context,String lockStr){
        if (wifiManager == null){
            wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifiManager.createMulticastLock(lockStr+"multicast_lock");
            multicastLock.acquire();

            wifiLock = wifiManager.createWifiLock(lockStr+"wifi_lock");
            wifiLock.acquire();
        }
    }


    public static void releaseWifiLock(){
        if(wifiLock != null && wifiLock.isHeld()){
            wifiLock.release();
        }

        if (multicastLock != null && multicastLock.isHeld())
            multicastLock.release();
    }
}


