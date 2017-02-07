package com.usr.assistent.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.usr.assistent.adapter.ConnectionSelectAdapter;
import com.usr.assistent.utils.Utils;
import com.usr.net.bean.Connect;

import java.util.List;

/**
 * Created by liu on 15/8/9.
 */
public class ConnectionsMenuManager implements View.OnAttachStateChangeListener{
    private static ConnectionsMenuManager instance;
    private SelectConnectionsMenu connectionsMenu;
    private boolean isShowing;
    private boolean isDismissing;


    public static ConnectionsMenuManager getInstance() {
        if (instance == null) {
            instance = new ConnectionsMenuManager();
        }
        return instance;
    }

    private ConnectionsMenuManager() {
    }

    public void toggleContextMenuFromView(List<Connect> list,View openingView,ConnectionSelectAdapter.ConnectionsOnItemSelectedListener listener) {
        if (connectionsMenu == null) {
            showConnectionsMenu(list, openingView, listener);
        } else {
            dismissConfigDialog();
        }
    }





    private void showConnectionsMenu(List<Connect> list,View openingView,ConnectionSelectAdapter.ConnectionsOnItemSelectedListener listener) {
        if (!isShowing) {
            isShowing = true;
            connectionsMenu = new SelectConnectionsMenu(openingView.getContext(),list,listener);
            initConfigDialog(openingView);
        }
    }




    private void initConfigDialog(final View openingView){
        connectionsMenu.addOnAttachStateChangeListener(this);
        ((ViewGroup) openingView.getRootView().findViewById(android.R.id.content)).addView(connectionsMenu);
        connectionsMenu.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                connectionsMenu.getViewTreeObserver().removeOnPreDrawListener(this);
                setupContextMenuInitialPosition(openingView);
                performShowAnimation();
                return false;
            }
        });
    }


    private void setupContextMenuInitialPosition(View openingView) {
        final int[] location = new int[2];
        openingView.getLocationOnScreen(location);
        int additionalTopMargin = Utils.dpToPx(10);

        connectionsMenu.setTranslationX(location[0]+openingView.getWidth()/2-connectionsMenu.getWidth()/2);
        connectionsMenu.setTranslationY(location[1]+additionalTopMargin);
    }

    private void performShowAnimation() {
        connectionsMenu.setPivotX(connectionsMenu.getWidth() / 2);
        connectionsMenu.setPivotY(0);
        connectionsMenu.setScaleX(0.1f);
        connectionsMenu.setScaleY(0.1f);
        connectionsMenu.animate()
                .scaleX(1f).scaleY(1f)
                .setDuration(150)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isShowing = false;
                    }
                });
    }


    private void dismissConfigDialog() {
        if (!isDismissing) {
            isDismissing = true;
            performDismissAnimation();
        }
    }


    private void performDismissAnimation() {
        connectionsMenu.setPivotX(connectionsMenu.getWidth() / 2);
        connectionsMenu.setPivotY(0);
        connectionsMenu.animate()
                .scaleX(0.1f).scaleY(0.1f)
                .setDuration(150)
                .setInterpolator(new AccelerateInterpolator())
                .setStartDelay(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (connectionsMenu != null) {
                            connectionsMenu.dismiss();
                        }
                        isDismissing = false;
                    }
                });
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        connectionsMenu = null;
    }

    public SelectConnectionsMenu getConnectionsMenu() {
        return connectionsMenu;
    }
}
