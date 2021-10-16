package com.aliergul.hackathon.voicechatapp.model;


import androidx.annotation.Nullable;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class Users implements Serializable {


    private String userUID;
    private String userName;
    private String userEmail;
    private String userPhoto;
    private String metaData;
    private String oneSignalDeviceID;
    private String onlineDate;

    public Users() {

    }


    public String getOneSignalDeviceID() {
        return oneSignalDeviceID;
    }

    public void setOneSignalDeviceID(String oneSignalDeviceID) {
        this.oneSignalDeviceID = oneSignalDeviceID;
    }

    public Users(String userUID, String userName, String userEmail, String userPhoto) {
        this.userUID = userUID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhoto = userPhoto;
        this.metaData=(userName.concat(" ").concat(userEmail.substring(0,userEmail.indexOf('@')))).toLowerCase();

    }


    @Override
    public String toString() {
        return "Users{" +
                "userUID='" + userUID + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPhoto='" + userPhoto + '\'' +
                ", metaData='" + metaData + '\'' +
                ", oneSignalDeviceID='" + oneSignalDeviceID + '\'' +
                ", onlineDate='" + onlineDate + '\'' +
                '}';
    }

    public String getMetaData() {
        return metaData;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getOnlineDate() {
        return onlineDate;
    }

    public void setOnlineDate(String onlineDate) {
        this.onlineDate = onlineDate;
    }
}
