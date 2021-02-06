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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private EditText inputphonenumber, inputverificationcode;
    private Button Sendverificationcode, verifybutton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();


        Sendverificationcode =findViewById(R.id.send_ver_code_button);
        verifybutton = (Button) findViewById(R.id.verify_button);
        inputphonenumber = (EditText) findViewById(R.id.phone_number_input);
        inputverificationcode = (EditText) findViewById(R.id.verification_code_input);
        loadingbar = new ProgressDialog(this);


        Sendverificationcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = inputphonenumber.getText().toString();

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter your phone number first...", Toast.LENGTH_LONG).show();
                } else {
                    loadingbar.setTitle("Phone Verification");
                    loadingbar.setMessage("please wait, while we are authenticating your phone...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();


                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });


        verifybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sendverificationcode.setVisibility(View.INVISIBLE);
                inputphonenumber.setVisibility(View.INVISIBLE);

                String verificationCode = inputverificationcode.getText().toString();

                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(PhoneLoginActivity.this, "Please write verification code first...", Toast.LENGTH_LONG).show();
                } else {
                    loadingbar.setTitle("Verification Code");
                    loadingbar.setMessage("please wait, while we are verifying verification code...");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingbar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please enter correct phone number with your country code..."+e.getMessage(), Toast.LENGTH_LONG).show();

                Sendverificationcode.setVisibility(View.VISIBLE);
                inputphonenumber.setVisibility(View.VISIBLE);

                verifybutton.setVisibility(View.INVISIBLE);
                inputverificationcode.setVisibility(View.INVISIBLE);
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingbar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Code has been sent, please check and verify...", Toast.LENGTH_LONG).show();

                Sendverificationcode.setVisibility(View.INVISIBLE);
                inputphonenumber.setVisibility(View.INVISIBLE);

                verifybutton.setVisibility(View.VISIBLE);
                inputverificationcode.setVisibility(View.VISIBLE);
            }
        };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingbar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Congratulations, you're logged in successfully...", Toast.LENGTH_LONG).show();
                            SendUserToMainActivity();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error : " + message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

}