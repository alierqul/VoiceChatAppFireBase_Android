package com.aliergul.hackathon.voicechatapp.search;

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
import com.aliergul.hackathon.voicechatapp.model.Users;
import com.aliergul.hackathon.voicechatapp.util.BottomNavigationHelper;
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
import java.util.Locale;

public class ActivitySearch extends AppCompatActivity {

    private static final String TAG = "ActivitySearch";
    private static final int ACTIVITY_NUM = 1;
    private AdapterListProfile adapter;
    private ActivitySearchBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupBottomNavigationView();


         adapter=new AdapterListProfile(new ArrayList<>(),this);
        binding.containerSearch.setLayoutManager(new LinearLayoutManager(this));
        binding.containerSearch.setAdapter(adapter);
        setupItemListener();
    }

    private void setupItemListener() {
        Toast.makeText(ActivitySearch.this,"setupItemListener: ",Toast.LENGTH_SHORT).show();
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
                }
            }
        });
    }

    private void  getFindProfiles(String queryText){

        List<Users> list =new ArrayList<>();
        DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").orderByChild("metaData")
                .startAt("%${"+queryText+"}%")
                .endAt(queryText+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot s:snapshot.getChildren()){
                    list.add(s.getValue(Users.class));
                }
                adapter.updateList(list);
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