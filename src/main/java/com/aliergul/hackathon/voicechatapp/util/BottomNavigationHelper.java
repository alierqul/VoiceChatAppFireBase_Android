package com.aliergul.hackathon.voicechatapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.home.ActivityMessages;
import com.aliergul.hackathon.voicechatapp.notifications.ActivityNotifications;
import com.aliergul.hackathon.voicechatapp.profile.ActivityProfile;
import com.aliergul.hackathon.voicechatapp.search.ActivitySearch;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationHelper {
    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationView bottomNavigationViewEx,int ACTIVITY_NUM){
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.setEnabled(false);
        bottomNavigationViewEx.setItemIconTintList(null);
        bottomNavigationViewEx.performClick();

        if(ACTIVITY_NUM>=0){
            Menu menu = bottomNavigationViewEx.getMenu();
            MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
            menuItem.setChecked(true);
            menuItem.setEnabled(false);
        }

    }

    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationView view){

        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.nav_profile: {
                        Intent intent1 = new Intent(context, ActivityProfile.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    }
                    case R.id.nav_message: {
                        Intent intent1 = new Intent(context, ActivityMessages.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    }
                    case R.id.nav_notifications: {
                        Intent intent1 = new Intent(context, ActivityNotifications.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    }
                    case R.id.nav_search: {
                        Intent intent1 = new Intent(context, ActivitySearch.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    }

                }
                return false;
            }
        });
    }
}
