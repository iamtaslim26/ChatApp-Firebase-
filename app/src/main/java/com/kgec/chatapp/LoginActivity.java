package com.kgec.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class LoginActivity extends AppCompatActivity {

        private Button loginbutton,loginphone;
        private EditText loginemail,loginpassword;
        private TextView forgotpassword,mcreatenewaccount,loginusingphone;
        private FirebaseAuth auth;
        private FirebaseUser firebaseUser;
        private ProgressDialog progressDialog;
        private DatabaseReference UsersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Initialize();
        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");

        if (firebaseUser!=null){

            SendUserToMainActivity();
        }


        loginphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,PhoneLoginActivity.class));
            }
        });


        mcreatenewaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });


        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));

            }
        });


        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginAccount();
                setbacktoMainActivity();

    }



    private void LoginAccount() {
        String email=loginemail.getText().toString();
        String password=loginpassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            loginemail.setError("Enter the email. . . . . ");
            return;
        }
        if (TextUtils.isEmpty(password)){
            loginpassword.setError("Enter the password. . . . .");
            return;
        }
        if (password.length()<6){
            loginpassword.setError("please enter the password more than 6 digits");
            return;
        }
        else {
            progressDialog.setTitle("Login. . . .");
            progressDialog.setTitle("please wait. . .");
            progressDialog.setCanceledOnTouchOutside(true);
       //     progressDialog.show();
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){


                        String CurrentUserId=auth.getCurrentUser().getUid();
                        String userToken= FirebaseInstanceId.getInstance().getToken();
                        UsersRef.child(CurrentUserId).child("User_Token").setValue(userToken)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            setbacktoMainActivity();
                                            progressDialog.dismiss();
                                        }

                                    }
                                });






                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Succesfull. . . .", Toast.LENGTH_LONG).show();
                    }else {
                        String message=task.getException().toString();
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Failed. . . ."+message, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
        });

    }

    private void SendUserToMainActivity() {

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    private void setbacktoMainActivity() {
        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private void Initialize() {
        loginbutton=findViewById(R.id.Login_button);
        loginphone=findViewById(R.id.phone_login_button);
        loginemail=findViewById(R.id.login_email);
        loginpassword=findViewById(R.id.login_password);
        forgotpassword=findViewById(R.id.forgot_password_link);
        mcreatenewaccount=findViewById(R.id.Create_new_Account_link);
        loginusingphone=findViewById(R.id.loginusing);
        progressDialog=new ProgressDialog(this);

    }



}
