package com.example.android.crazytenmessenger.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.crazytenmessenger.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {
    private TextInputEditText emailInputField;
    private Button passwordResetButton;
    private FirebaseAuth mAuth;
    private static final String TAG=PasswordResetActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        emailInputField=(TextInputEditText) findViewById(R.id.email_reset_textbox);
        passwordResetButton=(Button) findViewById(R.id.password_reset_button);
        mAuth=FirebaseAuth.getInstance();

        passwordResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailInputField.getText().toString();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(PasswordResetActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                }else{
                    resetPassword(email);
                }
            }
        });
    }

    private void resetPassword(String email) {

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(PasswordResetActivity.this, "E-mail sent", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PasswordResetActivity.this, "E-mail not sent Check your email", Toast.LENGTH_SHORT).show();
                Log.v(TAG,"Email no sent : "+e.getMessage());
            }
        });
    }
}