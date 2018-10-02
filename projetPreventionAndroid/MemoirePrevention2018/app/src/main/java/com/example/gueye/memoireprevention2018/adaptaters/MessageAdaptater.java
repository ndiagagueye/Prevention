package com.example.gueye.memoireprevention2018.adaptaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.modele.Chat;
import com.example.gueye.memoireprevention2018.modele.ChatMessage;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdaptater extends RecyclerView.Adapter<MessageAdaptater.MyViewHolder> {

    private List<ChatMessage> chatsList ;
    private Context mContext;
    private String imageProfile;


    String currentUserId;

    String ivAuthor;

    private FirebaseAuth mFirebaseAuth;


    public MessageAdaptater(List <ChatMessage> chatsList, Context mContext,String imageProfile) {
        this.chatsList = chatsList;
        this.mContext = mContext;
        this.imageProfile = imageProfile;

        mFirebaseAuth = FirebaseAuth.getInstance();

         currentUserId = mFirebaseAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;


        if(viewType == Const.MSG_TYPE_RECEIVE){

            view = LayoutInflater.from(mContext).inflate(R.layout.message_receive,parent,false);

        }else{

            view = LayoutInflater.from(mContext).inflate(R.layout.message_send,parent,false);
        }

        MyViewHolder viewHolder = new MyViewHolder(view);

        viewHolder.setTypeOf(viewType);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final String tvContentMessage = chatsList.get(position).getMessage();

        holder.tvMessage.setText(tvContentMessage);
        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.send_alerte);
        if (holder.imageProfil != null){
            Glide.with(mContext).applyDefaultRequestOptions(placeholderOption).load(imageProfile).into(holder.imageProfil);

        }


    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvMessage;
        private CircleImageView imageProfil;
        private TextView tvDate;

        private int typeOf = 0;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.tv_content_message);
            imageProfil = (CircleImageView)itemView.findViewById(R.id.civ_profile_image_author);
        }



        public int getTypeOf() {
            return typeOf;
        }

        public void setTypeOf(int typeOf) {
            this.typeOf = typeOf;
        }
    }

    @Override
    public int getItemViewType(int position) {


        if(!currentUserId.equals(chatsList.get(position).getSender())){

            return Const.MSG_TYPE_RECEIVE;

        }else{

            return Const.MSG_TYPE_SEND;
        }




    }
}
