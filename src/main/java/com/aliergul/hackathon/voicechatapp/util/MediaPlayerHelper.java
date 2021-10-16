package com.aliergul.hackathon.voicechatapp.util;

import android.content.Context;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.aliergul.hackathon.voicechatapp.R;

import java.io.IOException;

public  class MediaPlayerHelper {
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private Runnable updateSeekbar;
    private Handler seekbarHandler;
    private Context mContext;
    private ImageView playBtn;
    private SeekBar seekBar;

    public  MediaPlayerHelper(Context context, SeekBar seekBar, ImageView playBtn) {
        this.mContext=context;
        this.playBtn=playBtn;
        this.seekBar=seekBar;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });
    }

    public void playAudio(Uri uri) {

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(mContext,uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_circle, null));

        //Play the audio
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();

            }
        });

        seekBar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }
    private void pauseAudio() {
        mediaPlayer.pause();
        playBtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_circle, null));
        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio() {
        mediaPlayer.start();
        playBtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_circle, null));
        isPlaying = true;

        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);

    }
    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }
    public void stopAudio() {
        //Stop The Audio
        playBtn.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_circle, null));
        isPlaying = false;
        mediaPlayer.stop();
        seekBar.removeCallbacks(updateSeekbar);
    }

}

