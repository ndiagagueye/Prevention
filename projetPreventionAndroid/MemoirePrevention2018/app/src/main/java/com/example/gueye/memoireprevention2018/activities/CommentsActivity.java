package com.example.gueye.memoireprevention2018.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.CommentsRecyclerAdapter;
import com.example.gueye.memoireprevention2018.modele.Comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar commentToolbar;
    String saveCurrentDate, saveCurrentTime , currentTime;

    private EditText comment_field;
    private ImageView comment_post_btn;

    private RecyclerView comment_list;
    private CommentsRecyclerAdapter commentsRecyclerAdapter;
    private List<Comments> commentsList;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String blog_post_id;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        commentToolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(commentToolbar);
        getSupportActionBar().setTitle("Récentes commentaires");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("ShareIdUser", MODE_PRIVATE);
        current_user_id = preferences.getString("user_id", null);

        blog_post_id = getIntent().getStringExtra("blog_post_id");

        comment_field = findViewById(R.id.et_new_comment_usert);
        comment_post_btn = findViewById(R.id.et_new_comment_btn);
        comment_list = findViewById(R.id.comment_recycle_view);

        //RecyclerView Firebase List
        commentsList = new ArrayList<>();
        commentsRecyclerAdapter = new CommentsRecyclerAdapter(commentsList);
        comment_list.setHasFixedSize(true);
        comment_list.setLayoutManager(new LinearLayoutManager(this));
        comment_list.setAdapter(commentsRecyclerAdapter);


        firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments").orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(CommentsActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {
                                    String commentId = doc.getDocument().getId();
                                    Comments comments = doc.getDocument().toObject(Comments.class).withId(commentId);
                                    commentsList.add(comments);
                                    commentsRecyclerAdapter.notifyDataSetChanged();


                                }
                            }

                        }

                    }
                });

        // Btn Comment post

        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_message = comment_field.getText().toString();

                // Store comment

                saveCommentInFirestore(comment_message,current_user_id);
            }
        });


    }
    private String currentTime(){
        // Get Current Date //
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        // Get Current Time //
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm");
        saveCurrentTime = time.format(calForTime.getTime());
        // Get random Name //
        currentTime = saveCurrentDate+" "+saveCurrentTime;
        return currentTime;
    }

    private void saveCommentInFirestore(String comment, String user_id) {

        if (!TextUtils.isEmpty(comment)) {

           String date = currentTime();

            Map<String, Object> commentsMap = new HashMap<>();
            commentsMap.put("message", comment);
            commentsMap.put("user_id", user_id);
            commentsMap.put("timestamp", currentTime);

            firebaseFirestore.collection("Posts/" + blog_post_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {

                    if (!task.isSuccessful()) {

                        Toast.makeText(CommentsActivity.this, "Error Posting Comment : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    } else {

                        comment_field.setText("");

                    }

                }
            });

        }else{
            Toast.makeText(CommentsActivity.this, "Veuillez saisir un commentaire!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity( new Intent( CommentsActivity.this , MainActivity.class) );
    }
}
