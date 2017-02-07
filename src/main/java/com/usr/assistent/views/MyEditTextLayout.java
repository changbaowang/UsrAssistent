package com.usr.assistent.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Created by LJQ on 2015/6/29.
 */
public class MyEditTextLayout extends RelativeLayout{

    private View title;
    private EditText value;
    private final int TITLE_STATE_GONE = 0;
    private final int TITLE_STATE_SHOWING = 1;
    private final int TITLE_STATE_DISMISSING = 2;
    private final int TITLE_STATE_VISIBILITY = 3;

    private int currentState = TITLE_STATE_GONE;

    private int titleHeight ;

    private boolean isInited = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyEditTextLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MyEditTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyEditTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditTextLayout(Context context) {
        super(context);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        title = getChildAt(0);
        value = (EditText) getChildAt(1);
        title.setAlpha(0.f);

        value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeTtileState(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }




    private void changeTtileState(CharSequence s){
        if (currentState == TITLE_STATE_GONE){
            if (s.length() == 0)
                  return;
            showTitle();

        }else if(currentState == TITLE_STATE_SHOWING){

        }else if(currentState == TITLE_STATE_DISMISSING){

        }else if (currentState == TITLE_STATE_VISIBILITY){
            if (s.length() == 0 )
                dismissTitle();
        }
    }


    private void showTitle(){
        if (!isInited){
            title.setTranslationY(titleHeight);
            isInited = true;
        }

        currentState = TITLE_STATE_SHOWING;

        title.animate().alpha(1.f)
             .translationY(10)
             .setDuration(200)
             .setInterpolator(new LinearInterpolator())
             .start();

        postDelayed(new Runnable() {
            @Override
            public void run() {
               currentState = TITLE_STATE_VISIBILITY;
            }
        },300);
    }



    private void dismissTitle(){
        title.animate().alpha(0.f)
                .translationY(titleHeight)
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .start();

        currentState = TITLE_STATE_DISMISSING;

        postDelayed(new Runnable() {
            @Override
            public void run() {
                currentState = TITLE_STATE_GONE;
            }
        },300);
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //onFinishInflate 中还无法获得高度，只有通过onMeasure()方法测量后才可以
        // 因此我们在onLayout中获得高度
        titleHeight = title.getHeight();
        super.onLayout(changed, l, t, r, b);
    }


}
