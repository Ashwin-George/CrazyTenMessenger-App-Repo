package com.example.android.crazytenmessenger.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.crazytenmessenger.MainActivity;
import com.example.android.crazytenmessenger.MessagingActivity;
import com.example.android.crazytenmessenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText usernameInputTextBox;
    private TextInputEditText userPswdInputTextBox;
    private Button loginSubmitButton;
    private TextView registrationIntentTextView;
    private TextView forgotPasswordIntentTextView;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameInputTextBox=(TextInputEditText) findViewById(R.id.usermail_login_textbox);
        userPswdInputTextBox=(TextInputEditText) findViewById(R.id.userpswd_login_textbox);
        loginSubmitButton=(Button)findViewById(R.id.login_submit_button);
        registrationIntentTextView=(TextView)findViewById(R.id.new_user_intent);
        forgotPasswordIntentTextView=(TextView)findViewById(R.id.forgot_password_intent);
        progressBar=(ProgressBar) findViewById(R.id.progress_bar_login);
        firebaseAuth=FirebaseAuth.getInstance();

        loginSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=usernameInputTextBox.getText().toString().trim();
                String userPswd=userPswdInputTextBox.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);

                if(TextUtils.isEmpty(username)&& TextUtils.isEmpty(userPswd)){
                    Toast.makeText(getApplicationContext(),"Enter credentials",Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.signInWithEmailAndPassword(username,userPswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(findViewById(android.R.id.content),"Login successful", BaseTransientBottomBar.LENGTH_SHORT).show();
                                Intent intent=new Intent(LoginActivity.this,MessagingActivity.class);
                                startActivity(intent);
                            }else{
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(findViewById(android.R.id.content),"Login not successful, Enter again", BaseTransientBottomBar.LENGTH_SHORT).show();
                                usernameInputTextBox.clearComposingText();
                                userPswdInputTextBox.clearComposingText();
                            }

                        }
                    });
                }
            }
        });

        registrationIntentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });
        forgotPasswordIntentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,PasswordResetActivity.class);
                startActivity(intent);
            }
        });

    }

//    @Override
    protected void onStart() {
        if(getIntent().getExtras()==null){
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        super.onStart();
        FirebaseUser currentUser=
                firebaseAuth.getCurrentUser();
        if(currentUser!=null){
            Log.v(LoginActivity.class.getSimpleName(),currentUser.getEmail() + " "+ currentUser.getUid());
            Intent intent=new Intent(LoginActivity.this, MessagingActivity.class);
            startActivity(intent);
            Snackbar.make(findViewById(android.R.id.content),"Welcome",BaseTransientBottomBar.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Enter new User", Toast.LENGTH_SHORT).show();
        }
    }
}