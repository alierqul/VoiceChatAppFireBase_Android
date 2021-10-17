package com.aliergul.hackathon.voicechatapp.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aliergul.hackathon.voicechatapp.R;
import com.aliergul.hackathon.voicechatapp.databinding.LineHeaderTextBinding;
import com.aliergul.hackathon.voicechatapp.databinding.LineItemProfileBinding;
import com.aliergul.hackathon.voicechatapp.newmessage.ActivityNewMessage;
import com.aliergul.hackathon.voicechatapp.model.Users;
import com.aliergul.hackathon.voicechatapp.util.FirebaseHelper;
import com.aliergul.hackathon.voicechatapp.util.MyUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterListProfile extends RecyclerView.Adapter {
    private static final int SHOW_HEADER_NOT_MESSAGE=0;
    private static final int SHOW_LINE_USER_PROFILE=1;
    private List<Users> liste;
    private Context mContext;
    private IClickListener clickListener;
    private String emptyMesssage;
    public AdapterListProfile(List<Users> liste,String emptyMesssage ,Context mContext) {
        this.liste = liste;
        this.mContext = mContext;
        this.emptyMesssage=emptyMesssage;
    }
    public void setListe(List<Users> list){
        this.liste=list;
        notifyDataSetChanged();

    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SHOW_LINE_USER_PROFILE){
            LineItemProfileBinding binding=LineItemProfileBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new LineProfileHolder(binding);
        }else{
            LineHeaderTextBinding binding=LineHeaderTextBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new LineHeaderTextHolder(binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position)==SHOW_LINE_USER_PROFILE){
            Users user=liste.get(position);
            LineProfileHolder h=(LineProfileHolder)holder;
            h.binding.count.setVisibility(View.GONE);
            if(emptyMesssage.equals(mContext.getString(R.string.emptyMessages))){
                inboxCount(h,user);
            }

            h.binding.tvEmail.setText(user.getUserEmail());
            h.binding.tvFullName.setText(user.getUserName());

            if(user.getUserPhoto().length()>10){
                h.binding.lineProfileProgresbar.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(user.getUserPhoto())
                        .resize(30, 30)
                        .centerCrop()
                        .into(h.binding.profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                h.binding.lineProfileProgresbar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }
            h.binding.btnMessage.setOnClickListener(v->{
                Intent i=new Intent(mContext, ActivityNewMessage.class);
                i.putExtra(MyUtil.USER_UID,user.getUserUID());
                i.putExtra(MyUtil.FULL_NAME,user.getUserName());
                mContext.startActivity(i);
            });
        }else if(getItemViewType(position)==SHOW_HEADER_NOT_MESSAGE){
            LineHeaderTextHolder header=(LineHeaderTextHolder) holder;
            header.binding.tvHeader.setText(emptyMesssage);
        }

    }

    private void inboxCount(LineProfileHolder h,Users user) {
        //Okunmamış mesaj Yazımı...

        Users actUSer= FirebaseHelper.getActiveUser();

        FirebaseDatabase.getInstance().getReference()
                .child(MyUtil.COLUMN_MESSAGES)
                .child(actUSer.getUserUID())
                .child(user.getUserUID())
                .child("count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    int count=snapshot.getValue(Integer.class);
                    if(count>0){
                        h.binding.count.setText(String.valueOf(count));
                        h.binding.count.setVisibility(View.VISIBLE);

                    }else{
                        h.binding.count.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(liste.size()==0 ||liste==null){
            return SHOW_HEADER_NOT_MESSAGE;
        }else{
            return SHOW_LINE_USER_PROFILE;
        }

    }

    @Override
    public int getItemCount() {
        if(liste.size()==0)
            return 1;
        else
        return liste.size();
    }

    public void updateList(List<Users> list) {
        this.liste=list;
        notifyDataSetChanged();
    }
    public void setEmptyMesssage(String emptyMesssage) {
        this.emptyMesssage=emptyMesssage;
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

    private class LineHeaderTextHolder extends RecyclerView.ViewHolder {
        private LineHeaderTextBinding binding;
        public LineHeaderTextHolder(LineHeaderTextBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
