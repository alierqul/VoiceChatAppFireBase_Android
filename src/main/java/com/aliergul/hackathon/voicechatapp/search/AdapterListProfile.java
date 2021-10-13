package com.aliergul.hackathon.voicechatapp.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliergul.hackathon.voicechatapp.databinding.LineItemProfileBinding;
import com.aliergul.hackathon.voicechatapp.home.ActivityNewMessage;
import com.aliergul.hackathon.voicechatapp.model.Users;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterListProfile extends RecyclerView.Adapter<AdapterListProfile.LineProfileHolder> {
    private List<Users> liste;
    private Context mContext;
    private IClickListener clickListener;
    public AdapterListProfile(List<Users> liste, Context mContext) {
        this.liste = liste;
        this.mContext = mContext;
    }
    public void setListe(List<Users> list){
        this.liste=list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public LineProfileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LineItemProfileBinding binding=LineItemProfileBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new LineProfileHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LineProfileHolder holder, int position) {
        Users user=liste.get(position);
        holder.binding.tvEmail.setText(user.getUserEmail());
        holder.binding.tvFullName.setText(user.getUserName());
        if(user.getUserPhoto().length()>10){
            Picasso.get()
                    .load(user.getUserPhoto())
                    .resize(30, 30)
                    .centerCrop()
                    .into(holder.binding.profileImage);
        }
        holder.binding.btnMessage.setOnClickListener(v->{
            Intent i=new Intent(mContext, ActivityNewMessage.class);
           i.putExtra(MyUtil.USER_UID,user.getUserUID());
           i.putExtra(MyUtil.FULL_NAME,user.getUserName());
            mContext.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return liste.size();
    }

    public void updateList(List<Users> list) {
        this.liste=list;
        notifyDataSetChanged();
    }

    public class LineProfileHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private LineItemProfileBinding binding;
        public LineProfileHolder(@NonNull LineItemProfileBinding itemView) {
            super(itemView.getRoot());
            itemView.getRoot().setOnClickListener(this);
            this.binding=itemView;
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(),view);
        }
    }
    public void setOnItemClickListener(IClickListener clickListener){
        this.clickListener=clickListener;
    }
    public interface IClickListener{
        void onItemClick(int position,View v);
    }
}
