package com.aliergul.hackathon.voicechatapp.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.ActivityLoginAndRegisterBinding;


public class ActivityLoginAndRegister extends AppCompatActivity implements IMoveFragment {
    private ActivityLoginAndRegisterBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginAndRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        openFragment(IMoveFragment.LOGIN_PANEL);
        binding.closeActivity.setOnClickListener(v->{
            System.exit(0);
        });
    }


    @Override
    public void openFragment(int fragmentNumber) {
        switch (fragmentNumber){
            case 1: {
                FragmentLoginPanel fragmentLoginPanel=new FragmentLoginPanel(this);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.loginContainer, fragmentLoginPanel, fragmentLoginPanel.getTag())
                        .commit();
                break;
            }
            case 2: {
                FragmentRegisterPanel fragmentRegisterPanel=new FragmentRegisterPanel(this);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.loginContainer, fragmentRegisterPanel, fragmentRegisterPanel.getTag())
                        .commit();
                break;
            }
            case 3: {
                FragmentNotLogin fragmentNotLogin=new FragmentNotLogin(this);
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.loginContainer, fragmentNotLogin, fragmentNotLogin.getTag())
                        .commit();
                break;
            }
        }

    }
}