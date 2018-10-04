package com.example.gueye.memoireprevention2018.fragments;

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
import com.example.gueye.memoireprevention2018.adaptaters.AlerteRecyclerAdapter;
import com.example.gueye.memoireprevention2018.modele.BlogPost;
import com.example.gueye.memoireprevention2018.modele.Users;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public RecyclerView blog_list_view;
    private List<BlogPost> blog_list;
    private  List<Users> user_list;
    public FirebaseFirestore firebaseFirestore;
    public AlerteRecyclerAdapter blogRecyclerAdapter;
    public FirebaseAuth firebaseAuth;
    private ProgressBar loading_spinner;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_home, container, false);

        blog_list = new ArrayList<>();
        user_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.alerte_list_view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        loading_spinner = view.findViewById(R.id.loading_spinner);



        blogRecyclerAdapter = new AlerteRecyclerAdapter(blog_list, user_list,firebaseFirestore,firebaseAuth);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);

        if (firebaseAuth.getCurrentUser() != null) {

            loading_spinner.setVisibility(View.VISIBLE);

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING);
            firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if(documentSnapshots == null){
                        return;
                    }
                    if (!documentSnapshots.isEmpty()) {

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                final BlogPost   blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                                String blogUserId = doc.getDocument().getString("user_id");

                                firebaseFirestore.collection("Users").document(blogUserId).get().addOnCompleteListener(getActivity(),new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){

                                            loading_spinner.setVisibility(View.INVISIBLE);
                                            Users user = task.getResult().toObject(Users.class);
                                            user_list.add(user);
                                            blog_list.add(blogPost);
                                            blogRecyclerAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }

                    }else{
                        return;
                    }

                }
            });
        }

        // Inflate the layout for this fragment
        return view ;
    }



}