package com.first75494.facecallx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CallActivity extends AppCompatActivity {
    private TextView nameContact;
    private ImageView profileImage, cancelCallBtn, makeCallBtn;

    private String receiverUserId = "", receiverUserImage = "", receiverUserName = "";
    private String senderUserId = "", senderUserImage = "", senderUserName = "";
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        senderUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        nameContact = findViewById(R.id.name_calling);
        profileImage = findViewById(R.id.profile_image_call);
        cancelCallBtn = findViewById(R.id.cancel_call);
        makeCallBtn = findViewById(R.id.make_call);

        getAndSetUserProfileInfo();
    }

    private void getAndSetUserProfileInfo() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.child(receiverUserId).exists()){
                   receiverUserImage = dataSnapshot.child(receiverUserId).child("image").getValue().toString();
                   receiverUserName = dataSnapshot.child(receiverUserId).child("name").getValue().toString();

                   nameContact.setText(receiverUserName);
                   Picasso.get().load(receiverUserImage).placeholder(R.drawable.profile_image).into(profileImage);
               }
               if(dataSnapshot.child(senderUserId).exists()){
                   senderUserImage = dataSnapshot.child( senderUserId).child("image").getValue().toString();
                   senderUserName = dataSnapshot.child( senderUserId).child("name").getValue().toString();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

      userRef.child(receiverUserId)
              .addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      if(!dataSnapshot.hasChild("Calling") && !dataSnapshot.hasChild("Ringing")){
                          final HashMap<String,Object> callingInfo = new HashMap<>();
                          callingInfo.put("calling",receiverUserId);

                          userRef.child(senderUserId).child("Calling")
                                  .updateChildren(callingInfo)
                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                         if(task.isSuccessful()){
                                             final HashMap<String,Object> ringingInfo = new HashMap<>();
                                             ringingInfo.put("ringing",senderUserId);

                                             userRef.child(receiverUserId)
                                                     .child("Ringing")
                                                     .updateChildren(ringingInfo);
                                         }
                                      }
                                  });
                      }
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError databaseError) {

                  }
              });

    }
}
