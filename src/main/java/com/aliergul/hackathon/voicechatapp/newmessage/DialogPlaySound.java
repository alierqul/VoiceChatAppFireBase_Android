package com.aliergul.hackathon.voicechatapp.newmessage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.DialogVoiceChooseEffectBinding;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.arthenica.mobileffmpeg.FFmpeg;
import java.io.IOException;


public class DialogPlaySound extends DialogFragment implements View.OnClickListener {

    public DialogPlaySound(Context mContext, String fileName,IOnSenVoiceCloud onSenVoiceCloud) {
        this.mContext=mContext;

        //path== /storage/emulated/0/Android/data/com.aliergul.hackathon.voicechatapp/files/Recording_temp.3gp
        this.fileName=fileName;
        fileNameNew = mContext.getExternalFilesDir("/").getAbsolutePath()+ "/audioRecordNew.mp3";
        String fileNameMerge = mContext.getExternalFilesDir("/").getAbsolutePath() + "/audioRecordMerge.mp3";
        this.onSenVoiceCloud=onSenVoiceCloud;

    }
    private static final String TAG = "DialogPlaySound";
    DialogVoiceChooseEffectBinding binding ;

    private String fileName  = "";
    private String fileNameNew  = "";
    private  MediaRecorder recorder = null;
    private  Context mContext;
    private IOnSenVoiceCloud onSenVoiceCloud;


    private void checkClear(){
        binding.rdCase.setChecked(false);
        binding.rdChipMunk.setChecked(false);
        binding.rdOrinal.setChecked(false);
        binding.rdRadio.setChecked(false);
        binding.rdRobot.setChecked(false);
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding=DialogVoiceChooseEffectBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot());
        binding.rdCase.setOnClickListener(this);
        binding.rdChipMunk.setOnClickListener(this);
        binding.rdOrinal.setOnClickListener(this);
        binding.rdRadio.setOnClickListener(this);
        binding.rdRobot.setOnClickListener(this);

        // Gönder Butonu
        binding.voiceSend.setOnClickListener(v->{
            this.dismiss();
            if(!binding.rdOrinal.isChecked())
                onSenVoiceCloud.onSendVoiceCloud(fileNameNew);
            else
                onSenVoiceCloud.onSendVoiceCloud(fileName);

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
            boolean isEffectAddedOnce = true;
            start(fileNameNew);
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

    private void start(String fileNameNew) {
        MediaPlayer mediaPlayer = new MediaPlayer();
            try {

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


    @Override
    public void onClick(View view) {
        checkClear();
        switch (view.getId()) {
            case R.id.rd_Radio: {

                playRadio(fileName, fileNameNew);
                break;
            }

            case R.id.rd_Robot: {

                playRobot(fileName, fileNameNew);
                break;
            }

            case R.id.rd_Case: {

                playCave(fileName, fileNameNew);
                break;
            }

            case R.id.rd_ChipMunk: {

                playChipmunk(fileName, fileNameNew);
                break;
            }

            case R.id.rd_orinal: {

                start(fileName);
                break;
            }


        }
        ((RadioButton) view).setChecked(true);
    }
}
