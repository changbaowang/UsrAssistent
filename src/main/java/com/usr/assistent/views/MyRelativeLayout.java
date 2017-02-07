package com.usr.assistent.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2015-08-07.
 */
public class MyRelativeLayout extends RelativeLayout {
    private OnSizeChangedListener onSizeChangedListener;

    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (onSizeChangedListener != null)
            onSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
    }


    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        this.onSizeChangedListener = onSizeChangedListener;
    }


    public interface OnSizeChangedListener{
        public void onSizeChanged(int w, int h, int oldw, int oldh);
    }

}
