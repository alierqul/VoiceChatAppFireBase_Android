package com.aliergul.hackathon.voicechatapp.login;

import androidx.fragment.app.Fragment;

public interface IMoveFragment {
    public static final int LOGIN_PANEL=1;
    public static final int REGISTER_PANEL=2;
    public static final int NOT_LOGIN=3;
    public void openFragment(int fragmentNumber);
}
