package com.aliergul.hackathon.voicechatapp.home;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
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
import com.google.android.gms.tasks.Task;
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
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActivityNewMessage extends FragmentActivity {
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private static final String TAG = "ActivityNewMessage";
    private static final int FRAGMENT_ID = 100;
    private int PERMISSION_CODE = 21;
    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;
    private String recordFileName;
    private ActivityNewMessageBinding binding;
    private Users friendUser;
    private Users actUser;

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

        clickItemView();
    }

    private void clickItemView() {
        FirebaseHelper.reWriteOneSignalKey();
        binding.btnBack.setOnClickListener(v->{
            onBackPressed();
        });
        binding.btnSenMessage.setOnClickListener(v->{
            Post postText=new Post
                    ( binding.edtMessage.getText().toString()
                    , FirebaseAuth.getInstance().getCurrentUser().getUid()
                    , Post.POST_TEXT);
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
                    if(checkPermissions()) {
                        startRecording();
                        playBeep();
                        binding.recordTimer.setVisibility(View.VISIBLE);
                        binding.btnSenVoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_start, null));
                        binding.btnSenMessage.setEnabled(false);
                        isRecording = true;
                    }
                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP && isRecording){
                    binding.btnSenVoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_voice, null));
                    stopRecording();
                    binding.btnSenVoice.setEnabled(false);
                    binding.recordTimer.setVisibility(View.GONE);
                    binding.btnSenMessage.setEnabled(true);
                }
                return false;
            }

        });

    }

    private void playBeep() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP,150);
    }

    private void checkPermissionAndStart() {
        if(checkPermissions()) {
            startRecording();
            binding.btnSenVoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_record_start, null));

        }
    }
    private void stopRecording() {
        binding.recordTimer.stop();
        isRecording=false;
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        String path =this.getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        //File[] files = directory.listFiles();
        //Uri uri=Uri.parse(this.getExternalFilesDir("/").getAbsolutePath()+"/"+ recordFileName);
        File dir = new File(Environment.getExternalStorageDirectory(), "subDir");
        File file = new File(path,recordFileName);
        uploadVoice(recordFileName,Uri.fromFile(file));
    }
    private void startRecording() {
        binding.recordTimer.setBase(SystemClock.elapsedRealtime());
        binding.recordTimer.start();
        String recordPath = this.getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.getDefault());
        Date now = new Date();
        recordFileName = "Recording_" + formatter.format(now) + ".3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFileName);
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

        if(actUser!=null){
            Log.w(TAG,"getListMessages actUser=TRUE");
            mDatabase.child(MyUtil.COLUMN_USERS).child(actUser.getUserUID()).child(MyUtil.COLUMN_MESSAGES).child(friendUser.getUserUID()).addValueEventListener(new ValueEventListener() {
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
                        mDatabase.child(MyUtil.COLUMN_USERS)
                                .child(actUser.getUserUID())
                                .child(MyUtil.COLUMN_MESSAGES)
                                .child(friendUser.getUserUID())
                                .child("count").setValue(0);
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
        FirebaseHelper.setUserOnlineDate(MyUtil.IS_ONLINE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.setUserOnlineDate(System.currentTimeMillis()+"");
    }

    private void writeDatabese(Post p) {
        FirebaseHelper.getFriendUserUpgrade();
        binding.edtMessage.setText("");

        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        String key=mDatabase.child(actUser.getUserUID()).child(MyUtil.COLUMN_MESSAGES).push().getKey();
        p.setMessageUID(key);
        mDatabase.child(MyUtil.COLUMN_USERS)
                .child(actUser.getUserUID())
                .child(MyUtil.COLUMN_MESSAGES)
                .child(friendUser.getUserUID())
                .child(key)
                    .setValue(p);
        p.setMySender(false);
        mDatabase.child(MyUtil.COLUMN_USERS).child(friendUser.getUserUID())
                .child(MyUtil.COLUMN_MESSAGES)
                .child(actUser.getUserUID()).child(key).setValue(p);
        sendNoti(friendUser.getOneSignalDeviceID(),friendUser.getUserName(),p.getText());
       mDatabase.child(MyUtil.COLUMN_USERS)
                .child(friendUser.getUserUID())
                .child(MyUtil.COLUMN_MESSAGES)
                .child(actUser.getUserUID())
                .child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       try{
                           int count=snapshot.getValue(Integer.class);
                           mDatabase.child(MyUtil.COLUMN_USERS)
                                   .child(friendUser.getUserUID())
                                   .child(MyUtil.COLUMN_MESSAGES)
                                   .child(actUser.getUserUID())
                                   .child("count").setValue(++count);
                       }catch (Exception e){
                           mDatabase.child(MyUtil.COLUMN_USERS)
                                   .child(friendUser.getUserUID())
                                   .child(MyUtil.COLUMN_MESSAGES)
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
                OneSignal.postNotification(new JSONObject("{'contents': {'en':"+title+" }," +
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

                        Post postVoice=new Post(textMessage,actUser.getUserUID(),Post.POST_AUDIO);
                        postVoice.getVoiceURL();

                        writeDatabese(postVoice);
                        binding.btnSenVoice.setEnabled(true);
                        Toast.makeText(getApplicationContext(),"yüklendi!",Toast.LENGTH_LONG).show();


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