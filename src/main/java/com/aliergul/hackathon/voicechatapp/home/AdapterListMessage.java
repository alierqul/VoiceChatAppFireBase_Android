package com.aliergul.hackathon.voicechatapp.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliergul.hackathon.voicechatapp.databinding.LineTextMessageBinding;
import com.aliergul.hackathon.voicechatapp.model.Post;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AdapterListMessage extends RecyclerView.Adapter<AdapterListMessage.MessageHolder> {
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
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LineTextMessageBinding binding=LineTextMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MessageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Post text=liste.get(position);
        if(text.getTypeMessage()==Post.POST_TEXT){
            holder.binding.tvMessage.setText(text.getText());
            holder.binding.tvDate.setText(MyUtil.dateToString(text.getDate()));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.binding.cardText.getLayoutParams();
            if(myUid.equals(text.getAwayUID())){
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.binding.cardText.setLayoutParams(params);
            }else{
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                holder.binding.cardText.setLayoutParams(params);
            }
        }

    }

    @Override
    public int getItemCount() {
        return liste.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder{
        private LineTextMessageBinding binding;
        public MessageHolder(@NonNull LineTextMessageBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
