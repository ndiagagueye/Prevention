package com.example.gueye.memoireprevention2018.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.NotificationsAdapter;

import com.example.gueye.memoireprevention2018.modele.Notifications;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private RecyclerView mNotificationList;
    private NotificationsAdapter notificationsAdapter;

    private List<Notifications> mNotifList;
    private FirebaseFirestore mFirestore;
    private ProgressBar loading_spinner_notif;
    private boolean isNotif = true;



    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate( R.layout.fragment_notification, container, false);


        mNotifList = new ArrayList<>();

        mNotificationList = (RecyclerView) view.findViewById(R.id.notification_list);
        notificationsAdapter = new NotificationsAdapter(getContext(), mNotifList);
        loading_spinner_notif = view.findViewById(R.id.loading_spinner_notif);
        loading_spinner_notif.setVisibility( View.VISIBLE );

        mNotificationList.setHasFixedSize(true);
        mNotificationList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mNotificationList.setAdapter(notificationsAdapter);

        mFirestore = FirebaseFirestore.getInstance();

        SharedPreferences preferences = getContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        final String current_user_id = preferences.getString("user_id", null);

        Query firstQuery = mFirestore.collection("Notifications").orderBy("date", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(documentSnapshots == null){
                    isNotif = false;
                    return;
                }
                isNotif = true;

                for(DocumentChange doc: documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {
                        String notif_id = doc.getDocument().getId();

                        Notifications notifications = doc.getDocument().toObject(Notifications.class).withId(notif_id);

                        if (!current_user_id.equals(notifications.getFromNotif())){
                            mNotifList.add( notifications );
                            notificationsAdapter.notifyDataSetChanged();

                        }

                    }

                }

            }
        });
        loading_spinner_notif.setVisibility(View.INVISIBLE);
        if (isNotif == false){
            return   inflater.inflate( R.layout.no_notfication, container, false);
        }
        return view;
    }

}
