package com.usr.assistent.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.usr.assistent.R;
import com.usr.assistent.adapter.ConnectionSelectAdapter;
import com.usr.assistent.utils.Utils;
import com.usr.net.bean.Connect;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by liu on 15/8/9.
 */
public class SelectConnectionsMenu extends LinearLayout {
    private static final int CONTEXT_MENU_WIDTH = Utils.dpToPx(220);

    @InjectView(R.id.rv_connections)
    RecyclerView rv_connections;

    private List<Connect> list;
    private ConnectionSelectAdapter.ConnectionsOnItemSelectedListener connectionsOnItemSelectedListener;

    public SelectConnectionsMenu(Context context ,List<Connect> list,ConnectionSelectAdapter.ConnectionsOnItemSelectedListener connectionsOnItemSelectedListener) {
        super(context);
        this.list = list;
        this.connectionsOnItemSelectedListener = connectionsOnItemSelectedListener;
        init();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.context_menu_connections, this, true);
        setBackgroundResource(R.drawable.bg_container_shadow);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH,Utils.dpToPx(50*list.size()+20)));
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        initRecycleView();
    }


    private void initRecycleView(){
        ConnectionSelectAdapter adapter = new ConnectionSelectAdapter(getContext(),list);
        adapter.setConnectionsOnItemSelectedListener(connectionsOnItemSelectedListener);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv_connections.setLayoutManager(llm);
        rv_connections.setAdapter(adapter);
    }


    public void dismiss(){
        ((ViewGroup)getParent()).removeView(SelectConnectionsMenu.this);
    }

}
