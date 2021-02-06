package com.kgec.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText reset_email;
    private Button reset_password_btn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        reset_email=findViewById(R.id.reset_email_id);
        reset_password_btn=findViewById(R.id.reset_password);

        mAuth=FirebaseAuth.getInstance();

        reset_password_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=reset_email.getText().toString();

                if (TextUtils.isEmpty(email)){

                    Toast.makeText(ForgotPasswordActivity.this, "Please Enter proper email Id", Toast.LENGTH_LONG).show();
                }
                else {

                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                Toast.makeText(ForgotPasswordActivity.this, "Please check in your email", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                            }
                            else {

                                String e=task.getException().getMessage();

                                Toast.makeText(ForgotPasswordActivity.this, "Error  "+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }

            }
        });


    }
}