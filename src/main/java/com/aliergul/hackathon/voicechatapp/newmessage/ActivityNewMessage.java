package com.aliergul.hackathon.voicechatapp.newmessage;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.ActivityNewMessageBinding;
import com.aliergul.hackathon.voicechatapp.model.Post;
import com.aliergul.hackathon.voicechatapp.model.Users;
import com.aliergul.hackathon.voicechatapp.util.FirebaseHelper;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityNewMessage extends FragmentActivity {
    private final String recordPermission = Manifest.permission.RECORD_AUDIO;
    private static final String TAG = "ActivityNewMessage";
    private int PERMISSION_CODE = 200;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private String recordFileName;
    private ActivityNewMessageBinding binding;
    private Users friendUser;
    private Users actUser;
    private EChoseSend choseSend= EChoseSend.AUDIO;
    public enum EChoseSend{
        TEXT,AUDIO;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNewMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        binding.containerMessages.setLayoutManager(manager);
        actUser=FirebaseHelper.getActiveUser();
        friendUser=FirebaseHelper.getFriendUser();
        if(friendUser.getUserPhoto().length()>5){
            Picasso.get()
                    .load(friendUser.getUserPhoto())
                    .resize(30, 30)
                    .centerCrop()
                    .into(binding.friendProfileImg);
        }
       
        binding.tvFriendName.setText(friendUser.getUserName());
        FirebaseDatabase.getInstance().getReference()
                .child(MyUtil.COLUMN_MESSAGES)
                .child(actUser.getUserUID())
                .child(friendUser.getUserUID())
                .child("count").setValue(0);
        clickItemView();

    }

    private void clickItemView() {

        FirebaseHelper.reWriteOneSignalKey();
        binding.btnBack.setOnClickListener(v->{
            onBackPressed();
        });

        voiceOpenDialog();
        getListMessages();
        btnSeendVoiceandTextChosee();

    }
    private void btnSeendVoiceandTextChosee() {
        binding.edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable key) {
                if(key!=null && key.length()>0){
                    binding.imgSendbtn.setImageResource(R.drawable.ic_send);
                    choseSend=EChoseSend.TEXT;
                }else{
                    binding.imgSendbtn.setImageResource(R.drawable.ic_voice);
                    choseSend=EChoseSend.AUDIO;
                }
            }
        });

    }
    /**
     * ses Kayıt fragment aç
     */
    private void voiceOpenDialog() {
        binding.btnSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                long time=SystemClock.elapsedRealtime()-binding.recordTimer.getBase();
                if(choseSend== EChoseSend.AUDIO){
                    //ADUDIO SEÇİM
                    if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                        if(checkPermissions()) {
                            MyUtil.playBeep();
                            binding.recordTimer.setVisibility(View.VISIBLE);
                            binding.imgSendbtn.setImageResource(R.drawable.ic_pause_circle);

                            startRecording();
                        }
                    }else if(motionEvent.getAction()==MotionEvent.ACTION_UP && isRecording){

                        stopRecording();


                        binding.imgSendbtn.setImageResource(R.drawable.ic_voice);
                        binding.recordTimer.setVisibility(View.GONE);

                    }
                }else{
                    //text mesaj
                    if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                        Post postText=new Post
                                ( binding.edtMessage.getText().toString()
                                        , FirebaseAuth.getInstance().getCurrentUser().getUid()
                                        , Post.POST_TEXT);
                        writeDatabese(postText);
                    }

                }

                return false;
            }

        });

    }


    private void stopRecording() {
        if(isRecording){
            try{
                binding.recordTimer.stop();
                isRecording=false;
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                long time=SystemClock.elapsedRealtime()-binding.recordTimer.getBase();

                if(time>=1000L){
                    binding.btnSend.setEnabled(false);
                    effectDialogOpen();
                }
            }catch (Exception e){
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
            }

        }

    }

    private void effectDialogOpen() {
        String path =this.getExternalFilesDir("/").getAbsolutePath();
        DialogPlaySound dialog = new DialogPlaySound(ActivityNewMessage.this, path + "/" + recordFileName, new IOnSenVoiceCloud() {
            @Override
            public void onSendVoiceCloud(String fileName) {
                File file = new File(path,"audioRecordNew.mp3");
                binding.btnSend.setEnabled(false);
                uploadVoice("audioRecordNew.mp3", Uri.fromFile(file));

            }

            @Override
            public void setEnabledSendBtn(boolean isEnabled) {
                binding.btnSend.setEnabled(isEnabled);
            }
        });
        dialog.show(getSupportFragmentManager(), "PlaySound");
    }

    private void startRecording() {
        binding.recordTimer.setBase(SystemClock.elapsedRealtime());
        binding.recordTimer.start();
        String recordPath = this.getExternalFilesDir("/").getAbsolutePath();

        recordFileName = "Recording_temp.mp3";
        mediaRecorder = new MediaRecorder();
        try {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IOException e) {
            isRecording = false;
            e.printStackTrace();
        }

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

        if(actUser!=null){
            Log.w(TAG,"getListMessages actUser=TRUE");
            mDatabase.child(MyUtil.COLUMN_MESSAGES)
                    .child(actUser.getUserUID())
                    .child(friendUser.getUserUID())
                    .orderByChild(MyUtil.POSTDATE)
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue()!=null){
                        listPost.clear();
                        for (DataSnapshot s:snapshot.getChildren()) {
                            try {
                                Post post=s.getValue(Post.class);
                                listPost.add(post);
                                binding.containerMessages.smoothScrollToPosition(listPost.size()-1);

                            }catch (Exception e){
                                Log.e(TAG,"onDataChange Exception ="+e.getLocalizedMessage());
                                e.printStackTrace();
                            }

                        }

                        AdapterListMessage adapter=new AdapterListMessage(listPost,ActivityNewMessage.this);
                        binding.containerMessages.setAdapter(adapter);


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG,"getListMessages onCancelled a");
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseHelper.setUserOnlineDatee(MyUtil.IS_ONLINE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.setUserOnlineDatee(System.currentTimeMillis()+"");
    }

    private void writeDatabese(Post p) {
        binding.edtMessage.setText("");
        binding.btnSend.setEnabled(true);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.getDefault());
        Date now = new Date();
        String key=formatter.format(now);
        p.setMessageUID(key);
        //Notifikasyon Gönderelim
        sendNoti(friendUser.getOneSignalDeviceID(),actUser.getUserName(),p.getText());

        //FireBase
        FirebaseHelper.getFriendUserUpgrade();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();

        // Gönderen Mesaj İşle
        mDatabase.child(MyUtil.COLUMN_MESSAGES)
                .child(actUser.getUserUID())
                .child(friendUser.getUserUID())
                .child(key)
                .setValue(p);

        //Karşı taraf mesaj işle
        mDatabase.child(MyUtil.COLUMN_MESSAGES)
                .child(friendUser.getUserUID())
                .child(actUser.getUserUID())
                .child(key)
                .setValue(p);

        //Okunmamış mesaj Yazımı...
        mDatabase.child(MyUtil.COLUMN_MESSAGES)
                .child(friendUser.getUserUID())
                .child(actUser.getUserUID())
                .child("count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    int count=snapshot.getValue(Integer.class);
                    mDatabase.child(MyUtil.COLUMN_MESSAGES)
                            .child(friendUser.getUserUID())
                            .child(actUser.getUserUID())
                            .child("count").setValue(++count);
                }catch (Exception e){
                    mDatabase.child(MyUtil.COLUMN_MESSAGES)
                            .child(friendUser.getUserUID())
                            .child(actUser.getUserUID())
                            .child("count").setValue(1);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendNoti(String deviceUID, String title,String message){
        try {
            //  "'headings':{'en':"+message +"},"+
            if(!friendUser.getOnlineDate().equals(MyUtil.IS_ONLINE)){
                OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+message+"' }," +
                                "'headings':{'en':'"+title +"'},"+
                                "'include_player_ids': ['" + deviceUID + "']}"),
                        new OneSignal.PostNotificationResponseHandler() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                            }

                            @Override
                            public void onFailure(JSONObject response) {
                                Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                            }
                        });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void uploadVoice (String recordName, Uri recordUri) {



        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference uppURIstorageRef = storageReference.child("post").child(actUser.getUserUID()).child(recordName);
        uppURIstorageRef.putFile(recordUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                StorageReference downUrlfindREF = FirebaseStorage.getInstance().getReference().child("post").child(actUser.getUserUID()).child(recordName);

                downUrlfindREF.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadURL = uri.toString();
                        String textMessage = binding.recordTimer.getText().toString();
                        //System.out.println("download URL: " + downloadURL);
                        //String sendUID, String text, int typeMessage, String voiceURL
                        Post postVoice=new Post(actUser.getUserUID()
                                ,binding.recordTimer.getText().toString()
                                ,Post.POST_AUDIO
                                ,downloadURL
                        );

                        if(downloadURL!=null || downloadURL.length()>0){
                            writeDatabese(postVoice);
                            Toast.makeText(getApplicationContext(),"yüklendi!",Toast.LENGTH_LONG).show();
                        }





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



}