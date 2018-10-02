package com.example.gueye.memoireprevention2018.adaptaters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.activities.DiscussionsActivity;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.http.POST;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class ListMembersAdaptater extends RecyclerView.Adapter<ListMembersAdaptater.ViewHolder> {

    private List<Users> users ;
    private Context mContext;

    private String messageSend ;
    private String Date ;
    FirebaseAuth mAuth;
    String currentUserId ;
    String imageProfileCurrentUser;

    private String classCalled ="null";

    public ListMembersAdaptater(List users , Context mcontext){

        this.users = users;
        this.mContext = mcontext;
    }

    public ListMembersAdaptater(List<Users> users, Context mContext, String classCalled) {
        this.users = users;
        this.mContext = mContext;
        this.classCalled = classCalled;
        mAuth = FirebaseAuth.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if(currentUserId != null)

        getImageProfileCurrentUser(currentUserId);
    }

    private void getImageProfileCurrentUser(String currentUserId) {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


        firebaseFirestore.collection("Users").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful() && task.getResult() != null){

                  imageProfileCurrentUser = task.getResult().getString("image");
                }

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view ;

         view = LayoutInflater.from(mContext).inflate(R.layout.list_members_item_layout,parent,false);

        if(classCalled.equals(Const.CHAT_FRAGMENT)){

            view = LayoutInflater.from(mContext).inflate(R.layout.list_discussions_item_layout,parent,false);

        }



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


      final String   username = users.get(position).getName();
      final String profileImage =users.get(position).getImage();

      holder.setUserData(username,profileImage);

      if(classCalled.equals(Const.CHAT_FRAGMENT)){


      }

      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              String userId = users.get(position).usersId;
              Log.d(TAG, "onClick: user Id"+ userId);
              String userNameDest = users.get(position).getName();

              Log.d(TAG, "onClick: username "+ userNameDest);
              String profilImageDest = users.get(position).getImage();

              Log.d(TAG, "onClick: profile image dest "+ profileImage);

              Intent discussionIntent =new Intent(mContext, DiscussionsActivity.class);

              discussionIntent.putExtra(Const.USER_ID_DEST,userId);
              discussionIntent.putExtra(Const.USER_PROFILE_DEST,profilImageDest);
              discussionIntent.putExtra(Const.USERNAME_DEST,userNameDest);

              if(imageProfileCurrentUser != null ){

                  discussionIntent.putExtra(Const.CURRENT_USER_IMAGE_PROFILE, imageProfileCurrentUser);
              }

              mContext.startActivity(discussionIntent);

          }
      });

    }

    public String getMessageSend() {
        return messageSend;
    }

    public void setMessageSend(String messageSend) {
        this.messageSend = messageSend;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername ;
        private CircleImageView civProfileImage;
        private TextView tvMessageUser;
        private TextView tvDateMessageSend;


        public ViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            civProfileImage = (CircleImageView) itemView.findViewById(R.id.iv_user);
        }

       public void setUserData(String username , String profileImage){

           tvUsername.setText(username);

           RequestOptions placeholderOption = new RequestOptions();
           placeholderOption.placeholder(R.drawable.send_alerte);

           Glide.with(mContext).applyDefaultRequestOptions(placeholderOption).load(profileImage).into(civProfileImage);

           if(classCalled.equals(Const.CHAT_FRAGMENT)){

               tvMessageUser = itemView.findViewById(R.id.tv_message_user);
               tvDateMessageSend = itemView.findViewById(R.id.tv_message_date_user);

               tvMessageUser.setText("Cliquer pour repondre ...");

           }
       }
    }
}
