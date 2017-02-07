package com.usr.assistent;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.usr.assistent.adapter.ConnectionSelectAdapter;
import com.usr.assistent.bean.ConfigItem;
import com.usr.assistent.bean.Message;
import com.usr.assistent.utils.AnimateUtils;
import com.usr.assistent.utils.StringUtils;
import com.usr.assistent.utils.Utils;
import com.usr.assistent.utils.WifiUtils;
import com.usr.assistent.views.ConnectionsMenuManager;
import com.usr.assistent.views.MyConfigDialog;
import com.usr.assistent.views.MyConfigDialogManager;
import com.usr.net.ConnectListener;
import com.usr.net.TcpServer;
import com.usr.net.bean.Connect;
import com.usr.net.bean.ConnectConfiguration;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;

/**
 * Created by Administrator on 2015-07-30.
 */
public class TcpServerFragment extends CommunicationBaseFragment implements CommunicationBaseFragment.OnButtonClickListener, ConnectListener, TcpServer.OnAddTcpServerConnectListener {

    private final String TCP_SERVER_PORT = "tcp_server_port";

    private TcpServer tcpServer;
    private String localIp = "localhost";
    private int port = -1;
    private MyConfigDialogManager configDialogManager;
    private ConnectionsMenuManager connectionsMenuManager;

    private boolean isSendHex;
    private boolean isHexDisplay;

    @InjectView(R.id.btn_select_conns)
    Button btnSelectConns;
    private Connect currentConnection;
    private Timer timer;
    private final int TIME_SEND = 0 ;
    private boolean isTimeDialogClicked = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
             case TIME_SEND:
                 onSendClick();
                break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tcp_server, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSelectConns.setOnClickListener(this);
        setOnButtonClickListener(this);
        configDialogManager = MyConfigDialogManager.getInstance();
        connectionsMenuManager = ConnectionsMenuManager.getInstance();

        setConfigsOnItemClickListener(new ConfigsOnItemClickListener() {
            @Override
            public void onItemClick(int position,boolean isSelected) {
                switch (position) {
                    case 0:
                        getMsgsList().clear();
                        getMsgAdapter().notifyDataSetChanged();
                        showOrDismissConfigView();
                        break;
                    case 1:
                        isHexDisplay = isSelected;
                        break;
                    case 2://按16进制发送
                        String textInput = etSend.getText().toString();
                        isSendHex = isSelected;
                        if(isSelected){
                            if (textInput != null){
                                try {
                                    etSend.setText(StringUtils.bytesToHexString(textInput.getBytes("gb-2312")));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                        }else {
                            if (textInput != null){
                                textInput = textInput.replace(" ","");
                                byte[] bytes = StringUtils.hexStringToBytes(textInput);
                                try {
                                    String normalText = bytes == null?"":new String(bytes,"gb-2312");
                                    etSend.setText(normalText);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                        break;
                    case 3:
                        if (isSelected)
                            showTimerDialog();
                        else
                            stopTimer();
                        break;
                }
            }
        });


        rvMsg.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                //触摸隐藏选择连接菜单
                if (connectionsMenuManager.getConnectionsMenu() != null) {
                    dismissMenu();
                    return false;
                }

                //触摸滑动的时候才将configDialog下滑
                if (isShow()){
                    showOrDismissConfigView();
                    Utils.hideInputMethodWindow(getActivity());
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }
        });
    }


    @Override
    protected void viewStateAnimation() {
        btnSelectConns.setTranslationX(Utils.dpToPx(200));
        super.viewStateAnimation();
        AnimateUtils.translationX(btnSelectConns, 0, ANIM_DURATION_TRANSLATIONX, 700);
    }

    @Override
    protected String setFragmentTitle() {
        return getString(R.string.tcp_server);
    }

    @Override
    protected int setConnectStateIcon() {
        return R.mipmap.btn_ts_hl;
    }


    @Override
    protected List<ConfigItem> initConfigItem() {
        List<ConfigItem> configItemList = new ArrayList<>();
        ConfigItem item0 = new ConfigItem(new int[]{R.mipmap.btn_clear_text_n, R.mipmap.btn_clear_text_n},
                new int[]{getResources().getColor(R.color.config_item_normal), getResources().getColor(R.color.config_item_normal)},
                getString(R.string.clear), ConfigItem.CONFIG_TYPE.CLEAR_TEXT);

        ConfigItem item1 = new ConfigItem(new int[]{R.mipmap.btn_hex_display_n, R.mipmap.btn_hex_display_hl},
                new int[]{getResources().getColor(R.color.config_item_hl), getResources().getColor(R.color.config_item_normal)},
                getString(R.string.hex_display), ConfigItem.CONFIG_TYPE.HEX_DISPLAY);

        ConfigItem item2 = new ConfigItem(new int[]{R.mipmap.btn_hex_send_n, R.mipmap.btn_hex_send_hl},
                new int[]{getResources().getColor(R.color.config_item_hl), getResources().getColor(R.color.config_item_normal)},
                getString(R.string.hex_send), ConfigItem.CONFIG_TYPE.HEX_SEND);

        ConfigItem item3 = new ConfigItem(new int[]{R.mipmap.btn_timer_n, R.mipmap.btn_timer_hl},
                new int[]{getResources().getColor(R.color.config_item_hl), getResources().getColor(R.color.config_item_normal)},
                getString(R.string.timer_send), ConfigItem.CONFIG_TYPE.TIMER_SEND);

        configItemList.add(item0);
        configItemList.add(item1);
        configItemList.add(item2);
        configItemList.add(item3);

        return configItemList;
    }

    @Override
    protected boolean isStartIntroduceAnimation() {
        return true;
    }

    @Override
    public void onSendClick() {
        System.out.println("TcpServerFragment--------------------->onSendClick");
        if (currentConnection == null) {
            Utils.showToast(getActivity(), R.string.select_connection_first);
            return;
        }

        String sendStr = etSend.getText().toString();
        if (TextUtils.isEmpty(sendStr)) {
            AnimateUtils.shake(etSend);
            return;
        }
        if (!isSendHex){
            try {
                currentConnection.send(sendStr.getBytes("gb-2312"));
            }catch (Exception ex){
                currentConnection.send(sendStr.getBytes());
            }
        }
        else {
            sendStr = sendStr.replace(" ","");
            if (!StringUtils.isRightHexStr(sendStr)){
                AnimateUtils.shake(etSend);
                return;
            }
            byte[] data = StringUtils.hexStringToBytes(sendStr);
            if (data != null){
                currentConnection.send(data);
                sendStr = StringUtils.bytesToHexString(data);
            }else
                return;
        }

        Message newMsg = new Message(Message.MESSAGE_TYPE.SEND, sendStr, localIp + ":" + port);
        getMsgsList().add(newMsg);
        getMsgAdapter().notifyLastItem();
        rvMsg.smoothScrollToPosition(getMsgAdapter().getItemCount() - 1);
    }

    @Override
    public void onConfigMenuClick() {
        if (configDialogManager.getMyConfigDialog() != null)
            return;
        if (tcpServer != null) {
            Utils.showToast(getActivity(), R.string.break_connect_first);
            return;
        }

        canTouched = false;
        Utils.hideInputMethodWindow(getActivity());

        String portPre = AndroidSharedPreferences.getString(TCP_SERVER_PORT,"-1");
        port = Integer.parseInt(portPre);

        final String portStr = port == -1 ? "" :portPre;
        configDialogManager.toggleContextMenuFromView(portStr, getConfigMenuItem().getActionView(), new MyConfigDialog.OnOptionClickListener() {
            @Override
            public void onOkClick() {
                EditText etPort = configDialogManager.getMyConfigDialog().getEtConfigPort();
                String inputPort = etPort.getText().toString();
                if (TextUtils.isEmpty(inputPort.trim()) || Integer.parseInt(inputPort) > 65535) {
                    AnimateUtils.shake(etPort);
                    return;
                }

                configDialogManager.toggleContextMenuFromView(null, null, null);
                canTouched = true;
                port = Integer.parseInt(inputPort);

                AndroidSharedPreferences.putString(TCP_SERVER_PORT,inputPort);
            }

            @Override
            public void onCancelClick() {
                configDialogManager.toggleContextMenuFromView(null, null, null);
                canTouched = true;
            }
        });
    }

    @Override
    public void onConnOptionCLick() {
        System.out.println("TcpServerFragment--------------------->onConnOptionCLick");
        if (currentConnection != null) {
            currentConnection.breakConnect();
            return;
        }
        if (tcpServer != null) {
            refreshConnectState(false);
            MainActivity.connectManager.stopTcpServer();
            tcpServer = null;
        } else {
            if (port == -1) {
                Utils.showToast(getActivity(), R.string.config_error);
                return;
            }
            tcpServer = MainActivity.connectManager.createTcpServer(port, this, this);
            localIp = WifiUtils.getIp(getActivity());

            showLocalIpInfo(localIp + ":" + port);

            if (tcpServer != null)
                refreshConnectState(true);
        }

    }

    private void showLocalIpInfo(String info) {
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.local_connection_info)
                .setMessage(info)
                .setPositiveButton("OK", null)
                .create();
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        //触摸其它按钮先隐藏选择连接菜单
        if (connectionsMenuManager.getConnectionsMenu() != null) {
            dismissMenu();
            return;
        }
        super.onClick(v);
        if (v == btnSelectConns) {
            if (configDialogManager.getMyConfigDialog() != null)
                return;
            showConnectionsMenu();
        }
    }

    private void showConnectionsMenu() {
        if (tcpServer == null || tcpServer.getConnectsList().size() == 0) {
            Utils.showToast(getActivity(), R.string.no_connections);
            return;
        }
        canTouched = !canTouched;
        Utils.hideInputMethodWindow(getActivity());
        connectionsMenuManager.toggleContextMenuFromView(tcpServer.getConnectsList(), btnSelectConns, new ConnectionSelectAdapter.ConnectionsOnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                System.out.println("showConnectionsMenu-------------->onItemSelected:" + position);
                dismissMenu();
                currentConnection = tcpServer.getConnectsList().get(position);
                ConnectConfiguration cc = currentConnection.getConfiguration();
                tvConnState.setText(cc.getHost() + ":" + cc.getPort());
            }
        });
    }


    @Override
    public void connectSuccess(ConnectConfiguration configuration) {

    }

    @Override
    public void connectBreak(ConnectConfiguration configuration) {
        dismissMenu();
        if (currentConnection == null)
            return;
        ConnectConfiguration currentConfiguration = currentConnection.getConfiguration();
        if (currentConfiguration.getHost().equals(configuration.getHost()) &&
                currentConfiguration.getPort() == configuration.getPort()) {
            currentConnection = null;
            refreshConnectState(true);
        }

        //停止定时发送
        stopTimer();
    }

    @Override
    public void onReceviceData(ConnectConfiguration configuration, byte[] data) {
        if (currentConnection == null)
            return;
        ConnectConfiguration currentConfiguration = currentConnection.getConfiguration();
        if (currentConfiguration.getHost().equals(configuration.getHost()) &&
                currentConfiguration.getPort() == configuration.getPort()) {
            String msgInfo = configuration.getHost() + ":" + configuration.getPort();
            String strMsg = null;
            if (isHexDisplay)
                strMsg = StringUtils.bytesToHexString(data);
            else{
                try {
                    strMsg = new String(data,"gb-2312");
                }catch (Exception ex){
                    strMsg = new String(data);
                }
            }

            Message newMsg = new Message(Message.MESSAGE_TYPE.RECEIVE,strMsg, msgInfo);
            getMsgsList().add(newMsg);
            getMsgAdapter().notifyLastItem();
            rvMsg.smoothScrollToPosition(getMsgAdapter().getItemCount() - 1);
        }
    }

    @Override
    public void onAdd(Connect connect) {
        System.out.println("onAdd----------------------->connect:" + connect.getConfiguration().toString());
        dismissMenu();
    }


    private void refreshConnectState(boolean isConnected) {
        if (isConnected) {
            tvConnState.setText(R.string.listening);
            btnConnOption.setText(R.string.conn_break);
        } else {
            tvConnState.setText(R.string.not_listening);
            btnConnOption.setText(R.string.connect);
        }
    }

    @Override
    protected void onMySizeChanged(int w, int h, int oldw, int oldh) {

        if (configDialogManager.getMyConfigDialog() != null) {
            AnimateUtils.translationYBy(configDialogManager.getMyConfigDialog(), (h - oldh) / 2, 300, 0);
        }

        if (h > oldh){
            return;
        }

        if (isShow() ){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOrDismissConfigView();
                }
            }, 200);
        }
    }



    private void showTimerDialog(){
        if (currentConnection == null){
            Utils.showToast(getActivity(), R.string.connect_first);
            setConfigItemUnSelected(3);
            showOrDismissConfigView();
            stopTimer();
            return;
        }

        String sendStr = etSend.getText().toString();
        if (TextUtils.isEmpty(sendStr)){
            Utils.showToast(getActivity(), R.string.input_send_content);
            setConfigItemUnSelected(3);
            showOrDismissConfigView();
            stopTimer();
            return;
        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_timer,null,false);
        final EditText etInputTime = (EditText)view.findViewById(R.id.et_input_time);
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.set_a_time)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isTimeDialogClicked = true;
                        String text = etInputTime.getText().toString();
                        if (TextUtils.isEmpty(text) || Integer.parseInt(text) < 50) {
                            Utils.showToast(getActivity(), R.string.time_invalid);
                            setConfigItemUnSelected(3);
                        } else {
                            startTimer(Integer.parseInt(text));
                        }
                        if (isShow())
                            showOrDismissConfigView();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isTimeDialogClicked = true;
                        setConfigItemUnSelected(3);
                        if (isShow())
                            showOrDismissConfigView();
                    }
                }).create();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isTimeDialogClicked)
                    isTimeDialogClicked = false;
                else{
                    setConfigItemUnSelected(3);
                }
            }
        });

        dialog.show();
    }


    private void setConfigItemUnSelected(int position){
        ConfigItem item = getConfigItemList().get(position);
        if (item.isSelected()){
            item.setSelected(false);
            getConfigItemAdapter().notifyItemChanged(position);
        }
    }


    private void startTimer(long interval){
        etSend.setEnabled(false);
        ibtSend.setEnabled(false);

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(TIME_SEND);
            }
        };

        timer.schedule(timerTask, interval, interval);
    }


    private void stopTimer(){
        etSend.setEnabled(true);
        ibtSend.setEnabled(true);

        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }


    @Override
    public boolean onBackPressed() {
        if (connectionsMenuManager.getConnectionsMenu() != null) {
            dismissMenu();
            return true;
        }

        if(isShow()){
            showOrDismissConfigView();
            return true;
        }


        return false;
    }

    private void dismissMenu() {
        if (connectionsMenuManager.getConnectionsMenu() != null) {
            canTouched = true;
            connectionsMenuManager.toggleContextMenuFromView(null, null, null);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.connectManager.stopTcpServer();
    }
}
