package com.aliergul.hackathon.voicechatapp.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.ActivityHomeBinding;
import com.aliergul.hackathon.voicechatapp.login.ActivityLoginAndRegister;
import com.aliergul.hackathon.voicechatapp.model.MyNotification;
import com.aliergul.hackathon.voicechatapp.model.Users;
import com.aliergul.hackathon.voicechatapp.search.AdapterListProfile;
import com.aliergul.hackathon.voicechatapp.util.BottomNavigationHelper;
import com.aliergul.hackathon.voicechatapp.util.MyConstSecretID;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.firebase.ui.auth.data.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
        Log.w(TAG,"ActivityMessages onCreate");
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listUser=new ArrayList<>();
        setupBottomNavigationView();
        setupGeneralSettings();

        adapter=new AdapterListProfile(new ArrayList<Users>(),ActivityMessages.this);
        binding.containerProfiles.setLayoutManager(new LinearLayoutManager(this));
        binding.containerProfiles.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterListProfile.IClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if(listUser!=null){
                    Users user=listUser.get(position);
                    Intent i=new Intent(ActivityMessages.this, ActivityNewMessage.class);
                    i.putExtra(MyUtil.USER_UID,user.getUserUID());
                    i.putExtra(MyUtil.FULL_NAME,user.getUserName());
                    startActivity(i);
                }
            }
        });
        if(mAuth.getCurrentUser()!=null){
            getFirebaseUserdata(mAuth.getCurrentUser());
            getActiveUserData(mAuth.getCurrentUser());
        }

    }

    private void setupGeneralSettings() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(MyConstSecretID.ONESIGNAL_APP_ID);
        // FireBase
        mAuth=FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(listenerAccount(mAuth));
       OSDeviceState device = OneSignal.getDeviceState();
        String deviceID = device.getUserId();
    //   new MyNotification(1,"message","","ali",3,3,false).sendNoti(deviceID);


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

                }
            }
        };
    }

    private void getFirebaseUserdata(FirebaseUser user) {
        List<String> listUserKeys=new ArrayList<>();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(MyUtil.COLUMN_USERS).child(user.getUid()).child(MyUtil.COLUMN_MESSAGES)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUserKeys.clear();
                for(DataSnapshot s:snapshot.getChildren()){
                    listUserKeys.add(s.getKey());

                }
                if(listUserKeys.size()==0){
                    binding.tvNothingMessage.setVisibility(View.VISIBLE);
                }else{
                    binding.tvNothingMessage.setVisibility(View.GONE);
                }
               //Keyleri aldık şimdi isimleri alalım

                listUser.clear();
                for (String key:listUserKeys ) {
                    mDatabase.child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Users user=snapshot.getValue(Users.class);
                            Log.w(TAG,"Key: "+user.toString());
                            listUser.add(user);
                            adapter.setListe(listUser);

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


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

    private void getActiveUserData(FirebaseUser user) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(MyUtil.COLUMN_USERS).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users u=snapshot.getValue(Users.class);
                Users.setActiveUser(u);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}