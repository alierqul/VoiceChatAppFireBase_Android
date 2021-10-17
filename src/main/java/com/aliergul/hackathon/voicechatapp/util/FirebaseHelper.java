package com.aliergul.hackathon.voicechatapp.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aliergul.hackathon.voicechatapp.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;

public  class FirebaseHelper {
    private static final String TAG ="FirebaseHelper" ;
    private static Users activeUser;
    private static Users friendUser;
    private static List<Users> listFriendUsers;
    private static FirebaseAuth mAuth;
    private static FirebaseUser fireUser;
    private static FirebaseDatabase database;


static {
    if(mAuth==null){
        mAuth = FirebaseAuth.getInstance();
        fireUser = mAuth.getCurrentUser();
        database=FirebaseDatabase.getInstance();
    }
}



    public static void getActiveUserData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(MyUtil.COLUMN_USERS)
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    Log.w(TAG,"getActiveUserData DataSnapshot ="+snapshot.getKey());
                    Log.w(TAG,"getActiveUserData DataSnapshot ="+snapshot.getValue());
                    Users u = snapshot.getValue(Users.class);
                    activeUser=u;
                    Log.w(TAG,"getActiveUserData ="+activeUser);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public static void getFriendUserUpgrade() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(MyUtil.COLUMN_USERS).child(fireUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                activeUser = snapshot.getValue(Users.class);
                Log.w(TAG,"getActiveUserData ="+activeUser);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static Users getActiveUser(){
        return activeUser;
    }
    public static void setActiveUser(Users user){
        activeUser=user;
    }

    public static List<Users> getListFriendUsers() {
        return listFriendUsers;
    }

    public static void setListFriendUsers(List<Users> listFriendUsers) {
        listFriendUsers = listFriendUsers;
    }

    public static Users getFriendUser() {
        return friendUser;
    }

    public static void setFriendUser(Users friendUser) {
        FirebaseHelper.friendUser = friendUser;
    }

    public static void addListFriendUsers(Users u) {
        if(listFriendUsers==null){
            listFriendUsers=new ArrayList<>();
            listFriendUsers.add(u);
        }else{
            listFriendUsers.add(u);
        }

    }
    public static void reFirebaseColumn(String column,String newValue){
        database.getReference().child(MyUtil.COLUMN_USERS)
                .child(activeUser.getUserUID())
                .child(column).setValue(newValue);
    }
    public static void reWriteOneSignalKey(){
        OSDeviceState device = OneSignal.getDeviceState();
        String deviceID = device.getUserId();
        database.getReference().child(MyUtil.COLUMN_USERS).child(activeUser.getUserUID()).child("oneSignalDeviceID").setValue(deviceID);
    }

    public static void setUserOnlineDatee(String log) {
        database.getReference().child(MyUtil.COLUMN_USERS)
                .child(activeUser.getUserUID()).child("onlineDate").setValue(log);
    }

    public static void sendVerificationEmail(Context mContext){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(mContext,"Doğrulama linki Eposta Adresinize Gönderildi.\nEposta Adresinizi Kontol Ediniz.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public static void sendPaswordResetEmail(Context mContext,String emailAddress){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(mContext,"Sıfırlama Epostası Gönderildi.\nEposta Adresinizi Kontol Ediniz.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}



