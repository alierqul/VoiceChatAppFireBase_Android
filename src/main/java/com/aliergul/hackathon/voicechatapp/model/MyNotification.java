package com.aliergul.hackathon.voicechatapp.model;

import android.util.Log;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class MyNotification {
    private int ID;
    private String title;
    private String sendUID;
    private String message;
    private int type;
    private long date;
    private boolean appeared; //göründü

    public MyNotification() {
    }

    public MyNotification(String title, String body, int type) {
        this.title = title;
        this.message = body;
        this.type = type;
        this.date =System.currentTimeMillis();
        this.appeared=false;
    }

    public MyNotification(int ID, String title, String sendUID, String message, int type) {
        this.ID = ID;
        this.title = title;
        this.sendUID = sendUID;
        this.message = message;
        this.type = type;
        this.date = System.currentTimeMillis();;
        this.appeared = false;
    }

    public MyNotification(int ID, String title, String sendUID, String message, int type, long date, boolean appeared) {
        this.ID = ID;
        this.title = title;
        this.sendUID = sendUID;
        this.message = message;
        this.type = type;
        this.date = date;
        this.appeared = appeared;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isAppeared() {
        return appeared;
    }

    public void setAppeared(boolean appeared) {
        this.appeared = appeared;
    }

    public String getSendUID() {
        return sendUID;
    }

    public void setSendUID(String sendUID) {
        this.sendUID = sendUID;
    }

    public String toJSON(){

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("ID", getID());
            jsonObject.put("title", getTitle());
            jsonObject.put("sendUID", getSendUID());
            jsonObject.put("message", getMessage());
            jsonObject.put("type", getType());
            jsonObject.put("date", getDate());
            jsonObject.put("appeared", isAppeared());

            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

    }

    @Override
    public String toString() {
        return "MyNotification{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", sendUID='" + sendUID + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", date=" + date +
                ", appeared=" + appeared +
                '}';
    }
    //int ID, String title, String sendUID, String message, int type, long date, boolean appeared
    public static MyNotification fromJson(JSONObject obj) throws JSONException {
        return new MyNotification(
                obj.getInt("ID"),
                obj.getString("title"),
                obj.getString("sendUID"),
                obj.getString("message"),
                obj.getInt("type"),
                obj.getLong("date"),
                obj.getBoolean("appeared"));
    }
   public void sendNoti(String deviceUID){
       try {
           OneSignal.postNotification(new JSONObject("{'contents': {'tr':"+this.message+" },'include_player_ids': ['" + deviceUID + "'],'data':"+this.toJSON()+"}"),
                   new OneSignal.PostNotificationResponseHandler() {
                       @Override
                       public void onSuccess(JSONObject response) {
                           Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                       }

                       @Override
                       public void onFailure(JSONObject response) {
                           Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                       }
                   });
       } catch (JSONException e) {
           e.printStackTrace();
       }
   }
}
