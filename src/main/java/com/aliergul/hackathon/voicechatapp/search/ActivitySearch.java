package com.aliergul.hackathon.voicechatapp.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.ActivityProfileBinding;
import com.aliergul.hackathon.voicechatapp.databinding.ActivitySearchBinding;
import com.aliergul.hackathon.voicechatapp.home.ActivityMessages;
import com.aliergul.hackathon.voicechatapp.home.ActivityNewMessage;
import com.aliergul.hackathon.voicechatapp.model.Users;
import com.aliergul.hackathon.voicechatapp.util.BottomNavigationHelper;
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

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivitySearch extends AppCompatActivity {

    private static final String TAG = "ActivitySearch";
    private static final int ACTIVITY_NUM = 1;
    private AdapterListProfile adapter;
    private ActivitySearchBinding binding;
    private List<Users> listUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupBottomNavigationView();
        listUser=new ArrayList<>();

         adapter=new AdapterListProfile(new ArrayList<>(),this);
        binding.containerSearch.setLayoutManager(new LinearLayoutManager(this));
        binding.containerSearch.setAdapter(adapter);
        adapter.setOnItemClickListener(new AdapterListProfile.IClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                if(listUser!=null){
                    Users u=listUser.get(position);
                    Intent i=new Intent(ActivitySearch.this, ActivityNewMessage.class);
                    i.putExtra(MyUtil.USER_UID,u.getUserUID());
                    i.putExtra(MyUtil.FULL_NAME,u.getUserName());
                    startActivity(i);
                }
            }
        });
        setupItemListener();
    }

    private void setupItemListener() {
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s != null && s.length() > 1){
                    getFindProfiles(s.toString().toLowerCase());
                    listUser.clear();
                }
            }
        });
    }

    private void  getFindProfiles(String queryText){
        DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").orderByChild("metaData")
                .startAt("%${"+queryText+"}%")
                .endAt(queryText+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot s:snapshot.getChildren()){
                    if(s.getValue()!=null){
                        Users users=s.getValue(Users.class);
                        if(!Users.getActiveUser().getUserUID().equals(users.getUserUID())){
                            listUser.add(users);
                        }
                    }


                }
                adapter.updateList(listUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationViewEx = findViewById(R.id.bottomNavigationView);
        BottomNavigationHelper.setupBottomNavigationView(bottomNavigationViewEx,ACTIVITY_NUM);
        BottomNavigationHelper.enableNavigation(this, this,bottomNavigationViewEx);

    }
}