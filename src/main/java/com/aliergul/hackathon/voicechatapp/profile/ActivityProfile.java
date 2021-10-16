package com.aliergul.hackathon.voicechatapp.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.ActivityProfileBinding;
import com.aliergul.hackathon.voicechatapp.login.ActivityLoginAndRegister;
import com.aliergul.hackathon.voicechatapp.model.Users;
import com.aliergul.hackathon.voicechatapp.util.BottomNavigationHelper;
import com.aliergul.hackathon.voicechatapp.util.FirebaseHelper;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
public class ActivityProfile extends AppCompatActivity {
    public static final int CHOOSE_PIC_REQUEST_CODE = 1;
    private Uri mMediaUri;
    private static final String TAG = "ActivityProfile";
    private static final int ACTIVITY_NUM = 2;
    private ActivityProfileBinding binding;
    private Users activeUser;                //image, fullname, email, şifre
    private boolean isImegeChanged=false;
    private boolean isPasword=false;
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
            menu.setOnMenuItemClickListener(menuItem -> {
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
            });
            menu.show();

        });
    }

    private void editProfile() {
       modeEditViewButton();
       //Resim Değiştir Tıklama
       binding.btnChangeImage.setOnClickListener(v-> {
           //upload image

           Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
           choosePictureIntent.setType("image/*");
           startActivityForResult(choosePictureIntent, CHOOSE_PIC_REQUEST_CODE);


       });
       // Değişiklikleri kaydet Button
        binding.btnSave.setOnClickListener(v->{

            String email=binding.edtUserEmailId.getText().toString().trim();
            if(MyUtil.validate(email)){

                binding.edtUserEmailId.setError(null);

            //btn kaydet
            if(isImegeChanged){
                showProgress();
                //resim değişti
                    uploadImage();
            }
            if(!binding.edtFullName.getText().toString().trim().equals(activeUser.getUserName())){

                //fullName değişti
                FirebaseHelper.reFirebaseColumn(MyUtil.FULL_NAME,binding.edtFullName.getText().toString().trim());
            }
            if(!email.equals(activeUser.getUserEmail())){
                //Email değişti

                FirebaseHelper.reFirebaseColumn(MyUtil.EMAIL,binding.edtUserEmailId.getText().toString().trim());
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                user.updateEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "Email Güncellendi.");
                            }
                        });
            }


            modeShowViewButton();
            }else{
                binding.edtUserEmailId.setError(getString(R.string.invalidEmail));
            }
        });

        // Pasword Değiştir
        binding.btnPassChange.setOnClickListener(v->{
            if(!isPasword){
                isPasword=false;
                binding.btnPassChange.setError(null);
                DialogChoseePanel dialogChoseePanel=new DialogChoseePanel("Şifre Değiştirme", "Eski Şifrenizi Giriniz", this, new DialogChoseePanel.IOnReadPassword() {

                    @Override
                    public void onReadPasword(String pass, boolean isValided) {
                        if(isValided){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(activeUser.getUserEmail(), pass);

                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            binding.password.setVisibility(View.VISIBLE);
                                            binding.confirmPassword.setVisibility(View.VISIBLE);
                                            isPasword=true;
                                        }
                                    });
                        }else{
                            binding.btnPassChange.setError(getString(R.string.passowrd));
                        }
                    }
                });

                dialogChoseePanel.show(getSupportFragmentManager(),"password");
            }else if(isEmailAndPaswordConrol()){
                // Şifre İki sefer Girilir.
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String newPassword = binding.password.getText().toString();

                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User password updated.");
                                    Toast.makeText(ActivityProfile.this,getString(R.string.changedpassword),Toast.LENGTH_LONG).show();
                                    binding.password.setVisibility(View.GONE);
                                    binding.confirmPassword.setVisibility(View.GONE);
                                    FirebaseAuth.getInstance().signOut();
                                    openLoginPanel();

                                }
                            }
                        });

               }


        });

    }

    private boolean isEmailAndPaswordConrol(){

        if(binding.password.getText().toString().length()<6){
            binding.password.setError(getString(R.string.invalidPassword));
            return false;
        }else{
            binding.password.setError(null);

        }
        if(binding.confirmPassword.getText().toString().length()<6){
            binding.confirmPassword.setError(getString(R.string.invalidPassword));
            return false;
        }else{
            binding.confirmPassword.setError(null);
        }
        if(!binding.confirmPassword.getText().toString().equals(binding.password.getText().toString())){
            binding.confirmPassword.setError(getString(R.string.invalidPassword2));
            return false;
        }else{
            binding.confirmPassword.setError(null);
        }

        return true;
    }

    //check if external storage is mounted. helper method
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private void modeEditViewButton(){
        binding.btnPassChange.setVisibility(View.VISIBLE);
        binding.btnOptions.setVisibility(View.GONE);
        binding.tvHeader.setVisibility(View.GONE);
        binding.btnCancel.setVisibility(View.VISIBLE);
        binding.btnSave.setVisibility(View.VISIBLE);
        binding.btnChangeImage.setVisibility(View.VISIBLE);
        binding.edtFullName.setEnabled(true);
        binding.edtUserEmailId.setEnabled(true);
    }
    private void modeShowViewButton(){
        binding.btnPassChange.setVisibility(View.GONE);
        binding.btnOptions.setVisibility(View.VISIBLE);
        binding.tvHeader.setVisibility(View.VISIBLE);
        binding.btnCancel.setVisibility(View.GONE);
        binding.btnSave.setVisibility(View.GONE);
        binding.btnChangeImage.setVisibility(View.GONE);
        binding.edtFullName.setEnabled(false);
        binding.edtUserEmailId.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_PIC_REQUEST_CODE) {
                if (data == null) {
                    Toast.makeText(getApplicationContext(), "Bir resim seçmelisiniz!", Toast.LENGTH_LONG).show();
                } else {
                    mMediaUri = data.getData();
                    isImegeChanged=true;
                    Picasso.get()
                            .load(mMediaUri)
                            .resize(110,110)
                            .centerCrop()
                            .into(binding.profileImage);
                    //Bundle extras = data.getExtras();

                    //Log.e("URI", mMediaUri.toString());

                    //Bitmap bmp = (Bitmap) extras.get("data");


                }
            }

        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Cancelled!", Toast.LENGTH_LONG).show();
        }
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
            if(u.getUserPhoto().length()>5){
                showProgress();
                Picasso.get()
                        .load(u.getUserPhoto())
                        .resize(110,110)
                        .centerCrop()
                        .into(binding.profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                hideProgress();
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }
        }

    }


    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationView bottomNavigationViewEx = findViewById(R.id.bottomNavigationView);
        BottomNavigationHelper.setupBottomNavigationView(bottomNavigationViewEx,ACTIVITY_NUM);
        BottomNavigationHelper.enableNavigation(this, this,bottomNavigationViewEx);

    }
    public void uploadImage () {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference uppURIstorageRef = storageReference.child(MyUtil.IMAGE_URL)
                .child(activeUser.getUserUID()).child(MyUtil.IMAGE_URL);
        uppURIstorageRef.putFile(mMediaUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                StorageReference downUrlfindREF = FirebaseStorage.getInstance()
                        .getReference().child(MyUtil.IMAGE_URL).child(activeUser.getUserUID()).child(MyUtil.IMAGE_URL);

                downUrlfindREF.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadURL = uri.toString();
                        //System.out.println("download URL: " + downloadURL);
                        //String sendUID, String text, int typeMessage, String voiceURL
                        if(downloadURL!=null || downloadURL.length()>0){
                            FirebaseHelper.reFirebaseColumn(MyUtil.IMAGE_URL,downloadURL);
                            isImegeChanged=false;

                        }
                        hideProgress();

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgress();
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });



    }
    private void showProgress() {
        binding.btnSave.setEnabled(false);
        binding.profileProgresbar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {

        binding.btnSave.setEnabled(true);
        binding.profileProgresbar.setVisibility(View.GONE);
    }

}