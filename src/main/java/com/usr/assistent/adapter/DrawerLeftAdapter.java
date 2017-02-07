package com.usr.assistent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.usr.assistent.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015-07-21.
 */
public class DrawerLeftAdapter extends BaseAdapter {
    private static final int TYPE_MENU_ITEM = 0 ;
    private static final int TYPE_DIVIDER = 1;
    private List<DrawerMenuItem> list = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public DrawerLeftAdapter(Context context){
        this.layoutInflater = LayoutInflater.from(context);
        initMenuItems();
    }


    private void initMenuItems(){
        list.add(DrawerMenuItem.dividerMenuItem());
        list.add(new DrawerMenuItem(R.mipmap.btn_ts_n,R.string.tcp_server));
        list.add(new DrawerMenuItem(R.mipmap.btn_tc_n,R.string.tcp_client));
        list.add(new DrawerMenuItem(R.mipmap.btn_uc_n,R.string.udp_fragment));

        list.add(DrawerMenuItem.dividerMenuItem());
        list.add(new DrawerMenuItem(-1,R.string.about));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return list.get(position).isDivider ? TYPE_DIVIDER:TYPE_MENU_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == TYPE_MENU_ITEM){
            MenuItemHolder holder = null;
            if (convertView == null){
                convertView = layoutInflater.inflate(R.layout.item_drawer_left,parent,false);
                holder = new MenuItemHolder(convertView);
                convertView.setTag(holder);
            }else
                holder = (MenuItemHolder) convertView.getTag();

            DrawerMenuItem item = list.get(position);

            if (item.imgResId != -1)
               holder.ivIcon.setImageResource(item.imgResId);
            else
               holder.ivIcon.setVisibility(View.GONE);

            holder.tvName.setText(item.strResId);
            return convertView;
        }else {
             return layoutInflater.inflate(R.layout.item_drawler_left_divider,parent,false);
        }
    }


    public static class MenuItemHolder{
        @InjectView(R.id.iv_drawer_item_icon)
        ImageView ivIcon;
        @InjectView(R.id.tv_drawer_item_name)
        TextView tvName;

        public MenuItemHolder(View view){
            ButterKnife.inject(this,view);
        }
    }


    public static  class  DrawerMenuItem{
        public int imgResId;
        public int strResId;
        public boolean isDivider;

        private DrawerMenuItem(){

        }

        public DrawerMenuItem(int imgResId,int strResId){
            this.imgResId = imgResId;
            this.strResId = strResId;
            this.isDivider = false;
        }


        public static DrawerMenuItem dividerMenuItem(){
            DrawerMenuItem dividerMenuItem = new DrawerMenuItem();
            dividerMenuItem.isDivider = true;
            return  dividerMenuItem;
        }
    }
}
