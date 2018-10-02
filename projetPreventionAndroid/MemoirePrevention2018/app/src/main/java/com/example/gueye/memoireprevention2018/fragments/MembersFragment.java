package com.example.gueye.memoireprevention2018.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.ListMembersAdaptater;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.facebook.login.widget.ProfilePictureView.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MembersFragment extends Fragment {

    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
    private RecyclerView mRecyclerView;
    private ListMembersAdaptater listMembersAdaptater;
    private ArrayList userList;


    public MembersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view  = inflater.inflate(R.layout.fragment_members, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.rv_members_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        userList =new ArrayList();

        listMembersAdaptater =new ListMembersAdaptater(userList, getContext());
        mRecyclerView.setAdapter(listMembersAdaptater);

       getUsersList();
        return view;
    }

    private void getUsersList() {

        Toast.makeText(getContext(), "getting user liste", Toast.LENGTH_SHORT).show();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots == null) return;

                userList.clear();

                for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {

                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        Toast.makeText(getContext(), "User added ", Toast.LENGTH_SHORT).show();

                        String userId = documentChange.getDocument().getId();

                        Log.d(TAG, "onEvent: userId "+ userId);

                        Users users = documentChange.getDocument().toObject(Users.class).withId(userId);

                        if (!currentUser.getUid().equals(userId)) {

                            Toast.makeText(getContext() , "", Toast.LENGTH_SHORT).show();

                            userList.add(users);

                            listMembersAdaptater.notifyDataSetChanged();
                        }


                    }

                    Toast.makeText(getContext(), "Nombre user "+ userList.size(), Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

}
