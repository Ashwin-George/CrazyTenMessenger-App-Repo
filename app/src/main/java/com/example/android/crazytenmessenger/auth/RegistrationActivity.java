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

import com.airbnb.lottie.L;
import com.example.android.crazytenmessenger.R;
import com.example.android.crazytenmessenger.dao.MessageDAOHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

public class RegistrationActivity extends AppCompatActivity {
    private static String TAG=RegistrationActivity.class.getSimpleName();

    private TextInputEditText usernameInputTextBox;
    private TextInputEditText userMailInputTextBox;
    private TextInputEditText cnfPswdInputTextBox;
    private TextInputEditText userPswdInputTextBox;
    private Button registrationSubmitButton;
    private TextView loginIntentTextView;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userMailInputTextBox=(TextInputEditText) findViewById(R.id.usermail_input_textbox);
        usernameInputTextBox=(TextInputEditText) findViewById(R.id.username_input_textbox);
        userPswdInputTextBox=(TextInputEditText) findViewById(R.id.userpswd_input_textbox);
        cnfPswdInputTextBox=(TextInputEditText) findViewById(R.id.cnfpswd_input_textbox);
        registrationSubmitButton=(Button)findViewById(R.id.registration_submit_button);
        progressBar=(ProgressBar) findViewById(R.id.progress_bar_register);
        loginIntentTextView=(TextView)findViewById(R.id.login_user_intent);
        firebaseAuth=FirebaseAuth.getInstance();

        registrationSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String usermail=userMailInputTextBox.getText().toString().trim();
                String username=usernameInputTextBox.getText().toString().trim();
                String password=userPswdInputTextBox.getText().toString().trim();
                String cnfPassword=cnfPswdInputTextBox.getText().toString().trim();
                if(!password.equals(cnfPassword)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Check your password",Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(usermail)|| TextUtils.isEmpty(password)||TextUtils.isEmpty(cnfPassword)){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Check your credentials",Toast.LENGTH_SHORT).show();
                }else{
                    firebaseAuth.createUserWithEmailAndPassword(usermail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"User Registered",Toast.LENGTH_SHORT).show();
                                setDisplayname(username);
                                addUser(new User(username,usermail,null));
                                Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                                intent.putExtra("FROM_LOGIN",true);
                                startActivity(intent);
                            }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"User Not Registered, Please Try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

        loginIntentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addUser(User user){
        new MessageDAOHelper().addUser(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.v(TAG,"User  successfully added "+user.getUsername());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(findViewById(android.R.id.content),"User not added  "+e.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });
    }
    private void setDisplayname(String username) {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder()
                .setDisplayName(username)

                .build();
        user.updateProfile(profileUpdates);
    }

}