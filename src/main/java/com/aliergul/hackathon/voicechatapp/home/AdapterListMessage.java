package com.aliergul.hackathon.voicechatapp.home;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.LineTextMessageBinding;
import com.aliergul.hackathon.voicechatapp.databinding.LineVoiceMessageBinding;
import com.aliergul.hackathon.voicechatapp.model.Post;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AdapterListMessage extends RecyclerView.Adapter {
    private List<Post> liste;
    private Context mContext;
    private String myUid;

    public AdapterListMessage(List<Post> liste, Context mContext) {
        this.liste = liste;
        this.mContext = mContext;
        this.myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:{
                LineTextMessageBinding binding=LineTextMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
                return new TextHolder(binding);
            }
            case 1:{
                LineVoiceMessageBinding binding=LineVoiceMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
                return new VoiceHolder(binding);
            }
            default:{
                return null;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderItem, int position) {
        Post post=liste.get(position);
        /*--- Post Text --- */
        if(post.getTypeMessage()==Post.POST_TEXT){
            TextHolder holder=(TextHolder) holderItem;
            holder.binding.tvMessage.setText(post.getText());
            holder.binding.tvDate.setText(MyUtil.getTimestampSecond(post));

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.binding.cardText.getLayoutParams();
            if(myUid.equals(post.getSendUID())){
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.binding.cardText.setPadding(20,0,50,0);
                holder.binding.cardText.setLayoutParams(params);
                holder.binding.cardText.setBackgroundColor(Color.BLUE);
                holder.binding.cardText.setBackground(mContext.getDrawable(R.drawable.shape_bg_outgoing_bubble));
                //tarih
                LinearLayout.LayoutParams lnp=(LinearLayout.LayoutParams)holder.binding.tvDate.getLayoutParams();
                lnp.gravity=Gravity.START;

                holder.binding.tvDate.setLayoutParams(lnp);
                //messaj
                CardView.LayoutParams paramMessage=(CardView.LayoutParams)holder.binding.tvMessage.getLayoutParams();
                paramMessage.gravity=Gravity.END;
                holder.binding.tvMessage.setLayoutParams(paramMessage);


            }else{
                holder.binding.cardText.setPadding(50,0,20,0);
                holder.binding.cardText.setLayoutParams(params);
                holder.binding.cardText.setBackgroundColor(Color.GREEN);
                holder.binding.cardText.setBackground(mContext.getDrawable(R.drawable.shape_bg_incoming_bubble));
                //Tarih
                LinearLayout.LayoutParams paramDate=(LinearLayout.LayoutParams)holder.binding.tvDate.getLayoutParams();
                paramDate.gravity=Gravity.END;
                holder.binding.tvDate.setLayoutParams(paramDate);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                //messaj
                CardView.LayoutParams paramMessage=(CardView.LayoutParams)holder.binding.tvMessage.getLayoutParams();
                paramMessage.gravity=Gravity.START;
                holder.binding.tvMessage.setLayoutParams(paramMessage);
            }
            /*--- Post Audio --- */
        }else if(post.getTypeMessage()==Post.POST_AUDIO){
            VoiceHolder holder=(VoiceHolder) holderItem;
            holder.binding.cardVoice.setPadding(20,0,50,0);
            holder.binding.tvDate.setText(MyUtil.getTimestampSecond(post));
            holder.binding.listTitle.setText(post.getText()+" sn Sesli Mesaj");
            holder.binding.tvDate.setText(MyUtil.getTimestampSecond(post));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.binding.cardVoice.getLayoutParams();if(myUid.equals(post.getSendUID())){
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                holder.binding.cardVoice.setLayoutParams(params);
                holder.binding.cardVoice.setBackgroundColor(Color.BLUE);
                holder.binding.cardVoice.setBackground(mContext.getDrawable(R.drawable.shape_bg_outgoing_bubble));

            }else{
                holder.binding.cardVoice.setPadding(50,0,20,0);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                holder.binding.cardVoice.setLayoutParams(params);
                holder.binding.cardVoice.setBackgroundColor(Color.GREEN);
                holder.binding.cardVoice.setBackground(mContext.getDrawable(R.drawable.shape_bg_incoming_bubble));

            }
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

    public class TextHolder extends RecyclerView.ViewHolder{
        public LineTextMessageBinding binding;
        public TextHolder(@NonNull LineTextMessageBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
    public class VoiceHolder extends RecyclerView.ViewHolder{
        public LineVoiceMessageBinding binding;
        public VoiceHolder(@NonNull LineVoiceMessageBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
