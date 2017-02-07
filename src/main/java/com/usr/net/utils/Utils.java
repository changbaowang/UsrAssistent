package com.usr.net.utils;

/**
 * Created by Administrator on 2015-07-31.
 */
public class Utils {
    /**
     * 字节数组 转换为16进制
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + " ");
        }
        return stringBuilder.toString();
    }


    public static String generateConnectKey(String host,int port){
        StringBuilder sb = new StringBuilder();
        sb.append(host);
        sb.append(":");
        sb.append(String.valueOf(port));
        return sb.toString();
    }
}
