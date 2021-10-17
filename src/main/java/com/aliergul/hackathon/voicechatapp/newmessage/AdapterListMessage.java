package com.aliergul.hackathon.voicechatapp.newmessage;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.LineTextMessageBinding;
import com.aliergul.hackathon.voicechatapp.databinding.LineVoiceMessageBinding;
import com.aliergul.hackathon.voicechatapp.model.Post;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;

public class AdapterListMessage extends RecyclerView.Adapter {
    private List<Post> liste;
    private ActivityNewMessage mContext;
    private String myUid;
    private enum ERunning{
        PAUSE,RESUME,STOP
    }
    public AdapterListMessage(List<Post> liste, ActivityNewMessage mContext) {
        this.liste = liste;
        this.mContext = mContext;
        this.myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: {
                LineTextMessageBinding binding = LineTextMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new TextHolder(binding);
            }
            case 1: {
                LineVoiceMessageBinding binding = LineVoiceMessageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new VoiceHolder(binding);
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderItem, int position) {
        Post post = liste.get(position);
        /*--- Post Text --- */
        if (post.getTypeMessage() == Post.POST_TEXT) {
            TextHolder holder = (TextHolder) holderItem;
            holder.binding.tvMessage.setText(post.getText());
            holder.binding.tvDate.setText(MyUtil.getTimestampSecond(post));

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.binding.cardText.getLayoutParams();
            if (myUid.equals(post.getSendUID())) {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.binding.cardText.setPadding(20, 0, 50, 0);
                holder.binding.cardText.setLayoutParams(params);
                holder.binding.cardText.setBackgroundColor(Color.BLUE);
                holder.binding.cardText.setBackground(mContext.getDrawable(R.drawable.shape_bg_outgoing_bubble));
                //tarih
                LinearLayout.LayoutParams lnp = (LinearLayout.LayoutParams) holder.binding.tvDate.getLayoutParams();
                lnp.gravity = Gravity.START;

                holder.binding.tvDate.setLayoutParams(lnp);
                //messaj
                LinearLayout.LayoutParams paramMessage = (LinearLayout.LayoutParams) holder.binding.tvMessage.getLayoutParams();
                paramMessage.gravity = Gravity.END;
                holder.binding.tvMessage.setLayoutParams(paramMessage);


            } else {
                holder.binding.cardText.setPadding(50, 0, 20, 0);
                holder.binding.cardText.setLayoutParams(params);
                holder.binding.cardText.setBackgroundColor(Color.GREEN);
                holder.binding.cardText.setBackground(mContext.getDrawable(R.drawable.shape_bg_incoming_bubble));
                //Tarih
                LinearLayout.LayoutParams paramDate = (LinearLayout.LayoutParams) holder.binding.tvDate.getLayoutParams();
                paramDate.gravity = Gravity.END;
                holder.binding.tvDate.setLayoutParams(paramDate);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                //messaj
                LinearLayout.LayoutParams paramMessage = (LinearLayout.LayoutParams) holder.binding.tvMessage.getLayoutParams();
                paramMessage.gravity = Gravity.START;
                holder.binding.tvMessage.setLayoutParams(paramMessage);
            }
            /*--- Post Audio --- */
        } else if (post.getTypeMessage() == Post.POST_AUDIO) {
            VoiceHolder holder = (VoiceHolder) holderItem;
            holder.binding.cardVoice.setPadding(20, 0, 50, 0);
            holder.binding.tvDate.setText(MyUtil.getTimestampSecond(post));
            holder.binding.listTitle.setText(post.getText() + " sn Sesli Mesaj");
            holder.binding.tvDate.setText(MyUtil.getTimestampSecond(post));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.binding.cardVoice.getLayoutParams();
            if (myUid.equals(post.getSendUID())) {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                holder.binding.cardVoice.setLayoutParams(params);
                holder.binding.cardVoice.setBackgroundColor(Color.BLUE);
                holder.binding.cardVoice.setBackground(mContext.getDrawable(R.drawable.shape_bg_outgoing_bubble));

            } else {
                holder.binding.cardVoice.setPadding(50, 0, 20, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                holder.binding.cardVoice.setLayoutParams(params);
                holder.binding.cardVoice.setBackgroundColor(Color.GREEN);
                holder.binding.cardVoice.setBackground(mContext.getDrawable(R.drawable.shape_bg_incoming_bubble));

            }

            Uri uri = Uri.parse(liste.get(position).getVoiceURL());
            holder.getUri(uri);


        }

    }


    @Override
    public int getItemViewType(int position) {
        return liste.get(position).getTypeMessage();
    }

    @Override
    public int getItemCount() {
        return liste.size();
    }

    public class TextHolder extends RecyclerView.ViewHolder {
        public LineTextMessageBinding binding;

        public TextHolder(@NonNull LineTextMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class VoiceHolder extends RecyclerView.ViewHolder {
        public LineVoiceMessageBinding binding;
        private Uri uri;

        public VoiceHolder(@NonNull LineVoiceMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.listImageView.setOnClickListener(v->{
                if(isPlaying==ERunning.STOP){
                    binding.listImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_circle, null));
                    playAudio(uri);
                }else if(isPlaying==ERunning.RESUME){
                    pauseAudio();
                }else if(isPlaying==ERunning.PAUSE){
                    resumeAudio();
                }
            });

            binding.playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        public void getUri(Uri uri){
            this.uri=uri;
        }

        //MediaPlayer
        private MediaPlayer mediaPlayer = null;
        private ERunning isPlaying = ERunning.STOP;
        private Runnable updateSeekbar;
        private Handler seekbarHandler;




        public void playAudio(Uri uri) {

                isPlaying = ERunning.RESUME;
                mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(mContext,uri);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //Play the audio

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopAudio();

                    }
                });

                binding.playerSeekbar.setMax(mediaPlayer.getDuration());

                seekbarHandler = new Handler();
                updateRunnable();
                seekbarHandler.postDelayed(updateSeekbar, 0);



        }
        private void pauseAudio() {
            mediaPlayer.pause();
            binding.listImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_circle, null));
            isPlaying = ERunning.PAUSE;
            seekbarHandler.removeCallbacks(updateSeekbar);
        }

        private void resumeAudio() {
            mediaPlayer.start();
            binding.listImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause_circle, null));
            isPlaying = ERunning.RESUME;

            updateRunnable();
            seekbarHandler.postDelayed(updateSeekbar, 0);

        }
        private void updateRunnable() {
            updateSeekbar = new Runnable() {
                @Override
                public void run() {
                    binding.playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                    seekbarHandler.postDelayed(this, 500);
                }
            };
        }
        public void stopAudio() {
            //Stop The Audio
            binding.listImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play_circle, null));
            isPlaying = ERunning.STOP;
            mediaPlayer.stop();
            binding.playerSeekbar.removeCallbacks(updateSeekbar);
        }
        //MediaPlayer END
    }
}
