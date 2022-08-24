package com.project.chatstream.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.chatstream.Adapter.UserAdapter;
import com.project.chatstream.ModelClass.Users;
import com.project.chatstream.R;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    ArrayList<Users> usersArrayList;
    ImageView img_logOut,img_setting;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
        img_logOut = findViewById(R.id.img_logOut);
        img_setting = findViewById(R.id.img_setting);

        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("user");
        usersArrayList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users = dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        if(auth.getCurrentUser()==null)
        {
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        }

            img_logOut.setOnClickListener(v ->
            {
                Dialog dialog = new Dialog(HomeActivity.this,R.style.Dialoge);
                dialog.setContentView(R.layout.dialog_layout);

                TextView yesBtn,noBtn;

                yesBtn = dialog.findViewById(R.id.yesBtn);
                noBtn = dialog.findViewById(R.id.noBtn);

                yesBtn.setOnClickListener(v1 ->
                {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                });

                noBtn.setOnClickListener(v12 -> dialog.dismiss());
                dialog.show();
            });

        img_setting.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this,SettingActivity.class)));

    }

    public void onBackPressed()
    {
        if(doubleBackToExitPressedOnce)
        {
            super.onBackPressed();
            return;
        }

        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
        doubleBackToExitPressedOnce = true;
    }

    }
