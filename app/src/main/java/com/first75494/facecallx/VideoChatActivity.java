package com.first75494.facecallx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.opentok.android.OpentokError;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.Publisher;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity
        implements Session.SessionListener, PublisherKit.PublisherListener {

    private static String API_Key = "46813454";
    private static String SESSION_ID = "2_MX40NjgxMzQ1NH5-MTU5MzE2NzMyNzQ0Mn5NYjlZaXVTUVNrSk9QRGk4Ry8xdHpXWUJ-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjgxMzQ1NCZzaWc9MzUyYTM5OWIzYTllNzdhNzE4YzlmMzhhMjRkNjAzMWFlMmRiYjNjMjpzZXNzaW9uX2lkPTJfTVg0ME5qZ3hNelExTkg1LU1UVTVNekUyTnpNeU56UTBNbjVOWWpsWmFYVlRVVk5yU2s5UVJHazRSeTh4ZEhwWFdVSi1mZyZjcmVhdGVfdGltZT0xNTkzMTY3NDIxJm5vbmNlPTAuNzgxNzc5NzY2OTA1MTc0OSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTk1NzU5NDE3JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_TAG = VideoChatActivity.class.getSimpleName();
    private static final int RC_VIDEO_APP_PERM = 124;
    private DatabaseReference userRef;
    private String userId = "";
    private ImageView closeVideoChatBtn;
    private FrameLayout mPublisherViewController, mSubscriberViewController;
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        userRef = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        closeVideoChatBtn = findViewById(R.id.close_video_chat_btn);
        closeVideoChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               userRef.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if(dataSnapshot.child(userId).hasChild("Ringing")){
                           userRef.child(userId).child("Ringing").removeValue();

                           if(mPublisher != null){
                               mPublisher.destroy();
                           }
                           if (mSubscriber != null) {
                               mSubscriber.destroy();
                           }

                           startActivity(new Intent(VideoChatActivity.this,SignUpActivity.class));
                           finish();
                       }
                       if(dataSnapshot.child(userId).hasChild("Calling")){
                           userRef.child(userId).child("Calling").removeValue();

                           if(mPublisher != null){
                               mPublisher.destroy();
                           }
                           if (mSubscriber != null) {
                               mSubscriber.destroy();
                           }

                           startActivity(new Intent(VideoChatActivity.this,SignUpActivity.class));
                           finish();
                       }else{
                           startActivity(new Intent(VideoChatActivity.this,SignUpActivity.class));
                           finish();
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
            }
        });

        requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, VideoChatActivity.this);

    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions(){

        String[] perm = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

        if(EasyPermissions.hasPermissions(this,perm)){
            mPublisherViewController = findViewById(R.id.publisher_container);
            mSubscriberViewController = findViewById(R.id.subscriber_container);

            //1. initialize and connect to session
            mSession = new Session.Builder(this,API_Key,SESSION_ID).build();
            mSession.setSessionListener(VideoChatActivity.this);
            mSession.connect(TOKEN);
        }else{
            EasyPermissions.requestPermissions(this,"Please allow mic and camera permission",RC_VIDEO_APP_PERM);
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    //publishing a stream to the session
    @Override
    public void onConnected(Session session) {
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(VideoChatActivity.this);

        mPublisherViewController.addView(mPublisher.getView());

        if(mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {

    }


    // subscribing to the stream which has been published already
    @Override
    public void onStreamReceived(Session session, Stream stream) {

        if(mSubscriber == null){
            mSubscriber = new Subscriber.Builder(this,stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewController.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
       if(mSubscriber != null){
           mSubscriber = null;
           mSubscriberViewController.removeAllViews();
       }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
