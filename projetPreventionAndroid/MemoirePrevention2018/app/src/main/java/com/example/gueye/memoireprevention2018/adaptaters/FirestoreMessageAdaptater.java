package com.example.gueye.memoireprevention2018.adaptaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.modele.Chat;
import com.example.gueye.memoireprevention2018.modele.UserId;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public  class FirestoreMessageAdaptater extends FirestoreRecyclerAdapter<Chat,FirestoreMessageAdaptater.MessageHolder> {

    private Context mContext;
    private String imageProfileDest;
    private List<Chat> chatLists;
    private Chat mChat;
    String currentUserId;
    String ivAuthor;

    private String userIdDest;
    private FirebaseAuth mFirebaseAuth;

    public String imageProfileCurUser ;


    public FirestoreMessageAdaptater(@NonNull FirestoreRecyclerOptions<Chat> options, Context mContext, String userIdDest) {
        super(options);

        this.mContext = mContext;
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserId = mFirebaseAuth.getCurrentUser().getUid();
        getImageProfileCurrentUser();

        this.userIdDest = userIdDest;
    }


    @Override
    protected void onBindViewHolder(@NonNull final MessageHolder holder, int position, @NonNull Chat model) {



        final String tvContentMessage = model.getMessage();

        Toast.makeText(mContext, "curreUseId "+ currentUserId+"   otherId user "+ userIdDest, Toast.LENGTH_SHORT).show();

        if(currentUserId.equals(model.getFrom())  &&
                model.getTo().equals(userIdDest)
                ||
                model.getTo().equals(currentUserId) &&

                        model.getFrom().equals(userIdDest)
                                                            ){

           chatLists.add(model);

            if (holder.getTypeOf() == Const.MSG_TYPE_RECEIVE) {

                ivAuthor = imageProfileDest;
                holder.setMessageData(tvContentMessage, ivAuthor);

            } else {


                if(imageProfileCurUser != null){

                    holder.setMessageData(tvContentMessage, ivAuthor);

                }
            }
        }



    }

    public void setImageProfileUserDest(String imageProfileDest){

        this.imageProfileDest = imageProfileDest;
    }

    private void getImageProfileCurrentUser(){

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful() && task.getResult() != null){

                    ivAuthor = task.getResult().getString("image");

                    imageProfileCurUser = ivAuthor;

                }

            }
        });


    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;


        if(viewType == Const.MSG_TYPE_RECEIVE){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_receive,parent,false);

        }else{

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_send,parent,false);
        }

        MessageHolder viewHolder = new MessageHolder (view);

        viewHolder.setTypeOf(viewType);

        return viewHolder;

    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private CircleImageView imageProfil;
        private TextView tvDate;

        private int typeOf = 0;

        public MessageHolder(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.tv_content_message);
            imageProfil = (CircleImageView)itemView.findViewById(R.id.civ_profile_image_author);
        }

        private void setMessageData(String username ,String imageAuthor){

            tvMessage.setText(username);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.send_alerte);

            Glide.with(mContext).applyDefaultRequestOptions(placeholderOption).load(imageAuthor).into(imageProfil);

        }

        public int getTypeOf() {
            return typeOf;
        }

        public void setTypeOf(int typeOf) {
            this.typeOf = typeOf;
        }
    }


}
