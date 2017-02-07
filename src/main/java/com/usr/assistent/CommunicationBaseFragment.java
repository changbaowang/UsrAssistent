package com.usr.assistent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.usr.assistent.adapter.ConfigItemAdapter;
import com.usr.assistent.adapter.MessagesAdapter;
import com.usr.assistent.bean.ConfigItem;
import com.usr.assistent.bean.Message;
import com.usr.assistent.utils.AnimateUtils;
import com.usr.assistent.utils.Utils;
import com.usr.assistent.views.MyRelativeLayout;
import com.usr.assistent.views.RevealBackgroundView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by Administrator on 2015-07-22.
 */
public abstract class CommunicationBaseFragment extends BaseFragment implements
        RevealBackgroundView.OnStateChangeListener {
    public static final int ANIM_DURATION_TOOLBAR = 300;
    public static final int ANIM_DURATION_FAB = 400;
    public static final int ANIM_DURATION_TRANSLATIONX = 500;

    @InjectView(R.id.myrl_root)
    MyRelativeLayout myRelativeLayout;

    @InjectView(R.id.revealBackgroundView)
    RevealBackgroundView revealBackgroundView;

    @InjectView(R.id.view_conn_state)
    View viewConnState;
    @InjectView(R.id.iv_conn_state)
    ImageView ivConnState;
    @InjectView(R.id.tv_conn_state)
    TextView tvConnState;
    @InjectView(R.id.btn_conn_option)
    Button btnConnOption;


    @InjectView(R.id.rv_msg)
    RecyclerView rvMsg;


    @InjectView(R.id.view_send_bottom)
    View viewSendBottom;
    @InjectView(R.id.ibt_send_config)
    ImageButton ibtSendConfig;
    @InjectView(R.id.ibt_send)
    ImageButton ibtSend;
    @InjectView(R.id.et_send)
    EditText etSend;
    @InjectView(R.id.rv_send_config)
    RecyclerView rvSendConfig;

    private boolean startAnimation;

    private final List<Message> msgsList = new ArrayList<>();
    private final List<ConfigItem> configItemList = new ArrayList<>();
    private MessagesAdapter msgAdapter;
    private ConfigItemAdapter configItemAdapter;

    private boolean show;

    private OnButtonClickListener onButtonClickListener;
    private ConfigsOnItemClickListener configsOnItemClickListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getTvToolbarTitle().setText(setFragmentTitle());
        ivConnState.setImageResource(setConnectStateIcon());

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rvMsg.setLayoutManager(llm);
        msgAdapter = new MessagesAdapter(getActivity(), msgsList);
        rvMsg.setAdapter(msgAdapter);


        List<ConfigItem> list = initConfigItem();
        if (list != null) {
            configItemList.addAll(list);
        }

        GridLayoutManager glm = new GridLayoutManager(getActivity(), 3);
        rvSendConfig.setLayoutManager(glm);
        rvSendConfig.setOverScrollMode(View.OVER_SCROLL_NEVER);
        configItemAdapter = new ConfigItemAdapter(getActivity(), configItemList);
        rvSendConfig.setAdapter(configItemAdapter);
        configItemAdapter.setOnItemClickListener(new ConfigItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ConfigItem item = configItemList.get(position);
                item.setSelected(!item.isSelected());
                if (configsOnItemClickListener != null)
                    configsOnItemClickListener.onItemClick(position,item.isSelected());
            }
        });


        myRelativeLayout.setOnSizeChangedListener(new MyRelativeLayout.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                onMySizeChanged(w, h, oldw, oldh);
            }
        });


        revealBackgroundView.setOnStateChangeListener(this);

        ibtSendConfig.setOnClickListener(this);
        ibtSend.setOnClickListener(this);
        btnConnOption.setOnClickListener(this);

        if (savedInstanceState == null) {
            startAnimation = isStartIntroduceAnimation();
        } else {
            startAnimation = false;
        }

        if (!startAnimation){
            int actionbarSize = Utils.dpToPx(56);
            viewSendBottom.setTranslationY(actionbarSize);
            ibtSendConfig.setTranslationY(actionbarSize);
            etSend.setTranslationY(actionbarSize);
            ibtSend.setTranslationY(actionbarSize);
        }

    }


    /**
     * Set fragment's title
     *
     * @return
     */
    protected abstract String setFragmentTitle();

    protected abstract int setConnectStateIcon();

    protected void onMySizeChanged(int w, int h, int oldw, int oldh){

    }

    /**
     * Custom send config.
     * These configs will be shown at the bottom,and can be selected;
     */
    protected abstract List<ConfigItem> initConfigItem();

    /**
     * Wheater play animation when this fragment is first shown
     *
     * @return
     */
    protected abstract boolean isStartIntroduceAnimation();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (startAnimation) {
            startAnimation = false;
            startAnimation();
        }
    }

    /**
     * This will be invoked when the app launched
     */
    private void startAnimation() {
        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getTvToolbarTitle().setTranslationY(-actionbarSize);
        getConfigMenuItem().getActionView().setTranslationY(-actionbarSize);

        viewSendBottom.setTranslationY(actionbarSize);
        ibtSendConfig.setTranslationY(actionbarSize);
        etSend.setTranslationY(actionbarSize);
        ibtSend.setTranslationY(actionbarSize);

        AnimateUtils.translationY(getToolbar(), 0, ANIM_DURATION_TOOLBAR, 300);
        AnimateUtils.translationY(getTvToolbarTitle(), 0, ANIM_DURATION_TOOLBAR, 400);
        AnimateUtils.translationY(getConfigMenuItem().getActionView(), 0, ANIM_DURATION_TOOLBAR, 500,
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewStateAnimation();
                    }
                });


        AnimateUtils.translationY(viewSendBottom, 0, ANIM_DURATION_TOOLBAR, 300);
        AnimateUtils.translationY(ibtSendConfig, 0, ANIM_DURATION_TOOLBAR, 400);
        AnimateUtils.translationY(etSend, 0, ANIM_DURATION_TOOLBAR, 500);
        AnimateUtils.translationY(ibtSend, 0, ANIM_DURATION_TOOLBAR, 600);
    }


    /**
     * this method will be invoked when change the different fragment
     */
    public void startChangeFragmentAnimation() {
        if (revealBackgroundView.getState()  != RevealBackgroundView.STATE_NOT_STARTED)
            return;

        if (revealBackgroundView.getVisibility() == View.GONE)
            return;

        viewStateAnimation();
        AnimateUtils.translationY(viewSendBottom, 0, ANIM_DURATION_TOOLBAR, 300);
        AnimateUtils.translationY(ibtSendConfig, 0, ANIM_DURATION_TOOLBAR, 400);
        AnimateUtils.translationY(etSend, 0, ANIM_DURATION_TOOLBAR, 500);
        AnimateUtils.translationY(ibtSend, 0, ANIM_DURATION_TOOLBAR, 600);
    }


    protected void viewStateAnimation() {
        viewConnState.setVisibility(View.VISIBLE);
        viewConnState.setPivotY(0);
        viewConnState.setPivotX(viewConnState.getWidth() / 2);
        viewConnState.setRotationX(270);

        ivConnState.setTranslationX(-Utils.dpToPx(50));
        tvConnState.setTranslationX(-Utils.dpToPx(150));
        btnConnOption.setTranslationX(Utils.dpToPx(150));

        viewConnState.animate().rotationXBy(90)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        revealBackgroundView.startFromLocation(new int[]{revealBackgroundView.getWidth() / 2, 0});
                    }
                })
                .start();

        AnimateUtils.translationX(ivConnState, 0, ANIM_DURATION_TRANSLATIONX, 400);
        AnimateUtils.translationX(tvConnState, 0, ANIM_DURATION_TRANSLATIONX, 500);
        AnimateUtils.translationX(btnConnOption, 0, ANIM_DURATION_TRANSLATIONX, 600);
    }


    protected void showOrDismissConfigView() {

        if (rvSendConfig.getVisibility() == View.GONE) {
            rvSendConfig.setVisibility(View.VISIBLE);
        }

        if (!show) {
            ibtSendConfig.setSelected(true);
            viewSendBottom.setTranslationY(Utils.dpToPx(140));
            AnimateUtils.translationY(viewSendBottom, 0, 200, 0);
            configItemAdapter.notifyDataSetChanged();
        } else {
            ibtSendConfig.setSelected(false);
            AnimateUtils.translationY(viewSendBottom, Utils.dpToPx(140), 200, 0);
        }
        show = !show;
    }


    @Override
    public void onStateChange(int state) {
        if (state == RevealBackgroundView.STATE_FINISHED) {
            AnimateUtils.alpha(rvMsg, 1.0f, 2000, 0, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    revealBackgroundView.setVisibility(View.GONE);
                }
            });
        }
    }



    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (!canTouched)
            return;
        if (v == ibtSend) {
            if (onButtonClickListener != null)
                onButtonClickListener.onSendClick();
        }


        if (v == btnConnOption) {
            if (onButtonClickListener != null)
                onButtonClickListener.onConnOptionCLick();
        }

        if (v == ibtSendConfig) {
            showOrDismissConfigView();
            Utils.hideInputMethodWindow(getActivity());
        }
    }


    public boolean isShow() {
        return show;
    }

    public List<Message> getMsgsList() {
        return msgsList;
    }

    public List<ConfigItem> getConfigItemList() {
        return configItemList;
    }

    public MessagesAdapter getMsgAdapter() {
        return msgAdapter;
    }

    public ConfigItemAdapter getConfigItemAdapter() {
        return configItemAdapter;
    }

    public TextView getTvConnState() {
        return tvConnState;
    }

    public Button getBtnConnOption() {
        return btnConnOption;
    }

    public EditText getEtSend() {
        return etSend;
    }

    public RecyclerView getRvMsg() {
        return rvMsg;
    }

    public RevealBackgroundView getRevealBackgroundView() {
        return revealBackgroundView;
    }


    public void setConfigsOnItemClickListener(ConfigsOnItemClickListener configsOnItemClickListener) {
        this.configsOnItemClickListener = configsOnItemClickListener;
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public interface OnButtonClickListener {
        public void onSendClick();

        public void onConnOptionCLick();
    }

    public interface ConfigsOnItemClickListener {
        public void onItemClick(int position,boolean isSelected);
    }


}
