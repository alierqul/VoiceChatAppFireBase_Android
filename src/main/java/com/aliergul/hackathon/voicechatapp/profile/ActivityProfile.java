package com.aliergul.hackathon.voicechatapp.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.ActivityProfileBinding;
import com.aliergul.hackathon.voicechatapp.login.ActivityLoginAndRegister;
import com.aliergul.hackathon.voicechatapp.model.Users;
import com.aliergul.hackathon.voicechatapp.util.BottomNavigationHelper;
import com.aliergul.hackathon.voicechatapp.util.FirebaseHelper;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityProfile extends AppCompatActivity {

    private static final String TAG = "ActivityProfile";
    private static final int ACTIVITY_NUM = 3;
    private ActivityProfileBinding binding;
    private Users activeUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupBottomNavigationView();
        activeUser=FirebaseHelper.getActiveUser();
        if(activeUser==null){
            FirebaseHelper.getActiveUserData();
            activeUser=FirebaseHelper.getActiveUser();
        }
        setupProfile(activeUser);
        clickItemView();
    }

    private void clickItemView() {
        binding.btnOptions.setOnClickListener(v->{
            PopupMenu menu=new PopupMenu(this,binding.btnOptions);
            menu.inflate(R.menu.menu_profile_with_edit);
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.menu_edit:{
                            editProfile();
                            break;
                        }
                        case R.id.menu_exit:{
                            FirebaseAuth mAuth=FirebaseAuth.getInstance();
                            mAuth.signOut();
                            openLoginPanel();
                            break;
                        }

                    }
                    return false;
                }
            });
            menu.show();

        });
    }

    private void editProfile() {
        //TODO Edit düzenlenecek.
    }

    private void openLoginPanel(){
        Intent i=new Intent(this, ActivityLoginAndRegister.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void setupProfile(Users u) {
        Log.d(TAG,"setupProfile");
        if(u!=null){
            binding.edtFullName.setText(u.getUserName());
            binding.edtUserEmailId.setText(u.getUserEmail());
            binding.password.setText("**********");
        }

    }


    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationViewEx = findViewById(R.id.bottomNavigationView);
        BottomNavigationHelper.setupBottomNavigationView(bottomNavigationViewEx,ACTIVITY_NUM);
        BottomNavigationHelper.enableNavigation(this, this,bottomNavigationViewEx);

    }
}