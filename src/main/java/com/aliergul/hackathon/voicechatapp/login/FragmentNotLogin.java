package com.aliergul.hackathon.voicechatapp.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.LoginFragmentNotLoginBinding;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;


public class FragmentNotLogin extends Fragment {
    private static final String TAG = "FragmentLoginPanel";
    private LoginFragmentNotLoginBinding binding;
    private IMoveFragment fragmentMove;
    public FragmentNotLogin(IMoveFragment fragmentMove) {
        super(R.layout.login_fragment_login);
        this.fragmentMove=fragmentMove;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        binding= LoginFragmentNotLoginBinding.inflate(inflater, container,false);
        clickItemViews();
        return binding.getRoot();
    }

    private void clickItemViews() {
        binding.backToLoginBtn.setOnClickListener(v->{
            Log.d(TAG, "setOnClickListener: ");
            fragmentMove.openFragment(IMoveFragment.LOGIN_PANEL);
        });
        binding.forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEmailAndPaswordConrol()){

                }
            }
        });
    }

    private boolean isEmailAndPaswordConrol(){

        if(!MyUtil.validate(binding.registeredEmailid.getText().toString())){
            binding.registeredEmailid.setError(getString(R.string.invalidEmail));
            return false;
        }else{
            binding.registeredEmailid.setError(null);
        }

        return true;
    }
}
