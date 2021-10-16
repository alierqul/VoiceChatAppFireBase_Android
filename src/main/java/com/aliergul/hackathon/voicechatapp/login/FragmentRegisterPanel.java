package com.aliergul.hackathon.voicechatapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.LoginFragmentSignupBinding;
import com.aliergul.hackathon.voicechatapp.home.ActivityMessages;
import com.aliergul.hackathon.voicechatapp.model.Users;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

public class FragmentRegisterPanel extends Fragment {
    private static final String TAG ="FragmentRegisterPanel" ;
    private LoginFragmentSignupBinding binding;
    private IMoveFragment fragmentMove;
    private String oneSignalDeviceID;
    public FragmentRegisterPanel(IMoveFragment fragmentMove) {
        super(R.layout.login_fragment_login);
        this.fragmentMove=fragmentMove;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=LoginFragmentSignupBinding.inflate(inflater, container,false);
        clickItemView();
        setupOneSignal();
        binding.signUpBtn.setEnabled(true);
        return binding.getRoot();
    }

    private void setupOneSignal() {
        OSDeviceState device = OneSignal.getDeviceState();

        oneSignalDeviceID = device.getUserId();
    }

    private void clickItemView() {
        binding.alreadyUser.setOnClickListener(v->{
            fragmentMove.openFragment(IMoveFragment.LOGIN_PANEL);
        });
        binding.signUpBtn.setOnClickListener(v->{
            if(isEmailAndPaswordConrol()){

                showProgress();
                registerFirebase();

            }
        });
    }

    private void registerFirebase() {
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        Users createUser=new Users("",binding.edtFullName.getText().toString(),binding.edtUserEmailId.getText().toString(),"");
        mAuth.createUserWithEmailAndPassword(createUser.getUserEmail(), binding.password.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                createUser.setUserUID(firebaseUser.getUid());
                                createUser.setOneSignalDeviceID(oneSignalDeviceID);
                            updateUI(createUser);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            hideProgress();
                            Toast.makeText(getActivity(), getString(R.string.invalidDatabase),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                    private void updateUI( Users createUser) {

                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("Users").child(createUser.getUserUID()).setValue(createUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Log.w(TAG,"onComplete="+createUser.toString());
                                    hideProgress();
                                    openHomeActivity();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "addOnCompleteListener:success"+ e.getMessage());
                                Toast.makeText(getActivity(),getString(R.string.invalidDatabase),Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
    }



    private void openHomeActivity() {
        Intent i=new Intent(getActivity(), ActivityMessages.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private boolean isEmailAndPaswordConrol(){
        if(binding.edtFullName.getText().toString().length()<1){
            binding.edtFullName.setError(getString(R.string.invalidName));
            return false;
        }else{
            binding.edtFullName.setError(null);
        }

        if(!MyUtil.validate(binding.edtUserEmailId.getText().toString())){
            binding.edtUserEmailId.setError(getString(R.string.invalidEmail));
           return false;
        }else{
            binding.edtUserEmailId.setError(null);
        }
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
        if(!binding.termsConditions.isChecked()){
            binding.termsConditions.setError(getString(R.string.invalidContract));
        }else{
            binding.termsConditions.setError(null);
        }
        return true;
    }
    private void showProgress() {
        binding.signUpBtn.setEnabled(false);
        binding.registerProgresbar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {

        binding.signUpBtn.setEnabled(true);
        binding.registerProgresbar.setVisibility(View.GONE);
    }

}
