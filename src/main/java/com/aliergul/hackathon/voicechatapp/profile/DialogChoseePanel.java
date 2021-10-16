package com.aliergul.hackathon.voicechatapp.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aliergul.hackathon.voicechatapp.R;

public class DialogChoseePanel extends DialogFragment {

    private String title;
    private String message;
    private Context mContext;
    private IOnReadPassword onReadPassword;
    public DialogChoseePanel(String title, String message, Context mContext,IOnReadPassword onReadPassword) {
        this.title = title;
        this.message = message;
        this.mContext = mContext;
        this.onReadPassword=onReadPassword;
    }

    interface IOnReadPassword{
        void onReadPasword(String pass,boolean isValided);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        EditText edtPaswod=new EditText(mContext);
        edtPaswod.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(message)
                .setView(edtPaswod)
                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(edtPaswod.getText().toString().length()>=6){
                            onReadPassword.onReadPasword(edtPaswod.getText().toString(),true);
                            dismiss();
                        }else{
                            onReadPassword.onReadPasword(edtPaswod.getText().toString(),false);

                        }

                    }
                }).setNegativeButton("Vazgeç",(v,i)->{
                        dismiss();
                });

        return builder.create();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setCanceledOnTouchOutside(false);
    }
}
