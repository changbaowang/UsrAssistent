package com.usr.assistent.bean;

/**
 * Created by Administrator on 2015-07-28.
 */
public class Message {
    private String content;
    private String date;
    private MESSAGE_TYPE type;
    private String msgInfo;

    public Message(){};

    public Message(MESSAGE_TYPE type, String content) {
        this.type = type;
        this.content = content;
    }

    public Message(MESSAGE_TYPE type, String content,String msgInfo) {
        this.type = type;
        this.content = content;
        this.msgInfo = msgInfo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsgInfo() {
        return msgInfo;
    }

    public MESSAGE_TYPE getType() {
        return type;
    }

    public void setType(MESSAGE_TYPE type) {
        this.type = type;
    }

    public static enum MESSAGE_TYPE{
        SEND,RECEIVE;
    }
}
