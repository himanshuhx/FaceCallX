package com.first75494.facecallx;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsActivity extends AppCompatActivity {

    BottomNavigationView navView;
    RecyclerView contactsList;
    ImageView findPeopleBtn;
    private DatabaseReference  contactsRef,userRef;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private String userName = "", profileImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        findPeopleBtn = findViewById(R.id.find_people_btn);
        contactsList = findViewById(R.id.contacts_list);

        contactsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        findPeopleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ContactsActivity.this, FindPeopleActivity.class);
                startActivity(mainIntent);
            }
        });

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
             new BottomNavigationView.OnNavigationItemSelectedListener() {
                 @Override
                 public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                     switch (item.getItemId()){

                         case R.id.navigation_home:
                             Intent mainIntent = new Intent(ContactsActivity.this, ContactsActivity.class);
                             startActivity(mainIntent);
                             break;
                         case R.id.navigation_settings:
                             Intent settingsIntent = new Intent(ContactsActivity.this,SettingsActivity.class);
                             startActivity(settingsIntent);
                             break;
                         case R.id.navigation_notifications:
                             Intent notificationsIntent = new Intent(ContactsActivity.this,NotificationsActivity.class);
                             startActivity(notificationsIntent);
                             break;
                         case R.id.navigation_logout:
                             FirebaseAuth.getInstance().signOut();
                             Intent logoutIntent = new Intent(ContactsActivity.this,SignUpActivity.class);
                             startActivity(logoutIntent);
                             finish();
                             break;
                     }
                     return false;
                 }
             };

    @Override
    protected void onStart() {
        super.onStart();

       // validateUser();

        FirebaseRecyclerOptions<Contacts> options =
               new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef.child(currentUserId), Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {
                final String listUserId = getRef(position).getKey();

                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            userName = dataSnapshot.child("name").getValue().toString();
                            profileImage = dataSnapshot.child("image").getValue().toString();

                            holder.userNameText.setText(userName);
                            Picasso.get().load(profileImage).into(holder.profileImageView);
                        }

                        holder.callBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ContactsActivity.this,CallActivity.class);
                                intent.putExtra("visit_user_id",listUserId);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_design,parent,false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return  viewHolder;
            }
        };
      contactsList.setAdapter(firebaseRecyclerAdapter);
      firebaseRecyclerAdapter.startListening();
    }


    public static class ContactsViewHolder extends RecyclerView.ViewHolder {

        TextView userNameText;
        Button  callBtn;
        ImageView profileImageView;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameText =itemView.findViewById(R.id.name_contact);
            callBtn = itemView.findViewById(R.id.call_btn);
            profileImageView = itemView.findViewById(R.id.image_contact);

        }
    }

    private void validateUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.exists()){
                        Intent intent = new Intent(ContactsActivity.this,SettingsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
