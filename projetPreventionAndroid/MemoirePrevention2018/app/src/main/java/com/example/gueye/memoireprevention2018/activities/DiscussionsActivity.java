package com.example.gueye.memoireprevention2018.activities;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gueye.memoireprevention2018.R;
import com.example.gueye.memoireprevention2018.adaptaters.FirestoreMessageAdaptater;
import com.example.gueye.memoireprevention2018.adaptaters.MessageAdaptater;
import com.example.gueye.memoireprevention2018.modele.Chat;
import com.example.gueye.memoireprevention2018.modele.ChatMessage;
import com.example.gueye.memoireprevention2018.utils.Const;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class DiscussionsActivity extends AppCompatActivity {

    private Toolbar tbDiscussion;
    private CircleImageView civProfileImageDest;
    private TextView tvUsernameDest;
    private RecyclerView rvDiscussion;

    private EditText etMsg;
    private ImageView ivSendMsg;

    private List mChat   = new ArrayList<>(); ;

    private String userIdDest;
    private String usernameDest;
    private String profileImageDest;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mFirebaseAuth;

    private MessageAdaptater messageAdaptater;

    public FirestoreMessageAdaptater firestoreMessageAdaptater;

    private String  currentUserId;

    private FirebaseDatabase firebaseDatabase ;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussions);

        tbDiscussion = (Toolbar)findViewById(R.id.tb_discussion);
        tbDiscussion.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        } );
        setSupportActionBar(tbDiscussion);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        if(mFirebaseAuth.getCurrentUser() != null){

            currentUserId  = mFirebaseAuth.getCurrentUser().getUid();
        }else {

            Toast.makeText(this, "current user null", Toast.LENGTH_SHORT).show();
        }


        usernameDest = getIntent().getStringExtra(Const.USERNAME_DEST);
        profileImageDest = getIntent().getStringExtra(Const.USER_PROFILE_DEST);
        userIdDest = getIntent().getStringExtra(Const.USER_ID_DEST);
        //profileImageCurrentUser = getIntent().getStringExtra(Const.CURRENT_USER_IMAGE_PROFILE);


        tbDiscussion.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        civProfileImageDest = (CircleImageView)findViewById(R.id.civ_profile_dest_discussion);
        tvUsernameDest = (TextView) findViewById(R.id.tv_username_dest_discussion);
        rvDiscussion = (RecyclerView)findViewById(R.id.rv_discussion);


        
      setupRecyclevView();

        etMsg = (EditText)findViewById(R.id.et_msg_discussion);
        ivSendMsg = (ImageView)findViewById(R.id.iv_send_message);



      getUserDestInformation();


      ivSendMsg.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              String message = etMsg.getText().toString();

              if(!TextUtils.isEmpty(message)){

                 sendMessageUser(currentUserId, userIdDest,message);


              }else{

                  Toast.makeText(DiscussionsActivity.this, "You can't send empty message ", Toast.LENGTH_SHORT).show();
              }

              etMsg.setText("");

          }
      });



    }

    private void sendMessageUser(String currentUserId, String userIdDest, String message) {

        HashMap msgHashMap = new HashMap();

        msgHashMap.put("sender", currentUserId);
        msgHashMap.put("receiver",userIdDest);
        msgHashMap.put("message",message);

        DatabaseReference databaseReference = firebaseDatabase.getReference();

        databaseReference.child("Chats").push().setValue(msgHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DiscussionsActivity.this, "message send ", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupRecyclevView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rvDiscussion.setLayoutManager(linearLayoutManager);




    }


    private void readMessages(final String currentUserId , final String userIdDest , final String profileImageDest){

        mChat.clear();

      DatabaseReference  databaseReference = firebaseDatabase.getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);

                    if(chatMessage.getReceiver() != null && chatMessage.getSender()!=null){

                        if(chatMessage.getReceiver().equals(currentUserId) && chatMessage.getSender().equals(userIdDest) ||
                                chatMessage.getSender().equals(currentUserId) && chatMessage.getReceiver().equals(userIdDest)){

                            mChat.add(chatMessage);
                        }

                        messageAdaptater = new MessageAdaptater(mChat, DiscussionsActivity.this,profileImageDest);
                        rvDiscussion.setAdapter(messageAdaptater);

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




    private void readMessages() {

        firebaseFirestore.collection("Chats").orderBy("date").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (documentSnapshots == null) return;

                mChat.clear();

                for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {

                    if (documentChange.getType() == DocumentChange.Type.ADDED) {

                        Chat chat =documentChange.getDocument().toObject(Chat.class);

                        Log.d(TAG, "onEvent: chat "+ chat);

                                if(mFirebaseAuth.getCurrentUser() != null){


                                    currentUserId = mFirebaseAuth.getInstance().getCurrentUser().getUid();
                                }

                        Log.d(TAG, "onEvent: currentuser id is "+ currentUserId);
                                if(userIdDest == null){

                                    Log.d(TAG, "onEvent: userId is null");

                                }

                        Log.d(TAG, "onEvent: chat.getFrom() "+ chat.getFrom());

                                if(chat.getFrom()!= null && chat.getTo() != null){

                                    if(chat.getFrom().equals(currentUserId) &&

                                            chat.getTo().equals(userIdDest) ||

                                            chat.getFrom().equals(userIdDest) &&
                                                    chat.getTo().equals(currentUserId)) {

                                        mChat.add(chat);
                                        messageAdaptater.notifyDataSetChanged();


                                     }

                                    messageAdaptater = new MessageAdaptater(mChat, DiscussionsActivity.this,profileImageDest);
                                    rvDiscussion.setAdapter(messageAdaptater);
                                }

                        }

                    }


            }
        });

    }

    private void sendMessageUser() {

    final String message = etMsg.getText().toString();

        if(!TextUtils.isEmpty(message)){

            HashMap msgHashMap = new HashMap();

            msgHashMap.put("from", currentUserId);
            msgHashMap.put("to",userIdDest);
            msgHashMap.put("message",message);
            msgHashMap.put("date", FieldValue.serverTimestamp());



            firebaseFirestore.collection("Chats").document().set(msgHashMap);




        }else{

            Toast.makeText(this, "Please enter un message !!!!", Toast.LENGTH_SHORT).show();
        }



    }

    private void getUserDestInformation() {

        Toast.makeText(this, "username "+ usernameDest, Toast.LENGTH_SHORT).show();

        tvUsernameDest.setText(usernameDest);
        RequestOptions placeholderOption = new RequestOptions();
        placeholderOption.placeholder(R.drawable.profile);

        Glide.with(DiscussionsActivity.this).applyDefaultRequestOptions(placeholderOption).load(profileImageDest).into(civProfileImageDest);
      readMessages (currentUserId,userIdDest,profileImageDest);

    }

    private void getMessageChats() {

        Query query = firebaseFirestore
                .collection("Chats")
                .orderBy("date");

        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query,Chat.class)
                .build();

         firestoreMessageAdaptater = new FirestoreMessageAdaptater(options,getApplicationContext(),userIdDest);
        firestoreMessageAdaptater.setImageProfileUserDest(profileImageDest);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rvDiscussion.setHasFixedSize(true);
        rvDiscussion.setLayoutManager(linearLayoutManager);
        rvDiscussion.setAdapter(firestoreMessageAdaptater);


    }


}
