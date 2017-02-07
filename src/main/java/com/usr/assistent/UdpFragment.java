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
import android.widget.EditText;

import com.usr.assistent.bean.ConfigItem;
import com.usr.assistent.bean.Message;
import com.usr.assistent.utils.AnimateUtils;
import com.usr.assistent.utils.StringUtils;
import com.usr.assistent.utils.Utils;
import com.usr.assistent.utils.WifiUtils;
import com.usr.assistent.views.MyConfigDialog;
import com.usr.assistent.views.MyConfigDialogManager;
import com.usr.net.BaseConnectAdapter;
import com.usr.net.bean.ConnectConfiguration;
import com.usr.net.bean.UdpConnect;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015-07-30.
 */
public class UdpFragment extends CommunicationBaseFragment implements CommunicationBaseFragment.OnButtonClickListener {
    private final String UDP_IP = "udp_ip";
    private final String UDP_LOCAL_PORT = "udp_local_port";
    private final String UDP_TARGET_PORT = "udp_target_port";
    private UdpConnect connect;
    private int targetPort=-1;
    private int localPort=-1;
    private String ip = "";
    private MyConfigDialogManager configDialogManager;
    private boolean isSendHex;
    private boolean isHexDisplay;
    private String localIp = "localhost";
    private Timer timer;

    private final int TIME_SEND = 0 ;

    private boolean isTmeiDialogClicked = false;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnButtonClickListener(this);
        configDialogManager = MyConfigDialogManager.getInstance();

        setConfigsOnItemClickListener(new ConfigsOnItemClickListener() {
            @Override
            public void onItemClick(int position,boolean isSelected) {
                switch (position){
                    case 0:
                        getMsgsList().clear();
                        getMsgAdapter().notifyDataSetChanged();
                        showOrDismissConfigView();
                        Utils.hideInputMethodWindow(getActivity());
                        break;
                    case 1:
                        isHexDisplay = isSelected;
                        break;
                    case 2:
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
                //触摸滑动的时候才将configDialog下滑
                if (isShow()) {
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
    protected String setFragmentTitle() {
        return getString(R.string.udp_fragment);
    }

    @Override
    protected int setConnectStateIcon() {
        return R.mipmap.btn_uc_hl;
    }

    @Override
    protected List<ConfigItem> initConfigItem() {
        List<ConfigItem> configItemList = new ArrayList<>();
        ConfigItem item0 = new ConfigItem(new int[]{R.mipmap.btn_clear_text_n, R.mipmap.btn_clear_text_n},
                new int[]{getResources().getColor(R.color.config_item_normal), getResources().getColor(R.color.config_item_normal)},
                getString(R.string.clear), ConfigItem.CONFIG_TYPE.CLEAR_TEXT);

        ConfigItem item1 = new ConfigItem(new int[]{R.mipmap.btn_hex_display_n,R.mipmap.btn_hex_display_hl},
                new int[]{getResources().getColor(R.color.config_item_hl), getResources().getColor(R.color.config_item_normal)},
                getString(R.string.hex_display), ConfigItem.CONFIG_TYPE.HEX_DISPLAY);

        ConfigItem item2 = new ConfigItem(new int[]{R.mipmap.btn_hex_send_n,R.mipmap.btn_hex_send_hl},
                new int[]{getResources().getColor(R.color.config_item_hl), getResources().getColor(R.color.config_item_normal)},
                getString(R.string.hex_send), ConfigItem.CONFIG_TYPE.HEX_SEND);

        ConfigItem item3 = new ConfigItem(new int[]{R.mipmap.btn_timer_n,R.mipmap.btn_timer_hl},
                new int[]{getResources().getColor(R.color.config_item_hl), getResources().getColor(R.color.config_item_normal)},
                getString(R.string.timer_send), ConfigItem.CONFIG_TYPE.TIMER_SEND);

        configItemList.add(item0);configItemList.add(item1);
        configItemList.add(item2);configItemList.add(item3);

        return configItemList;
    }

    @Override
    protected boolean isStartIntroduceAnimation() {
        return false;
    }

    @Override
    public void onSendClick() {
        if (connect == null){
            Utils.showToast(getActivity(),R.string.connect_first);
            return;
        }

        String sendStr = etSend.getText().toString();
        if (TextUtils.isEmpty(sendStr)){
            AnimateUtils.shake(etSend);
            return;
        }
        if (!isSendHex){
            try {
                connect.send(sendStr.getBytes("gb-2312"));
            }catch (Exception ex){
                connect.send(sendStr.getBytes());
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
                connect.send(data);
                sendStr = StringUtils.bytesToHexString(data);
            }else
                return;
        }

        Message newMsg = new Message(Message.MESSAGE_TYPE.SEND,sendStr,localIp);
        getMsgsList().add(newMsg);
        getMsgAdapter().notifyLastItem();
        rvMsg.smoothScrollToPosition(getMsgAdapter().getItemCount() - 1);
    }

    @Override
    public void onConfigMenuClick() {
        if (configDialogManager.getMyConfigDialog() != null)
            return;
        if (connect != null){
            Utils.showToast(getActivity(), R.string.break_connect_first);
            return;
        }
        canTouched = false;
        Utils.hideInputMethodWindow(getActivity());

        ip = AndroidSharedPreferences.getString(UDP_IP,"");
        String preLocPort = AndroidSharedPreferences.getString(UDP_LOCAL_PORT,"-1");
        String prePort = AndroidSharedPreferences.getString(UDP_TARGET_PORT, "-1");
        targetPort = Integer.parseInt(prePort);
        localPort = Integer.parseInt(preLocPort);

        String portStr = targetPort == -1 ?"":prePort;
        String localPortStr = localPort == -1 ?"":preLocPort;
        configDialogManager.toggleContextMenuFromView(ip, portStr, localPortStr, getConfigMenuItem().getActionView(), new MyConfigDialog.OnOptionClickListener() {
            @Override
            public void onOkClick() {
                EditText etIp = configDialogManager.getMyConfigDialog().getEtConfigIp();
                EditText etPort = configDialogManager.getMyConfigDialog().getEtConfigPort();
                EditText etLocalPort = configDialogManager.getMyConfigDialog().getEtConfigLocalPort();
                String ipInput = etIp.getText().toString();
                String portInput = etPort.getText().toString();
                String localPortInput = etLocalPort.getText().toString();

                if (TextUtils.isEmpty(ipInput.trim())) {
                    AnimateUtils.shake(etIp);
                    return;
                }

                if (TextUtils.isEmpty(portInput.trim()) || Integer.parseInt(portInput) > 65535) {
                    AnimateUtils.shake(etPort);
                    return;
                }

                if (TextUtils.isEmpty(localPortInput.trim()) || Integer.parseInt(localPortInput) > 65535) {
                    AnimateUtils.shake(etLocalPort);
                    return;
                }

                configDialogManager.toggleContextMenuFromView(null, null, null, null);
                canTouched = true;
                ip = ipInput;
                targetPort = Integer.parseInt(portInput);
                localPort = Integer.parseInt(localPortInput);

                AndroidSharedPreferences.putString(UDP_IP,ip);
                AndroidSharedPreferences.putString(UDP_LOCAL_PORT,localPortInput);
                AndroidSharedPreferences.putString(UDP_TARGET_PORT,portInput);
            }

            @Override
            public void onCancelClick() {
                configDialogManager.toggleContextMenuFromView(null, null, null, null);
                canTouched = true;
            }
        });
    }

    @Override
    public void onConnOptionCLick() {
       if (connect != null)
            connect.breakConnect();
       else{
           //经测试发现，udp端口号不能使用小于1000附件的端口号，因此做限制端口号都>=2000
           if (TextUtils.isEmpty(ip) || targetPort <2000 || localPort<2000){
               Utils.showToast(getActivity(),R.string.config_error);
               return;
           }
           connect = MainActivity.connectManager.createUdp(ip,targetPort,localPort, new BaseConnectAdapter() {
               @Override
               public void onReceviceData(ConnectConfiguration configuration, byte[] data) {
                   System.out.println("receivedData------------------>" + data);
                   String msgInfo = configuration.getHost()+":"+configuration.getPort();
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

                   Message newMsg = new Message(Message.MESSAGE_TYPE.RECEIVE,strMsg,msgInfo);
                   getMsgsList().add(newMsg);
                   getMsgAdapter().notifyLastItem();
                   rvMsg.smoothScrollToPosition(getMsgAdapter().getItemCount()-1);
               }

               @Override
               public void connectBreak(ConnectConfiguration configuration) {
                  refreshConnectState(false);

                  //停止定时发送
                  stopTimer();

                  Utils.showToast(getActivity(),R.string.connect_has_break);
                  connect = null;
               }
           });

           localIp = WifiUtils.getIp(getActivity());
           showLocalIpInfo(localIp);
           refreshConnectState(true);
       }
    }


    private void showLocalIpInfo(String info){
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.local_connection_info)
                .setMessage(info)
                .setPositiveButton("OK",null)
                .create();
        dialog.show();
    }

    private void refreshConnectState(boolean isConnected){
        if (isConnected){
            tvConnState.setText(R.string.listening);
            btnConnOption.setText(R.string.conn_break);
        }else {
            tvConnState.setText(R.string.not_listening);
            btnConnOption.setText(R.string.connect);
        }
    }


    @Override
    protected void onMySizeChanged(int w, int h, int oldw, int oldh) {


        if (configDialogManager.getMyConfigDialog() != null){
            AnimateUtils.translationYBy(configDialogManager.getMyConfigDialog(), (h - oldh) / 2, 300, 0);
        }

        if (h > oldh){
            return;
        }

        if (isShow()){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOrDismissConfigView();
                }
            }, 200);
        }
    }


    private void showTimerDialog(){
        if (connect == null){
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
                        isTmeiDialogClicked = true;
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
                        isTmeiDialogClicked = true;
                        setConfigItemUnSelected(3);
                        if (isShow())
                            showOrDismissConfigView();
                    }
                }).create();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
               if (isTmeiDialogClicked)
                   isTmeiDialogClicked = false;
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

        timer.schedule(timerTask,interval,interval);
        showOrDismissConfigView();
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
        if (isShow()){
            showOrDismissConfigView();
            return true;
        }
        return false;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (connect != null){
            connect.breakConnect();
        }
    }
}
