package com.example.gueye.memoireprevention2018.adaptaters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.activities.MainActivity;
import com.example.gueye.memoireprevention2018.activities.ProfileBenevolActivity;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by gueye on 06/09/18.
 */

public class BenevolAdapter extends RecyclerView.Adapter<BenevolAdapter.ViewHolder> {
    private List<Users> usersList;
    private Context context;
    public FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    public BenevolAdapter(Context context, List<Users> usersList) {
        this.usersList = usersList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.list_benevol_layout, parent, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
         final  Intent benevolIntent = new Intent(context, ProfileBenevolActivity.class);

        final String user_name = usersList.get(position).getName();
        final String token_id = usersList.get( position ).getToken_id();
        final  String image = usersList.get(position).getImage() ;
        final String telephone = usersList.get( position ).getTelephone();

        holder.user_name.setText(user_name);

        CircleImageView user_image_view = holder.user_image;
        Glide.with(context).load(image).into(user_image_view);

        final String user_id = usersList.get(position).usersId;
        //holder.setTypeUser(user_id);
        firebaseFirestore.collection("Users").document(user_id).collection("Benevol").document(user_id).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long item = documentSnapshot.getLong("benevol");
                holder.user_status.setText( Const.DEFAULT_TYPES_USER[(int) item]);
                benevolIntent.putExtra("type", Const.DEFAULT_TYPES_USER[(int) item]);

            }
        } );
        //clic on user
        holder.mView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                benevolIntent.putExtra("image", image);
                benevolIntent.putExtra("username", user_name);
                benevolIntent.putExtra("telephone", telephone);
                benevolIntent.putExtra("token_id", token_id);
                context.startActivity(benevolIntent);
            }
        } );

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        TextView user_name ;
        TextView user_status ;
        CircleImageView user_image ;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
             user_name =  mView.findViewById(R.id.name_text);
             user_status = mView.findViewById(R.id.status_text);
             user_image =  mView.findViewById(R.id.profile_image);

        }

        private void setTypeUser(String user_id){


        }
    }
}
