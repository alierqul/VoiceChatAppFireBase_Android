package com.aliergul.hackathon.voicechatapp.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Post {
    public static final int POST_TEXT =0;
    public static final int POST_AUDIO=1;
    private long date;
    private String sendUID;// veren=gönderen
    private String messageUID;
    protected boolean appeared; //Göründü
    private String text="";
    private int typeMessage=0;
    private String voiceURL;

    public Post() {
        this.date=System.currentTimeMillis();
        this.appeared=false;
    }

    public Post(String text, String awayUID,int typeMessage) {

        this.sendUID = awayUID;
        this.text=text;
        this.date=System.currentTimeMillis();
        this.typeMessage=typeMessage;
        this.appeared=false;
    }

    public Post( String sendUID,  String text, int typeMessage, String voiceURL) {
        this.date=System.currentTimeMillis();
        this.sendUID = sendUID;
        this.appeared=false;
        this.text = text;
        this.typeMessage = typeMessage;
        this.voiceURL = voiceURL;
    }


    @Override
    public String toString() {
        return "Post{" +
                "date=" + date +
                ", sendUID='" + sendUID + '\'' +
                ", messageUID='" + messageUID + '\'' +
                ", appeared=" + appeared +
                ", text='" + text + '\'' +
                ", typeMessage=" + typeMessage +
                ", voiceURL='" + voiceURL + '\'' +
                '}';
    }

    public long getDate() {
        return date;
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

    public void setMessageUID(String messageUID) {
        this.messageUID = messageUID;
    }

    public boolean isAppeared() {
        return appeared;
    }

    public void setAppeared(boolean appeared) {
        this.appeared = appeared;
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

    public String getVoiceURL() {
        return voiceURL;
    }

    public void setVoiceURL(String voiceURL) {
        this.voiceURL = voiceURL;
    }
}
