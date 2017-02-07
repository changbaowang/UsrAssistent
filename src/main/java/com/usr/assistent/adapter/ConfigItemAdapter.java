package com.usr.assistent.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.usr.assistent.R;
import com.usr.assistent.bean.ConfigItem;
import com.usr.assistent.utils.AnimateUtils;
import com.usr.assistent.utils.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015-07-28.
 */
public class ConfigItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private List<ConfigItem> list;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    private Context context;
    public ConfigItemAdapter(Context context,List<ConfigItem> list){
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_item_config,parent,false);
        ConfigItemViewHolder configItemViewHolder = new ConfigItemViewHolder(view);
        configItemViewHolder.ibtConfig.setOnClickListener(this);
        return configItemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ConfigItem item = list.get(position);
        ConfigItemViewHolder configItemViewHolder = (ConfigItemViewHolder) holder;
        configItemViewHolder.tvConfigName.setText(item.getName());
        configItemViewHolder.ibtConfig.setTag(configItemViewHolder);
        if (item.isSelected()){
            configItemViewHolder.ibtConfig.setImageResource(item.getImgRes()[1]);
            configItemViewHolder.tvConfigName.setTextColor(item.getColors()[0]);
        }else{
            configItemViewHolder.ibtConfig.setImageResource(item.getImgRes()[0]);
            configItemViewHolder.tvConfigName.setTextColor(item.getColors()[1]);
        }


        if (position <3)
            animatroItem(configItemViewHolder,position);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void animatroItem(ConfigItemViewHolder holder,int position){
        holder.ibtConfig.setTranslationY(Utils.dpToPx(55));
        holder.tvConfigName.setTranslationY(Utils.dpToPx(55));
        AnimateUtils.translationY(holder.ibtConfig, 0, 250, position*50);
        AnimateUtils.translationY(holder.tvConfigName,0,250,position*50);
    }

    @Override
    public void onClick(View v) {
        ConfigItemViewHolder holder = (ConfigItemViewHolder) v.getTag();
        int position = holder.getAdapterPosition();
        ConfigItem item = list.get(position);
        if (!item.isSelected()){
            holder.ibtConfig.setImageResource(item.getImgRes()[1]);
            holder.tvConfigName.setTextColor(item.getColors()[0]);
        }else {
            holder.ibtConfig.setImageResource(item.getImgRes()[0]);
            holder.tvConfigName.setTextColor(item.getColors()[1]);
        }

        if (onItemClickListener != null)
            onItemClickListener.onItemClick(position);
    }

    public class ConfigItemViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.ibt_config)
        ImageButton ibtConfig;
        @InjectView(R.id.tv_config_name)
        TextView tvConfigName;

        public ConfigItemViewHolder(View view){
            super(view);
            ButterKnife.inject(this,view);
        }
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onItemClick(int position);
    }
}
