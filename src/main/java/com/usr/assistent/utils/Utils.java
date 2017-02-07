package com.usr.assistent.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.usr.assistent.views.MyToast;

/**
 * Created by Administrator on 2015-07-29.
 */
public class Utils {

    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static MyToast myToast;

    public static int dpToPx(int dp){
        return  (int)(dp* Resources.getSystem().getDisplayMetrics().density);
    }


    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }


    // 关闭输入法
    public static void hideInputMethodWindow(Activity act) {
        InputMethodManager imm = (InputMethodManager) act
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    public static void hideInputMethodWindow(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showToast(Context context,String str){
        if (myToast == null)
            myToast = MyToast.getInstance(context);
        myToast.showToast(str);
    }

    public static void showToast(Context context,int strId){
        if (myToast == null)
            myToast = MyToast.getInstance(context);
        myToast.showToast(strId);
    }
}
