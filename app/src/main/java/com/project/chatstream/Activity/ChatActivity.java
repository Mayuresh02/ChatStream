package com.project.chatstream.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.chatstream.Adapter.MessagesAdapter;
import com.project.chatstream.ModelClass.Messages;
import com.project.chatstream.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String receiverImage,receiverUID,receiverName,senderUID;
    CircleImageView profile_image;
    TextView receiver_name;
    public static String sImage;
    public static String rImage;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;
    CardView sendBtn;
    EditText editMessage;
    String senderRoom,receiverRoom;
    RecyclerView messageAdapter;
    ArrayList<Messages> messagesArrayList;
    MessagesAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        receiverName = getIntent().getStringExtra("name");
        receiverImage = getIntent().getStringExtra("ReceiverImage");
        receiverUID = getIntent().getStringExtra("uid");

        messagesArrayList = new ArrayList<>();

        profile_image = findViewById(R.id.profile_image);
        receiver_name = findViewById(R.id.receiver_name);
        sendBtn = findViewById(R.id.sendBtn);
        editMessage = findViewById(R.id.editMessage);
        messageAdapter = findViewById(R.id.messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter.setLayoutManager(linearLayoutManager);
        adapter = new MessagesAdapter(ChatActivity.this,messagesArrayList);
        messageAdapter.setAdapter(adapter);

        Picasso.get().load(receiverImage).into(profile_image);
        receiver_name.setText("" + receiverName);

        senderUID = firebaseAuth.getUid();

        senderRoom = senderUID+receiverUID;
        receiverRoom = receiverUID+senderUID;



        DatabaseReference reference = database.getReference().child("user").child(Objects.requireNonNull(firebaseAuth.getUid()));
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                sImage = Objects.requireNonNull(snapshot.child("imageUri").getValue()).toString();
                rImage = receiverImage;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(v ->
        {
            String message = editMessage.getText().toString();
            if(message.isEmpty())
            {
                Toast.makeText(ChatActivity.this, "Please Enter Valid Message", Toast.LENGTH_SHORT).show();
                return;
            }
            editMessage.setText("");
            Date date = new Date();

            Messages messages = new Messages(message,senderUID,date.getTime());

            database = FirebaseDatabase.getInstance();
            database.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .push()
                    .setValue(messages).addOnCompleteListener(task -> database.getReference().child("chats")
                            .child(receiverRoom)
                            .child("messages")
                            .push().setValue(messages).addOnCompleteListener(task1 ->
                            {

                            }));
        });

    }
}