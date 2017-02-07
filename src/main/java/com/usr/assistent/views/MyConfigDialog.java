package com.usr.assistent.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.usr.assistent.R;
import com.usr.assistent.utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015-08-04.
 */
public class MyConfigDialog extends CardView {
    public static int TYPE_TCP_SERVER = 0;
    public static int TYPE_TCP_CLIENT =1;
    public static int TYPE_UDP = 2;

    private int type = TYPE_TCP_CLIENT;

    private OnOptionClickListener onOptionClickListener;

    @InjectView(R.id.et_config_ip)
    EditText etConfigIp;
    @InjectView(R.id.et_config_port)
    EditText etConfigPort;
    @InjectView(R.id.et_config_localPort)
    EditText etConfigLocalPort;

    @InjectView(R.id.met_config_ip)
    MyEditTextLayout ipMyET;
    @InjectView(R.id.met_config_port)
    MyEditTextLayout portMyET;
    @InjectView(R.id.met_config_localport)
    MyEditTextLayout localPortMyET;

    private String ip="";
    private String port="";
    private String localPort = "";


    public MyConfigDialog(Context context,String port) {
        super(context);
        this.port = port;
        type = TYPE_TCP_SERVER;
        init();
    }

    public MyConfigDialog(Context context,String ip,String port) {
        super(context);
        this.ip = ip;
        this.port = port;
        type = TYPE_TCP_CLIENT;
        init();
    }

    public MyConfigDialog(Context context,String ip,String port,String localPort) {
        super(context);
        this.ip = ip;
        this.port = port;
        this.localPort = localPort;
        type = TYPE_UDP;
        init();
    }

    public MyConfigDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyConfigDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        int dialogWidth = Utils.getScreenWidth(getContext())- 2*Utils.dpToPx(20);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_config, this, true);
        setRadius(Utils.dpToPx(3));
        setLayoutParams(new LayoutParams(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);

        etConfigIp.setText(ip);
        etConfigPort.setText(port);
        etConfigPort.setHint(R.string.input_local_port_2000);
        etConfigLocalPort.setText(localPort);
        etConfigLocalPort.setHint(R.string.input_local_port_2000);

        if (type == TYPE_TCP_SERVER){
            ipMyET.setVisibility(View.GONE);
            localPortMyET.setVisibility(View.GONE);
            etConfigPort.setHint(R.string.input_port);
        }else if (type == TYPE_TCP_CLIENT){
            localPortMyET.setVisibility(View.GONE);
            etConfigPort.setHint(R.string.input_port);
        }
    }


    public void dismiss(){
        ((ViewGroup) getParent()).removeView(MyConfigDialog.this);
    }


    @OnClick(R.id.btn_config_cancel)
    public void onCancelClick(){
        if (onOptionClickListener != null)
            onOptionClickListener.onCancelClick();
    }

    @OnClick(R.id.btn_config_ok)
    public void onOkClick(){
        if (onOptionClickListener != null)
            onOptionClickListener.onOkClick();
    }


    public void setOnOptionClickListener(OnOptionClickListener onOptionClickListener) {
        this.onOptionClickListener = onOptionClickListener;
    }

    public interface OnOptionClickListener{
        public void onOkClick();
        public void onCancelClick();
    }

    public EditText getEtConfigIp() {
        return etConfigIp;
    }

    public EditText getEtConfigPort() {
        return etConfigPort;
    }

    public EditText getEtConfigLocalPort() {
        return etConfigLocalPort;
    }
}
