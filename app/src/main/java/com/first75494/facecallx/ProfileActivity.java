package com.first75494.facecallx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserId = "", receiverUserName = "",receiverUserImage = "";
    private ImageView backgroundImage;
    private TextView name;
    private Button cancel_friend_request,send_friend_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        receiverUserImage = getIntent().getExtras().get("profile_image").toString();
        receiverUserName = getIntent().getExtras().get("profile_name").toString();

        backgroundImage = findViewById(R.id.background_profile_image);
        name = findViewById(R.id.name_profile);
        cancel_friend_request = findViewById(R.id.cancel_friend_request);
        send_friend_request = findViewById(R.id.send_friend_request);

        Picasso.get().load(receiverUserImage).into(backgroundImage);
        name.setText(receiverUserName);
    }
}
