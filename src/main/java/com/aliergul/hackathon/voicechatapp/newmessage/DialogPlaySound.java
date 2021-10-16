package com.aliergul.hackathon.voicechatapp.newmessage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.DialogVoiceChooseEffectBinding;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.arthenica.mobileffmpeg.FFmpeg;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;

public class DialogPlaySound extends DialogFragment  {
    private static final String TAG = "DialogPlaySound";
    DialogVoiceChooseEffectBinding binding ;

    private String fileName  = "";
    private String fileNameNew  = "";
    private String fileNameMerge  = "";
    private boolean isEffectAddedOnce = false;
    private MediaRecorder recorder = null;
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private Context mContext;
    private Uri uri;
    private  IOnSenVoiceCloud onSenVoiceCloud;
    interface IOnSenVoiceCloud{
        void onSendVoiceCloud(String fileName);
        void setEnabledSendBtn(boolean isEnabled);
    }
    public DialogPlaySound(Context mContext, String fileName,IOnSenVoiceCloud onSenVoiceCloud) {
        this.mContext=mContext;
        this.uri=uri;
        //path== /storage/emulated/0/Android/data/com.aliergul.hackathon.voicechatapp/files/Recording_temp.3gp
        this.fileName=fileName;
        fileNameNew = mContext.getExternalFilesDir("/").getAbsolutePath()+ "/audioRecordNew.mp3";
        fileNameMerge = mContext.getExternalFilesDir("/").getAbsolutePath()+ "/audioRecordMerge.mp3";
        this.onSenVoiceCloud=onSenVoiceCloud;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding=DialogVoiceChooseEffectBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot());
        binding.rgEffectgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rd_Radio:
                        playRadio(fileName,fileNameNew);
                        break;
                    case R.id.rd_Robot:
                        playRobot(fileName,fileNameNew);
                        break;
                    case R.id.rd_Case:
                        playCave(fileName,fileNameNew);
                        break;
                    case R.id.rd_ChipMunk:
                        playChipmunk(fileName,fileNameNew);
                        break;
                }
            }
        });
        // Gönder Butonu
        binding.voiceSend.setOnClickListener(v->{
            this.dismiss();
            onSenVoiceCloud.onSendVoiceCloud(fileNameNew);


        });

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        onSenVoiceCloud.setEnabledSendBtn(true);
    }

    /**
     * FFMPEG Sorgusu yürütme işlevi
     */
    private void exceuteFFMPEG(String[] cmd) {
        FFmpeg.execute(cmd);
        int rc = FFmpeg.getLastReturnCode();
        String output= FFmpeg.getLastCommandOutput();

        if (rc == FFmpeg.RETURN_CODE_SUCCESS) {
            Log.i("GetInfo", "Komut yürütme başarıyla tamamlandı.");
              hideProgress();
            isEffectAddedOnce = true;
            start();
        } else if (rc == FFmpeg.RETURN_CODE_CANCEL) {
            Log.i("GetInfo", "Komut yürütme kullanıcı tarafından iptal edildi.");
        } else {
            Log.i(
                    "GetInfo",
                    String.format(
                            "Komut yürütme ile başarısız oldu rc=%d and output=%s.",
                            rc,
                            output
                    )
            );
        }
    }

    private void start() {
        mediaPlayer = new MediaPlayer();
            try {
                isPlaying=true;
                mediaPlayer.setDataSource(fileNameNew);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                Log.e(TAG, "prepare() failed");
            }
                binding.btnPlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_circle, null));
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    binding.btnPlay.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_circle, null));
                }
            });

    }
/**
 *Sesi Yankılı gibi çalmak için kullanılan işlev
 */
private void playCave(String fileName1 , String fileName2) {
    showProgress();

    String[] cmd = new String[] {
            "-y",
            "-i",
            fileName1,
            "-af",
            "aecho=0.8:0.9:1000:0.3",
            fileName2
    };//Radio

    exceuteFFMPEG(cmd);

}
    /**
     * Sesi bir Robot gibi çalmak için kullanılan işlev
     */
    private void playRobot(String fileName1 , String fileName2) {
        showProgress();

        String[] cmd = new String[] {
                "-y",
                "-i",
                fileName1,
                "-af",
                "asetrate=11100,atempo=4/3,atempo=1/2,atempo=3/4",
                fileName2
        };//Radio

        exceuteFFMPEG(cmd);

    }
    /**
     * Sesi bir Sincap gibi çalmak için kullanılan işlev
     */
    private void playChipmunk(String fileName1 , String fileName2) {
        showProgress();

        String[] cmd = new String[] {
                "-y",
                "-i",
                fileName1,
                "-af",
                "asetrate=22100,atempo=1/2",
                fileName2
        };//Radio

        exceuteFFMPEG(cmd);

    }



    /**
     * Sesi Radyo gibi çalmak için kullanılan işlev
     */
    private void playRadio(String fileName1 , String fileName2) {
         showProgress();

        String[] cmd = new String[] {
                "-y",
                "-i",
                fileName1,
                "-af",
                "atempo=1",
                fileName2
        };//Radio

        exceuteFFMPEG(cmd);

    }
    private void showProgress() {
        binding.progressChoseEffect.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        binding.progressChoseEffect.setVisibility(View.GONE);
    }


}
