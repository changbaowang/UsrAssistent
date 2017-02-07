package com.usr.assistent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.usr.assistent.R;
import com.usr.net.bean.Connect;
import com.usr.net.bean.ConnectConfiguration;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by liu on 15/8/9.
 */
public class ConnectionSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private List<Connect> list;
    private LayoutInflater inflater;
    private ConnectionsOnItemSelectedListener connectionsOnItemSelectedListener;


    public ConnectionSelectAdapter(Context context,List<Connect> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_connections_select,parent,false);
        ConnectionsSelectHolder holder = new ConnectionsSelectHolder(view);
        holder.btnConnection.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Connect connect = list.get(position);
        ConnectConfiguration configuration = connect.getConfiguration();
        ConnectionsSelectHolder connectionsSelectHolder = (ConnectionsSelectHolder) holder;
        connectionsSelectHolder.btnConnection.setTag(holder);
        connectionsSelectHolder.btnConnection.setText(configuration.getHost()+":"+configuration.getPort());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        ConnectionsSelectHolder holder = (ConnectionsSelectHolder) v.getTag();
        int position = holder.getAdapterPosition();
        if (connectionsOnItemSelectedListener != null)
            connectionsOnItemSelectedListener.onItemSelected(position);
    }

    public void setConnectionsOnItemSelectedListener(ConnectionsOnItemSelectedListener connectionsOnItemSelectedListener) {
        this.connectionsOnItemSelectedListener = connectionsOnItemSelectedListener;
    }

    public interface ConnectionsOnItemSelectedListener{
        public void onItemSelected(int position);
    }


    public class ConnectionsSelectHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.btn_connections_select)
        Button btnConnection;

        public ConnectionsSelectHolder(View view){
            super(view);
            ButterKnife.inject(this, view);
        }
    }

}
