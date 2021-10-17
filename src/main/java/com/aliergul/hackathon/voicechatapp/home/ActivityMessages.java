package com.aliergul.hackathon.voicechatapp.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.ActivityHomeBinding;
import com.aliergul.hackathon.voicechatapp.login.ActivityLoginAndRegister;
import com.aliergul.hackathon.voicechatapp.model.Users;
import com.aliergul.hackathon.voicechatapp.newmessage.ActivityNewMessage;
import com.aliergul.hackathon.voicechatapp.search.AdapterListProfile;
import com.aliergul.hackathon.voicechatapp.util.BottomNavigationHelper;
import com.aliergul.hackathon.voicechatapp.util.FirebaseHelper;
import com.aliergul.hackathon.voicechatapp.util.MyConstSecretID;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.List;


public class ActivityMessages extends AppCompatActivity {

    private static final String TAG = "ActivityMessages";

    private static final int ACTIVITY_NUM = 0;
    private AdapterListProfile adapter;
    private List<Users> listUser;
    private ActivityHomeBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listUser=new ArrayList<>();
        mAuth=FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(listenerAccount(mAuth));
        if(mAuth.getCurrentUser()!=null){
            FirebaseHelper.getActiveUserData();
            getFirebaseUserdata();
            setupNoifications();
            setupRecyclerView();
            setupBottomNavigationView();
        }

    }


    private void setupRecyclerView() {
        adapter=new AdapterListProfile(new ArrayList<Users>(),getString(R.string.emptyMessages),ActivityMessages.this);
        binding.containerProfiles.setLayoutManager(new LinearLayoutManager(this));
        binding.containerProfiles.setAdapter(adapter);
        binding.containerProfiles.setHasFixedSize(true);
        adapter.setOnItemClickListener(new AdapterListProfile.IClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if(listUser!=null){
                    Users friendUser=listUser.get(position);
                    Intent i=new Intent(ActivityMessages.this, ActivityNewMessage.class);
                    FirebaseHelper.setFriendUser(friendUser);
                    startActivity(i);
                }
            }
        });
    }

    private void setupNoifications() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(MyConstSecretID.ONESIGNAL_APP_ID);

        OneSignal.setNotificationOpenedHandler(
                new OneSignal.OSNotificationOpenedHandler() {
                    @Override
                    public void notificationOpened(OSNotificationOpenedResult result) {
                        // Capture Launch URL (App URL) here
                        String launchUrl = result.getNotification().getLaunchURL();

                        if (launchUrl != null) {
                            // The following can be used to open an Activity of your choice.
                            // Replace - getApplicationContext() - with any Android Context.
                            // Replace - YOURACTIVITY.class with your activity to deep link
                            Intent intent = new Intent(getApplicationContext(), ActivityMessages.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("openURL", launchUrl);

                            startActivity(intent);
                        }
                    }
                });

        // FireBase

    }
    private FirebaseAuth.AuthStateListener listenerAccount(FirebaseAuth mAuth) {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=mAuth.getCurrentUser();
                if(mAuth==null || user==null){
                    Log.d(TAG,"onAuthStateChanged");
                    openLoginPanel();
                }else{
                    Log.d(TAG,"onAuthStateChanged TRUE");



                }
            }
        };
    }


    private void getFirebaseUserdata() {
        showProgress();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase .child(MyUtil.COLUMN_MESSAGES)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                for(DataSnapshot s:snapshot.getChildren()){

                    mDatabase.child(MyUtil.COLUMN_USERS).child(s.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try{
                                Users user=snapshot.getValue(Users.class);
                                listUser.add(user);
                                adapter.setListe(listUser);
                                FirebaseHelper.setListFriendUsers(listUser);
                            }catch (Exception e){
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
                hideProgress();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openLoginPanel(){
        Intent i=new Intent(this, ActivityLoginAndRegister.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationViewEx = findViewById(R.id.bottomNavigationView);
        BottomNavigationHelper.setupBottomNavigationView(bottomNavigationViewEx,ACTIVITY_NUM);
        BottomNavigationHelper.enableNavigation(this, this,bottomNavigationViewEx);

    }


    private void showProgress() {
        binding.homeProgresbar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {

        binding.homeProgresbar.setVisibility(View.GONE);
    }
}