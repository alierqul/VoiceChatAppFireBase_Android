package com.aliergul.hackathon.voicechatapp.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.aliergul.hackathon.voicechatapp.databinding.ActivityNewMessageBinding;
import com.aliergul.hackathon.voicechatapp.model.Post;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityNewMessage extends AppCompatActivity {
    private static final String TAG = "ActivityNewMessage";
    private ActivityNewMessageBinding binding;
    private String friendUID="";
    private String friendName="";
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNewMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        gelenVeriKontrol();
        clickItemView();

        binding.containerMessages.setLayoutManager(new LinearLayoutManager(this));


    }

    private void clickItemView() {
        binding.btnBack.setOnClickListener(v->{
            onBackPressed();
        });
        binding.btnSenMessage.setOnClickListener(v->{
            writeDatabese();
        });

        getListMessages();
    }

    private void getListMessages() {
        List<Post> listPost=new ArrayList<>();
        DatabaseReference mDatabase=FirebaseDatabase.getInstance().getReference();
        FirebaseUser user= mAuth.getCurrentUser();
        if(user!=null){
            mDatabase.child(MyUtil.COLUMN_USERS).child(user.getUid()).child(MyUtil.COLUMN_MESSAGES).child(friendUID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue()!=null){
                        listPost.clear();
                        for (DataSnapshot s:snapshot.getChildren()) {
                            listPost.add(s.getValue(Post.class));
                        }
                        AdapterListMessage adapter=new AdapterListMessage(listPost,ActivityNewMessage.this);
                        binding.containerMessages.setAdapter(adapter);

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void writeDatabese() {
        FirebaseUser user= mAuth.getCurrentUser();
        Post postText=new Post(binding.edtMessage.getText().toString(),friendUID, user.getUid(),Post.POST_TEXT);
        binding.edtMessage.setText("");

        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        String key=mDatabase.child(user.getUid()).child(MyUtil.COLUMN_MESSAGES).push().getKey();
        postText.setMessageUID(key);
        mDatabase.child(MyUtil.COLUMN_USERS).child(user.getUid()).child(MyUtil.COLUMN_MESSAGES).child(friendUID).child(key).setValue(postText);
        postText.setMySender(false);
        mDatabase.child(MyUtil.COLUMN_USERS).child(friendUID)
                .child(MyUtil.COLUMN_MESSAGES)
                .child(user.getUid()).child(key).setValue(postText);
    }

    private void gelenVeriKontrol() {
        if(getIntent().getExtras()!=null);{
            mAuth =FirebaseAuth.getInstance();
            friendName=getIntent().getStringExtra(MyUtil.FULL_NAME);
            friendUID=getIntent().getStringExtra(MyUtil.USER_UID);
            binding.tvFriendName.setText(friendName);
        }

    }
}