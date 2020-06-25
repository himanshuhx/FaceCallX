package com.first75494.facecallx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private EditText userName, userBio;
    private ImageView profileImage;
    private Button update;
    private static  int GalleryPick = 1;
    private Uri imageUri;
    private StorageReference userProfileImageRef;
    private String downloadUrl;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        userName = findViewById(R.id.username_settings);
        userBio = findViewById(R.id.bio_settings);
        profileImage = findViewById(R.id.profile_image);
        update = findViewById(R.id.save_settings_btn);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GalleryPick);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        retrieveUserInfo();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data!=null){
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }


    private void saveUserData() {
        final String getUsername = userName.getText().toString();
        final String getUserStatus = userBio.getText().toString();

        if(imageUri == null){

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("image")){
                        saveInfoOnlyWithoutImage();
                    }else{

                        Toast.makeText(SettingsActivity.this,"please select a profile image..",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else if(getUsername.equals("")){
            Toast.makeText(this,"Username is mandatory",Toast.LENGTH_SHORT).show();
        }else if(getUserStatus.equals("")){
            Toast.makeText(this,"UserStatus is mandatory",Toast.LENGTH_SHORT).show();
        }else{
            final StorageReference filePath = userProfileImageRef.child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid());

            final UploadTask uploadTask = filePath.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    downloadUrl = filePath.getDownloadUrl().toString();
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete (@NonNull Task < Uri > task) {
                        if (task.isSuccessful()) {
                            downloadUrl = task.getResult().toString();

                            HashMap<String, Object> profilemap = new HashMap<>();
                            profilemap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            profilemap.put("name", getUsername);
                            profilemap.put("status", getUserStatus);
                            profilemap.put("image", downloadUrl);

                            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .updateChildren(profilemap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Intent intent = new Intent(SettingsActivity.this, ContactsActivity.class);
                                                startActivity(intent);
                                                finish();

                                                Toast.makeText(SettingsActivity.this,
                                                        "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                 });
        }
    }

    private void saveInfoOnlyWithoutImage() {
        final String getUsername = userName.getText().toString();
        final String getUserStatus = userBio.getText().toString();

        if(getUsername.equals("")){

            Toast.makeText(this,"Username is mandatory",Toast.LENGTH_SHORT).show();

        }else if(getUserStatus.equals("")){

            Toast.makeText(this,"UserStatus is mandatory",Toast.LENGTH_SHORT).show();

        }else{

            final StorageReference filePath = userProfileImageRef.child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid());

            HashMap<String,Object> profilemap = new HashMap<>();
            profilemap.put("uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            profilemap.put("name",getUsername);
            profilemap.put("status",getUserStatus);

            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(profilemap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                                Intent intent = new Intent(SettingsActivity.this,ContactsActivity.class);
                                startActivity(intent);
                                finish();

                                Toast.makeText(SettingsActivity.this,
                                        "Profile updated successfully",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });}
    }

    private void retrieveUserInfo(){
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String image = dataSnapshot.child("image").getValue().toString();
                            String name = dataSnapshot.child("name").getValue().toString();
                            String bio = dataSnapshot.child("status").getValue().toString();

                            userName.setText(name);
                            userBio.setText(bio);
                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
