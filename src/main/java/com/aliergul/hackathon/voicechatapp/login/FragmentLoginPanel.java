package com.aliergul.hackathon.voicechatapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.LoginFragmentLoginBinding;
import com.aliergul.hackathon.voicechatapp.home.ActivityMessages;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentLoginPanel extends Fragment {
    private static final String TAG = "FragmentLoginPanel";
    private LoginFragmentLoginBinding binding;
    private IMoveFragment fragmentMove;
    public FragmentLoginPanel(IMoveFragment fragmentMove) {
        super(R.layout.login_fragment_login);
        this.fragmentMove=fragmentMove;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        binding= LoginFragmentLoginBinding.inflate(inflater, container,false);
        clickItemView();


        return binding.getRoot();
    }

    private void clickItemView() {
        binding.createAccount.setOnClickListener(v->{

            fragmentMove.openFragment(IMoveFragment.REGISTER_PANEL);
        });

        binding.dontLogin.setOnClickListener(v->{
            fragmentMove.openFragment(IMoveFragment.NOT_LOGIN);
        });

        binding.showHidePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.d(TAG, "onCheckedChanged: "+b);
                if(b){
                    binding.showHidePassword.setText(R.string.hide_pwd);
                    binding.loginPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                }else{
                    binding.showHidePassword.setText(R.string.show_pwd);
                    binding.loginPassword.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        binding.loginBtn.setOnClickListener(v->{
            if(isEmailAndPaswordConrol()){
                    loginFirebase();
            }
        });

    }

    private void loginFirebase() {
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(binding.loginEmailid.getText().toString(), binding.loginPassword.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        openHomeActivity();
    }
    private void openHomeActivity() {
        Intent i=new Intent(getActivity(), ActivityMessages.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    private boolean isEmailAndPaswordConrol(){

        if(!MyUtil.validate(binding.loginEmailid.getText().toString())){
            binding.loginEmailid.setError(getString(R.string.invalidEmail));
           return false;
        }else{

            binding.loginEmailid.setError(null);
        }
        if(binding.loginPassword.getText().toString().length()<6){
            binding.loginPassword.setError(getString(R.string.invalidPassword));
            return false;
        }else{
            binding.loginPassword.setError(null);

        }
        return true;
    }



}
