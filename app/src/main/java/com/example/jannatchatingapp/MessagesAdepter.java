package com.example.jannatchatingapp;

import static com.example.jannatchatingapp.ChatActivity.rImage;
import static com.example.jannatchatingapp.ChatActivity.sImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdepter extends RecyclerView.Adapter {
    Context context;
    ArrayList <Messages> messagesArrayList;
    int ITEM_SEND =1;
    int ITEM_Receive = 2;

    public MessagesAdepter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item,parent,false);

            return new SenderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout_item,parent,false);

            return new RecieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages = messagesArrayList.get(position);

        if (holder.getClass()== SenderViewHolder.class){
            SenderViewHolder viewHolder =(SenderViewHolder) holder;
       viewHolder.txtmessage.setText(messages.getMessage());
            Picasso.get().load(sImage).into(viewHolder.circleImageView);

        }else {
            RecieverViewHolder viewHolder =(RecieverViewHolder) holder;
            viewHolder.txtmessage.setText(messages.getMessage());
            Picasso.get().load(rImage).into(viewHolder.circleImageView);

        }
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())){
        return  ITEM_SEND ;
    }else {
        return  ITEM_Receive ;
    }

    }

    class SenderViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView txtmessage;

       public SenderViewHolder(@NonNull View itemView) {
           super(itemView);

        circleImageView = itemView.findViewById(R.id.profile_image);
        txtmessage = itemView.findViewById(R.id.texMessages);

       }
   }

    class RecieverViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView txtmessage;

        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profile_image);
            txtmessage = itemView.findViewById(R.id.texMessages);

        }
    }
}
