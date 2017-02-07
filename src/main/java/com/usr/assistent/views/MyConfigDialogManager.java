package com.usr.assistent.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.usr.assistent.utils.Utils;

/**
 * Created by Administrator on 2015-08-04.
 */
public class MyConfigDialogManager implements View.OnAttachStateChangeListener {
    private static MyConfigDialogManager instance;
    private MyConfigDialog myConfigDialog;
    private boolean isDialogDismissing;
    private boolean isDialogShowing;
    private int myLayerType;

    public static int TYPE_TCP_SERVER = 0;
    public static int TYPE_TCP_CLIENT =1;
    public static int TYPE_UDP = 2;
    private int type;

    public static MyConfigDialogManager getInstance() {
        if (instance == null) {
            instance = new MyConfigDialogManager();
        }
        return instance;
    }

    private MyConfigDialogManager() {
    }



    public void toggleContextMenuFromView(String port,View openingView, MyConfigDialog.OnOptionClickListener listener) {
        if (myConfigDialog == null) {
            showConfigDialog(port,openingView, listener);
        } else {
            dismissConfigDialog();
        }
    }


    public void toggleContextMenuFromView(String ip,String port,View openingView, MyConfigDialog.OnOptionClickListener listener) {
        if (myConfigDialog == null) {
            showConfigDialog(ip,port,openingView, listener);
        } else {
            dismissConfigDialog();
        }
    }


    public void toggleContextMenuFromView(String ip,String port,String localPort,View openingView, MyConfigDialog.OnOptionClickListener listener) {
        if (myConfigDialog == null) {
            showConfigDialog(ip,port,localPort,openingView, listener);
        } else {
            dismissConfigDialog();
        }
    }


    private void showConfigDialog(String port,final View openingView, MyConfigDialog.OnOptionClickListener listener) {
        if (!isDialogShowing) {
            isDialogShowing = true;
            myConfigDialog = new MyConfigDialog(openingView.getContext(),port);
            initConfigDialog(openingView,listener);
        }
    }


    private void showConfigDialog(String ip,String port,final View openingView, MyConfigDialog.OnOptionClickListener listener) {
        if (!isDialogShowing) {
            isDialogShowing = true;
            myConfigDialog = new MyConfigDialog(openingView.getContext(),ip,port);
            initConfigDialog(openingView,listener);
        }
    }


    private void showConfigDialog(String ip,String port,String localPort,final View openingView, MyConfigDialog.OnOptionClickListener listener) {
        if (!isDialogShowing) {
            isDialogShowing = true;
            myConfigDialog = new MyConfigDialog(openingView.getContext(),ip,port,localPort);
            initConfigDialog(openingView,listener);
        }
    }

    private void initConfigDialog(final View openingView, MyConfigDialog.OnOptionClickListener listener){
        myConfigDialog.addOnAttachStateChangeListener(this);
        myConfigDialog.setOnOptionClickListener(listener);
        ((ViewGroup) openingView.getRootView().findViewById(android.R.id.content)).addView(myConfigDialog);
        myConfigDialog.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                myConfigDialog.getViewTreeObserver().removeOnPreDrawListener(this);
                setupContextMenuInitialPosition();
                performShowAnimation();
                return false;
            }
        });
    }


    private void setupContextMenuInitialPosition() {
        final int[] location = new int[2];
        location[0] = Utils.getScreenWidth(myConfigDialog.getContext())-Utils.dpToPx(20);
        location[1] = Utils.dpToPx(20);

        myConfigDialog.setTranslationX(location[0]-myConfigDialog.getWidth());
        myConfigDialog.setTranslationY(location[1]);
    }

    private void performShowAnimation() {
        int localX = Utils.getScreenWidth(myConfigDialog.getContext())/2-myConfigDialog.getWidth()/2;
        int localY = Utils.getScreenHeight(myConfigDialog.getContext()) / 2 - myConfigDialog.getHeight()/2;
        myConfigDialog.setScaleX(0.f);
        myConfigDialog.setScaleY(0.f);
        myConfigDialog.setAlpha(0.0f);
        myConfigDialog.setPivotX(myConfigDialog.getWidth());
        myConfigDialog.setPivotY(0);



        myConfigDialog.animate()
                .scaleX(1f).scaleY(1f)
                .alpha(1.0f)
                .translationX(localX)
                .translationY(localY)
                .setDuration(300)
                .setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        myLayerType = myConfigDialog.getLayerType();
                        myConfigDialog.setLayerType(View.LAYER_TYPE_HARDWARE,null);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        myConfigDialog.setLayerType(myLayerType,null);
                        isDialogShowing = false;
                    }
                });
    }


    private void dismissConfigDialog() {
        if (!isDialogDismissing) {
            isDialogDismissing = true;
            performDismissAnimation();
        }
    }


    private void performDismissAnimation() {
        final int[] location = new int[2];
        location[0] = Utils.dpToPx(20);
        location[1] = Utils.dpToPx(20);
        myConfigDialog.setPivotX(myConfigDialog.getWidth());
        myConfigDialog.setPivotY(0);
        myConfigDialog.animate()
                .scaleX(0.0f).scaleY(0.0f)
                .setDuration(300)
                .translationX(location[0])
                .translationY(location[1])
                .alpha(0.0f)
                .setInterpolator(new AccelerateInterpolator())
                .setStartDelay(100)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        myLayerType = myConfigDialog.getLayerType();
                        myConfigDialog.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        myConfigDialog.setLayerType(myLayerType,null);
                        if (myConfigDialog != null) {
                            myConfigDialog.dismiss();
                        }
                        isDialogDismissing = false;
                    }
                });
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        myConfigDialog = null;
    }

    public MyConfigDialog getMyConfigDialog() {
        return myConfigDialog;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
