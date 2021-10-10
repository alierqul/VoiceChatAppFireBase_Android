package com.aliergul.hackathon.voicechatapp.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.util.BottomNavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActivityNotifications extends AppCompatActivity {

    private static final String TAG ="ActivityNotifications" ;
    private static final int ACTIVITY_NUM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        setupBottomNavigationView();
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationViewEx = findViewById(R.id.bottomNavigationView);
        BottomNavigationHelper.setupBottomNavigationView(bottomNavigationViewEx,ACTIVITY_NUM);
        BottomNavigationHelper.enableNavigation(this, this,bottomNavigationViewEx);

    }
}