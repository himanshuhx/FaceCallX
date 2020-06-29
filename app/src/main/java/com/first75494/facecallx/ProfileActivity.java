package com.first75494.facecallx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserId = "", receiverUserName = "",receiverUserImage = "";
    private ImageView backgroundImage;
    private TextView name;
    private Button cancel_friend_request,send_friend_request;
    private FirebaseAuth firebaseAuth;
    private String senderUserId, currentState = "new";
    private DatabaseReference friendRequestRef, contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        senderUserId = firebaseAuth.getCurrentUser().getUid();
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        receiverUserImage = getIntent().getExtras().get("profile_image").toString();
        receiverUserName = getIntent().getExtras().get("profile_name").toString();

        backgroundImage = findViewById(R.id.background_profile_image);
        name = findViewById(R.id.name_profile);
        cancel_friend_request = findViewById(R.id.cancel_friend_request);
        send_friend_request = findViewById(R.id.send_friend_request);

        Picasso.get().load(receiverUserImage).into(backgroundImage);
        name.setText(receiverUserName);

        manageClickEvents();
    }

    private void manageClickEvents() {

        friendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserId)){
                            String requestType = dataSnapshot.child(receiverUserId).child("request_type")
                                                                .getValue().toString();
                            if(requestType.equals("sent")){
                                currentState = "request_sent";
                                send_friend_request.setText("Cancel Friend Request");
                            }
                            else if(requestType.equals("received")){
                                currentState = "request_received";
                                send_friend_request.setText("Accept Friend Request");

                                cancel_friend_request.setVisibility(View.VISIBLE);
                                cancel_friend_request.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendRequest();
                                    }
                                });
                            }else{
                                contactsRef.child(senderUserId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.hasChild(receiverUserId)){
                                                    currentState = "friends";
                                                    send_friend_request.setText("Delete Contact");
                                                }else{
                                                    currentState = "new";
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if(senderUserId.equals(receiverUserId)){
            send_friend_request.setVisibility(View.GONE);
        }else{

            send_friend_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  if(currentState.equals("new")){
                      SendFriendRequest();
                  }
                  if(currentState.equals("request_sent")){
                      CancelFriendRequest();
                  }
                  if(currentState.equals("request_received")){
                      AcceptFriendRequest();
                  }
                  if(currentState.equals("request_sent")){
                      CancelFriendRequest();
                  }
                }
            });
        }
    }

    private void SendFriendRequest() {

        friendRequestRef.child(senderUserId).child(receiverUserId)
                           .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){
                          friendRequestRef.child(receiverUserId).child(senderUserId)
                                  .child("request_type").setValue("received")
                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                           if(task.isSuccessful()){
                                               currentState = "request_sent";
                                               send_friend_request.setText("Cancel Friend Request");
                                               Toast.makeText(ProfileActivity.this,"Request Sent",Toast.LENGTH_SHORT).show();
                                           }
                                      }
                                  });
                      }
                    }
                });
    }

    private void CancelFriendRequest() {
        friendRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                friendRequestRef.child(receiverUserId).child(senderUserId)
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    send_friend_request.setText("Add Friend");
                                                    currentState = "new";
                                                }
                                            }
                                        });
                            }
                    }
                });
    }

    private void AcceptFriendRequest() {
        contactsRef.child(senderUserId).child(receiverUserId)
                .child("Contact").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            contactsRef.child(receiverUserId).child(senderUserId)
                                    .child("Contact").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                friendRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    friendRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){

                                                                                        send_friend_request.setText("Delete Contact");
                                                                                        currentState = "friends";

                                                                                        cancel_friend_request.setVisibility(View.GONE);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
