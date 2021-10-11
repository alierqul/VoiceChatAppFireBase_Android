package com.aliergul.hackathon.voicechatapp.home;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.ActivityNewMessageBinding;
import com.aliergul.hackathon.voicechatapp.model.Post;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ActivityNewMessage extends FragmentActivity {
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private static final String TAG = "ActivityNewMessage";
    private static final int FRAGMENT_ID = 100;
    private int PERMISSION_CODE = 21;
    private boolean isRecording = false;
    private Chronometer timer;
    private MediaRecorder mediaRecorder;
    private String recordFile;

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
            Post postText=new Post(binding.edtMessage.getText().toString(),friendUID, FirebaseAuth.getInstance().getCurrentUser().getUid(),Post.POST_TEXT);
            writeDatabese(postText);
        });
        binding.btnSenVoice.setOnClickListener(v->{
            voiceOpenDialog();
        });
        getListMessages();
    }

    /**
     * ses Kayıt fragment aç
     */
    private void voiceOpenDialog() {
        binding.btnSenVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){

                    checkPermissionAndStart();
                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    binding.btnSenVoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_voice, null));
                    stopRecording();
                }
                return false;
            }

        });

    }

    private void checkPermissionAndStart() {
        if(checkPermissions()) {
            startRecording();
            binding.btnSenVoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_start, null));
            isRecording = true;
        }
    }
    private void stopRecording() {
        timer.stop();
        Toast.makeText(this,"Recording Stopped, File Saved ",Toast.LENGTH_SHORT).show();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }
    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        String recordPath = this.getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.getDefault());
        Date now = new Date();
        recordFile = "Recording_" + formatter.format(now) + ".3gp";
        Toast.makeText(this,"Recording, File Name",Toast.LENGTH_SHORT).show();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }
    private boolean checkPermissions() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            return true;
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
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
                            binding.containerMessages.smoothScrollToPosition(listPost.size()-1);
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

    private void writeDatabese(Post p) {
        FirebaseUser user= mAuth.getCurrentUser();

        binding.edtMessage.setText("");

        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        String key=mDatabase.child(user.getUid()).child(MyUtil.COLUMN_MESSAGES).push().getKey();
        p.setMessageUID(key);
        mDatabase.child(MyUtil.COLUMN_USERS).child(user.getUid()).child(MyUtil.COLUMN_MESSAGES).child(friendUID).child(key).setValue(p);
        p.setMySender(false);
        mDatabase.child(MyUtil.COLUMN_USERS).child(friendUID)
                .child(MyUtil.COLUMN_MESSAGES)
                .child(user.getUid()).child(key).setValue(p);
    }
    public void uploadVoice (FirebaseUser user,String recordName,Uri recordUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference uppURIstorageRef = storageReference.child("post").child(user.getUid()).child(recordName);

        uppURIstorageRef.putFile(recordUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                StorageReference downUrlfindREF = FirebaseStorage.getInstance().getReference().child("post").child(user.getUid()).child(recordName);

                downUrlfindREF.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadURL = uri.toString();
                        //System.out.println("download URL: " + downloadURL);

                        FirebaseUser user = mAuth.getCurrentUser();

                        //String text,String giveUID, String awayUID,int typeMessage
                        Post postVoice=new Post(downloadURL,friendUID,user.getUid(),Post.POST_AUDIO);
                        writeDatabese(postVoice);

                        Toast.makeText(getApplicationContext(),"Uploaded!",Toast.LENGTH_LONG).show();


                    }
                });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });



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