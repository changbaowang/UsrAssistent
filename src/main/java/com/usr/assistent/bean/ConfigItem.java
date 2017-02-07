package com.usr.assistent.bean;

/**
 * Created by Administrator on 2015-07-28.
 */
public class ConfigItem {
    private int[] imgRes;
    private int[] colors;
    private String name;
    private CONFIG_TYPE configType;
    private boolean selected;

    public ConfigItem(){}

    public ConfigItem(int[] imgRes,int[] colors,String name, CONFIG_TYPE configType) {
        this.imgRes = imgRes;
        this.colors = colors;
        this.name = name;
        this.configType = configType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CONFIG_TYPE getConfigType() {
        return configType;
    }

    public void setConfigType(CONFIG_TYPE configType) {
        this.configType = configType;
    }

    public int[] getImgRes() {
        return imgRes;
    }

    public void setImgRes(int[] imgRes) {
        this.imgRes = imgRes;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public static enum CONFIG_TYPE{
        CLEAR_TEXT,HEX_DISPLAY,HEX_SEND,TIMER_SEND;
    }
}
