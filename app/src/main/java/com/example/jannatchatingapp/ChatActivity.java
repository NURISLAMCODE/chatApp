package com.example.jannatchatingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReciverImage,ReciverUid,Recivername,senderUid;
    CircleImageView profileIamge;
    TextView ReciverName;
    FirebaseDatabase database;
    FirebaseAuth auth;
    CardView sendBtn;
    EditText edtMassage;

    String senderRoom, receiverRoom;
    RecyclerView messageAdater;
    ArrayList <Messages> messagesArrayList;

    public  static String sImage;
    public  static String rImage;
    MessagesAdepter adepter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        edtMassage =  findViewById(R.id.edtMassage);
        sendBtn =  findViewById(R.id.sendBtn);
        Recivername=getIntent().getStringExtra("name");
        ReciverImage=getIntent().getStringExtra("ReciverImage");
        ReciverUid=getIntent().getStringExtra("uid");
        senderUid = auth.getUid();
        senderRoom = senderUid + ReciverUid ;
        messagesArrayList = new ArrayList<>();
        receiverRoom = ReciverUid + senderUid;
        messageAdater = findViewById(R.id.messageAdater);
        profileIamge  = findViewById(R.id.profile_image);
        ReciverName = findViewById(R.id.ReciverName);
        Picasso.get().load(ReciverImage).into(profileIamge);
        ReciverName.setText(""+Recivername);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdater.setLayoutManager(linearLayoutManager);
        adepter = new MessagesAdepter(ChatActivity.this,messagesArrayList);
        messageAdater.setAdapter(adepter);

       DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");


        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesArrayList.add(messages);
                }
                adepter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                sImage = snapshot.child("image_uri").getValue().toString();
                rImage = ReciverImage;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtMassage.getText().toString();

                if (message.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Message Empty", Toast.LENGTH_SHORT).show();
                return;
                }
                edtMassage.setText("");
                Date date = new Date();
               Messages messages = new Messages(message,senderUid, date.getTime());
                database = FirebaseDatabase.getInstance();

                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .child("messages")
                                        .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });

                            }
                        });

            }
        });

    }
}