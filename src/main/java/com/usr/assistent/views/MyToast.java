package com.usr.assistent.views;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.usr.assistent.R;

/**
 * Created by Administrator on 2015/7/10.
 */
public class MyToast {
    private Context context ;
    private static MyToast instance;
    private Toast toast;
    private TextView tvToastMsg;
    private MyToast(Context context){
        this.context = context;
        init();
    }

    private void init(){
        if (context == null)
            return;
        View tView = LayoutInflater.from(context).inflate(
                R.layout.view_toast, null);
        tvToastMsg = (TextView) tView.findViewById(R.id.tv_toast_msg);

        toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);

        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(tView);
    }


    public static MyToast getInstance(Context context){
        if (instance == null){
            instance = new MyToast(context);
        }

        return instance;
    }


    public void showToast(int resId){
        String text = context.getString(resId);
        showToast(text);
    }

    public void showToast(String text){
        tvToastMsg.setText(text);
        toast.show();
    }
}
