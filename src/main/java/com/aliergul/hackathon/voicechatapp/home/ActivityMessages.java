package com.aliergul.hackathon.voicechatapp.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.ActivityHomeBinding;
import com.aliergul.hackathon.voicechatapp.login.ActivityLoginAndRegister;
import com.aliergul.hackathon.voicechatapp.model.Users;
import com.aliergul.hackathon.voicechatapp.search.AdapterListProfile;
import com.aliergul.hackathon.voicechatapp.util.BottomNavigationHelper;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ActivityMessages extends AppCompatActivity {

    private static final String TAG = "ActivityMessages";
    private static final int ACTIVITY_NUM = 0;
    private AdapterListProfile adapter;

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG,"ActivityMessages onCreate");
        binding=ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupBottomNavigationView();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()==null){
            mAuth.signOut();
        }
        mAuth.addAuthStateListener(listenerAccount(mAuth));
        adapter=new AdapterListProfile(new ArrayList<Users>(),ActivityMessages.this);
        binding.containerProfiles.setLayoutManager(new LinearLayoutManager(this));
        binding.containerProfiles.setAdapter(adapter);
        getFirebaseUserdata(mAuth.getCurrentUser());
    }

    private FirebaseAuth.AuthStateListener listenerAccount(FirebaseAuth mAuth) {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=mAuth.getCurrentUser();
                if(mAuth==null ||user==null){
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
        mDatabase.child("Users").child(user.getUid()).child(MyUtil.COLUMN_MESSAGES)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUserKeys.clear();
                for(DataSnapshot s:snapshot.getChildren()){
                    listUserKeys.add(s.getKey());
                    Log.w(TAG,"Key: "+s.getKey());
                }
               //Keyleri aldık şimdi isimleri alalım
                List<Users> listUser=new ArrayList<>();
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
}