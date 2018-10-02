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
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.activities.DetailsNotificationActivity;
import com.example.gueye.memoireprevention2018.modele.Notifications;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AkshayeJH on 10/01/18.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{

    private List<Notifications> mNotifList;

    private FirebaseFirestore firebaseFirestore;
    private Context context;

    public NotificationsAdapter(Context context, List<Notifications> mNotifList) {

        this.mNotifList = mNotifList;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(parent.getContext()).inflate( R.layout.notification_item_layout, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        firebaseFirestore = FirebaseFirestore.getInstance();

        final String from_id = mNotifList.get(position).getFromNotif();
        final String notif_id = mNotifList.get(position).notificationId;

        final int postionType = mNotifList.get(position).getTypeNotif();

        //holder.mNotifDate.setText(mNotifList.get(position).getTime());

        // Put date post
        String timestamp = mNotifList.get( position ).getTime();

        String[] dateFormat = timestamp.split(" ");

        String time = dateFormat[1];

        String[] dateArray = dateFormat[0].split("-");

        String date = dateArray[0]+" "+dateArray[1].substring(0,3)+" "+dateArray[2];


        holder.setDate(date,time);


        holder.mNotifType.setText( Const.DEFAULT_TYPES[postionType]);


        firebaseFirestore.collection("Users").document(from_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String name = documentSnapshot.getString("name");
                String image = documentSnapshot.getString("image");

                holder.mNotifName.setText(name);

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder( R.drawable.profile);

                Glide.with(context).setDefaultRequestOptions(requestOptions).load(image).into(holder.mNotifImage);

            }
        });



        //clik on nitification
        holder.mView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore = FirebaseFirestore.getInstance();

                Map<String,Object> statusMap = new HashMap<>();
                statusMap.put("status", "1" );
                firebaseFirestore.collection("Notifications").document(notif_id).update(statusMap).addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( context, "Status changed", Toast.LENGTH_SHORT ).show();
                    }
                } );

                Intent sendIntent = new Intent( context, DetailsNotificationActivity.class );
                sendIntent.putExtra( "notif_id", notif_id );
                context.startActivity( sendIntent );


            }
        } );


    }

    @Override
    public int getItemCount() {
        return mNotifList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public CircleImageView mNotifImage;
        public TextView mNotifName;
        public TextView mNotifType;
        public TextView mNotifDate;
        private TextView mNotifTime;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mNotifImage = (CircleImageView) mView.findViewById( R.id.notif_image);
            mNotifType = (TextView) mView.findViewById( R.id.notif_type );
            mNotifName = (TextView) mView.findViewById( R.id.notif_name);
            mNotifDate = (TextView) mView.findViewById( R.id.tv_notif_date );


        }

        // set date
        public void setDate(String date,String time) {
            mNotifDate = mView.findViewById(R.id.tv_notif_date);
            mNotifTime = mView.findViewById(R.id.tv_notif_time);
            mNotifDate.setText(date);
            mNotifTime.setText("à "+time);

        }
    }

}
