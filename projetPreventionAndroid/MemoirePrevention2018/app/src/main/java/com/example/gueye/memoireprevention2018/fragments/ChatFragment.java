package com.example.gueye.memoireprevention2018.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.activities.DiscussionsActivity;
import com.example.gueye.memoireprevention2018.adaptaters.ListMembersAdaptater;
import com.example.gueye.memoireprevention2018.modele.Chat;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView rvChatApp;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private List<String> userIdList ;
    private List mUsers;

    public static final String TAG = "Chat Fragment ";

    private String currentUserId ;
    private ListMembersAdaptater listMembersAdaptater;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        currentUserId = mFirebaseAuth.getCurrentUser().getUid();

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        rvChatApp = (RecyclerView) view.findViewById(R.id.rv_chat_app);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(false);
        rvChatApp.setLayoutManager(linearLayoutManager);

        showRecenteDiscussion();



        return view;
    }

    private void showRecenteDiscussion() {

        userIdList = new ArrayList<>();


        final boolean[] isAdded = {false};

        mFirestore.collection("Chats").orderBy("date") .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots == null) return;

                for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {



                    Log.d(TAG, "onEvent: current user id is "+currentUserId);
                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                         Chat   chat   =documentChange.getDocument().toObject(Chat.class);

                        if(chat.getTo().equals(currentUserId) ){

                            Log.d(TAG, "onEvent: current user id a reçu un message ");

                            if( userIdList.size() != 0){

                                for(String id : userIdList ){

                                    if (chat.getFrom().equals(id)){
                                        isAdded[0] = true ;
                                        Log.d(TAG, "onEvent: je vais faire un break ");
                                        break;
                                    }
                                }

                               if(!isAdded[0]){

                                    userIdList.add(chat.getFrom());
                                   isAdded[0] =false;
                               }

                            }else{

                                userIdList.add(chat.getFrom());
                            }
                        }


                        if (chat.getFrom().equals(currentUserId)){

                            Log.d(TAG, "onEvent: current user id a envoyé un message ");

                            if( userIdList.size() != 0){

                                for(String id : userIdList ){


                                    if (chat.getTo().equals(id)) {
                                        isAdded [0] =true ;
                                        Log.d(TAG, "onEvent: je vais faire un return ");
                                        break;
                                    }
                                }

                                if(!isAdded[0]){

                                    userIdList.add(chat.getTo() );

                                    isAdded[0] =false;
                                }


                                Log.d(TAG, "onEvent:  j'ai exécuté cette instruction ");

                            }else{

                                userIdList.add(chat.getTo());
                            }

                        }
                    }
                }

                Log.d(TAG, "onEvent: number of discussion of current user id is "+ userIdList.size());

                //recupérations des utilisateur ayant chaté avec currentUser id

                if (userIdList.size() != 0){

                    showUserDiscussion(userIdList);
                }else {

                    Toast.makeText(getContext(), "Vous n'avez pas encore de discussion ", Toast.LENGTH_SHORT).show();
                }
             }

    });
    }

    private void showUserDiscussion(final List<String> userIdList) {
        mUsers = new ArrayList<>();

         listMembersAdaptater = new ListMembersAdaptater(mUsers,getContext());
        mFirestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {


                if (documentSnapshots == null) return;

                for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {

                    if (documentChange.getType() == DocumentChange.Type.ADDED) {



                        String userId = documentChange.getDocument().getId();

                        Log.d(TAG, "onEvent: userId "+ userId);

                        Users users = documentChange.getDocument().toObject(Users.class).withId(userId);

                        for(String id : userIdList ){

                            if(userId.equals(id)) {

                                mUsers.add(users);
                                break;
                            }

                        }

                        listMembersAdaptater.notifyDataSetChanged();

                    }
                }



                Log.d(TAG, "onEvent: nombre d'utilisateurs obtenus est "+ mUsers.size());

                rvChatApp.setAdapter(listMembersAdaptater);

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
