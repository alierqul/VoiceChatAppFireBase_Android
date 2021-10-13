package com.aliergul.hackathon.voicechatapp.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Post {
    public static final int POST_TEXT =0;
    public static final int POST_AUDIO=1;
    private long date;
    private String giveUID;// alan
    private String sendUID;// veren=gönderen
    private String messageUID;
    protected boolean appeared; //Göründü
    private String text="";
    private int typeMessage=0;
    private String voiceTime;




    public Post() {
        this.date=System.currentTimeMillis();
        this.appeared=false;
    }

    public Post(String text,String giveUID, String awayUID,int typeMessage) {
        this.giveUID = giveUID;
        this.sendUID = awayUID;
        this.text=text;
        this.date=System.currentTimeMillis();
        this.typeMessage=typeMessage;
        this.appeared=false;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(int typeMessage) {
        this.typeMessage = typeMessage;
    }

    public long getDate() {
        return date;
    }

    public String getGiveUID() {
        return giveUID;
    }

    public void setGiveUID(String giveUID) {
        this.giveUID = giveUID;
    }

    public String getSendUID() {
        return sendUID;
    }

    public void setSendUID(String sendUID) {
        this.sendUID = sendUID;
    }

    public String getMessageUID() {
        return messageUID;
    }

    public boolean isAppeared() {
        return appeared;
    }

    public void setAppeared(boolean appeared) {
        this.appeared = appeared;
    }

    public String getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(String voiceTime) {
        this.voiceTime = voiceTime;
    }


    public void setMySender(boolean mySender) {
        mySender = mySender;
    }

    public void setMessageUID(String messageUID) {
        this.messageUID = messageUID;
    }
}
