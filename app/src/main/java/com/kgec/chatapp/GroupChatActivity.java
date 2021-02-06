package com.kgec.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.strictmode.CredentialProtectedWhileLockedViolation;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private ImageButton myimagebutton;
    private EditText usermessageinput;
    private ScrollView mScrollview;
    private TextView displaytextmessage;
    private String currentGroupname,CurrentUserId,CurrentUsername,CurrentDate,CurrentTime;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference,GroupNameref,GroupMessagekeyref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);




        currentGroupname=getIntent().getExtras().get("group name").toString();
        Toast.makeText(this,currentGroupname+" welcome", Toast.LENGTH_LONG).show();


        auth=FirebaseAuth.getInstance();
        CurrentUserId=auth.getCurrentUser().getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Users");

        GroupNameref=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupname);
        Initialize();

        GetUserInfo();

        myimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveMessageInfoToDatabase();
                usermessageinput.setText("");

                mScrollview.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();

        GroupNameref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){

                    Displaymessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){

                    Displaymessages(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void Initialize() {
        mtoolbar=findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("group name");
        myimagebutton=findViewById(R.id.send_message_button);
        displaytextmessage=findViewById(R.id.group_chat_text_display);
        usermessageinput=findViewById(R.id.input_group_message);
        mScrollview=findViewById(R.id.myScrollview);

    }
    private void GetUserInfo() {

        databaseReference.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    CurrentUsername=dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void SaveMessageInfoToDatabase() {

        String message=usermessageinput.getText().toString();
        String messageKEY=GroupNameref.push().getKey();

        if (TextUtils.isEmpty(message)){

            Toast.makeText(this, "Please Enter the message. . .", Toast.LENGTH_SHORT).show();
        }else {

            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat currentDateFormat=new SimpleDateFormat("MMM dd,yyyy");
            CurrentDate=currentDateFormat.format(calendar.getTime());


            Calendar calFortime=Calendar.getInstance();
            SimpleDateFormat currentTimeFormat=new SimpleDateFormat("hh:mm a");
            CurrentDate=currentTimeFormat.format(calFortime.getTime());


            HashMap<String,Object>groupmessageKey=new HashMap<>();
            GroupNameref.updateChildren(groupmessageKey);

            GroupMessagekeyref=GroupNameref.child(messageKEY);

            HashMap<String,Object>messageInfomap=new HashMap<>();

            messageInfomap.put("username",CurrentUsername);
            messageInfomap.put("time",CurrentTime);
            messageInfomap.put("date",CurrentDate);
            messageInfomap.put("message",message);

            GroupMessagekeyref.updateChildren(messageInfomap);


        }

    }

    private void Displaymessages(DataSnapshot dataSnapshot) {

        Iterator iterator=dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()){

            //String chatusername=(String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String)((DataSnapshot)iterator.next()).getValue();
           String chatTime=(String)((DataSnapshot)iterator.next()).getValue();
            String chatDate=(String)((DataSnapshot)iterator.next()).getValue();

            displaytextmessage.append(chatMessage+"\n"+chatDate+"   \n  "+chatTime+" \n "+"\n \n \n");

            mScrollview.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }




}
