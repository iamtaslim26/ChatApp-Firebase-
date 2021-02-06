package com.kgec.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.auth.FirebaseInstanceId;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseUser currentuser;
    private Button register;
    private EditText memail,mpassword;
    private TextView malreadyhaveanaccount;
    private FirebaseAuth auth;
    private ProgressDialog loadingBar;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth=FirebaseAuth.getInstance();
        UsersRef= FirebaseDatabase.getInstance().getReference();
        Initialize();
        malreadyhaveanaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
                setbacktoLoginActivity();

            }
        });





    }

    private void setbacktoLoginActivity() {
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }

    private void CreateNewAccount() {
        String email=memail.getText().toString();
        String pass=mpassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            memail.setError("Enter the email. . .. . ");
            return;
        }
        if (TextUtils.isEmpty(pass)){
            mpassword.setError("Enter the password. . . . . ");
            return;
        }
        if (pass.length()<6){
            mpassword.setError("please enter more than 6 digits");
            return;
        }
        else {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait. . .. ");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String currentuserid=auth.getCurrentUser().getUid();
                       // String userToken= FirebaseInstanceId.getInstance().getToken();

                        
                        UsersRef.child("Users").child(currentuserid).setValue("");
                      //  UsersRef.child("Users").child(currentuserid).child("User_Token").setValue(userToken);
                        sendbacktoMainActivity();


                        Toast.makeText(RegisterActivity.this, "successful. . . .. ", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }else {
                        String message=task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error. . .. ."+message, Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void sendbacktoMainActivity() {
        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void Initialize() {
        register=findViewById(R.id.register_button);
        memail=findViewById(R.id.register_email);
        mpassword=findViewById(R.id.register_password);
        malreadyhaveanaccount=findViewById(R.id.already_have_an_account);
        loadingBar=new ProgressDialog(this);
    }

}
