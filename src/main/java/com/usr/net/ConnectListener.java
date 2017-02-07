package com.usr.net;

import com.usr.net.bean.ConnectConfiguration;

/**
 * Created by LiuJinqi on 2015-07-30.
 */
public interface ConnectListener {
    /**
     * connect success
     */
    public void connectSuccess(ConnectConfiguration configuration);

    /**
     * connect error
     */
    public void connectBreak(ConnectConfiguration configuration);


    public void onReceviceData(ConnectConfiguration configurations,byte[] data);
}
