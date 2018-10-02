package com.example.gueye.memoireprevention2018.adaptaters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.modele.Comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


/**
 * Created by gueye on 14/08/18.
 */

public class CommentsRecyclerAdapter extends RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder> {

    public List<Comments> commentsList;
    public Context context;
    public FirebaseFirestore firebaseFirestore;
    public FirebaseAuth firebaseAuth;

    public CommentsRecyclerAdapter(List<Comments> commentsList) {

        this.commentsList = commentsList;

    }

    @Override
    public CommentsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item_layout, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        return new CommentsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsRecyclerAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        // SET COMMENT
        String commentId =  commentsList.get(position).commentId;

        String commentMessage = commentsList.get(position).getMessage();
        holder.setCommentMessage(commentMessage);

        //String date = commentsList.get(position).getTimestamp();
        //holder.setDate(date);

        // Put date post
        String timestamp = commentsList.get( position ).getTimestamp();

        String[] dateFormat = timestamp.split(" ");

        String time = dateFormat[1];

        String[] dateArray = dateFormat[0].split("-");

        String date = dateArray[0]+" "+dateArray[1].substring(0,3)+" "+dateArray[2];


        holder.setDate(date,time);


        //SET IMAGE AND USERNAME
         String user_id = commentsList.get(position).getUser_id();
        // User Data will be retrieved

        // On recupere le nom d'utilisateur dont l'id est egal a user_id
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    // get username and profile image
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.setuserData(userName,userImage);
                }else{
                    // Firebase Exception
                }
            }
        });

    }


    @Override
    public int getItemCount() {

        if (commentsList != null) {

            return commentsList.size();

        } else {

            return 0;

        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView comment_message;
        private TextView commentUserName;
        private ImageView commentUserImage;
        private TextView commentDate;
        private TextView tv_comment_time;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCommentMessage(String message) {
            comment_message = mView.findViewById(R.id.tv_comment_user);
            comment_message.setText(message);

        }

        // set usermane and image profile

        public  void setuserData(String name, String image){
            commentUserName = mView.findViewById(R.id.tv_comment_username);
            commentUserImage = mView.findViewById( R.id.iv_comment_user);
            commentUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.send_alerte);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(commentUserImage);
        }

        // set date
        public void setDate(String date,String time) {
            commentDate = mView.findViewById(R.id.tv_comment_date_user);
            tv_comment_time = mView.findViewById(R.id.tv_comment_time);
            commentDate.setText(date);
            tv_comment_time.setText("à "+time);

        }

    }

}