package com.example.gueye.memoireprevention2018.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.BenevolAdapter;
import com.example.gueye.memoireprevention2018.modele.BlogPost;
import com.example.gueye.memoireprevention2018.modele.Users;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListBenevolActivity extends AppCompatActivity {

    private EditText mSearchField;
    private CircleImageView mBtnSearch;
    private RecyclerView mListBenevol;
    private android.support.v7.widget.Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DatabaseReference mUserDatabase;
    private List<Users> usersList;
    private BenevolAdapter mBenevolAdapter;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_list_benevol );

        //init();
    }



   /* @Override
    protected void onStart() {
        super.onStart();

        usersList.clear();
        String clee = mSearchField.getText().toString();
        com.google.firebase.firestore.Query query = mFirestore.collection("Users").orderBy("name");//.startAt(clee).endAt(clee);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(documentSnapshots == null){
                    return;
                }
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String userId = doc.getDocument().getId();
                        final Users  user = doc.getDocument().toObject(Users.class).withId(userId);
                        mFirestore.collection("Users").document(userId).collection("Benevol").document(userId).addSnapshotListener( new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                if(documentSnapshot == null){
                                    return;
                                }

                                if (documentSnapshot.exists()){
                                    usersList.add(user);
                                    mBenevolAdapter.notifyDataSetChanged();
                                }
                            }
                        } );

                    }
                }

            }
        });
    }*/

/*
    private void init(){

        mAuth = FirebaseAuth.getInstance();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        mUserId = preferences.getString("user_id", null);

        mSearchField = findViewById(R.id.search_field);
        mBtnSearch = findViewById(R.id.btn_search);
        mListBenevol = findViewById(R.id.result_recycle_view);
        mListBenevol.setHasFixedSize(true);
        mListBenevol.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = findViewById( R.id.benevol_toolbar );
        setSupportActionBar( mToolbar );
        getSupportActionBar().setTitle("Bénévoles" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        mFirestore =FirebaseFirestore.getInstance();
        mListBenevol = findViewById(R.id.result_recycle_view);

        usersList = new ArrayList<>();
        mBenevolAdapter = new BenevolAdapter(this, usersList);
        mListBenevol.setHasFixedSize(true);
        mListBenevol.setLayoutManager(new LinearLayoutManager(this));
        mListBenevol.setAdapter(mBenevolAdapter);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String searchText = mSearchField.getText().toString();
                //Toast.makeText( ListBenevolActivity.this, ""+searchText, Toast.LENGTH_SHORT ).show();
                search(searchText);
            }
        });

    }
*/




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ListBenevolActivity.this, MainActivity.class));
    }



/*
    public void search( String input){

        usersList.clear();

        input = mSearchField.getText().toString();
        Toast.makeText( this, ""+input, Toast.LENGTH_SHORT ).show();

        com.google.firebase.firestore.Query query = mFirestore.collection("Users");
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(documentSnapshots == null){
                    return;
                }
                for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        String userId = doc.getDocument().getId();
                        final Users  user = doc.getDocument().toObject(Users.class).withId(userId);
                        mFirestore.collection("Users").document(userId).collection("Benevol").document(userId).addSnapshotListener( new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                                if(documentSnapshot == null){
                                    return;
                                }

                                if (documentSnapshot.exists()){
                                    usersList.add(user);
                                    mBenevolAdapter.notifyDataSetChanged();
                                }
                            }
                        } );

                    }
                }

            }
        });

    }
*/




}
