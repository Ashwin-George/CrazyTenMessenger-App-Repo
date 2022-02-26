package com.example.android.crazytenmessenger.auth;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.crazytenmessenger.R;
import com.example.android.crazytenmessenger.dao.MessageDAOHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {
    private static String TAG=RegistrationActivity.class.getSimpleName();
    private String DEFAULT_PROFILE_IMAGE;
//            =ContentResolver.SCHEME_ANDROID_RESOURCE +
//            "://" + getResources().getResourcePackageName(R.drawable.circular_image_profile_picture)
//            + '/' + getResources().getResourceTypeName(R.drawable.circular_image_profile_picture)
//            + '/' + getResources().getResourceEntryName(R.drawable.circular_image_profile_picture);

    private TextInputEditText usernameInputTextBox;
    private TextInputEditText userMailInputTextBox;
    private TextInputEditText cnfPswdInputTextBox;
    private TextInputEditText userPswdInputTextBox;
    private Button registrationSubmitButton;
    private TextView loginIntentTextView;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private CircleImageView profileImageView;
    private FloatingActionButton profileImageSelectorBtn;

    private String profileImageUri;
    ActivityResultLauncher<String> getImageFromGallery;

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
        profileImageView=(CircleImageView) findViewById(R.id.profile_image_view);
        profileImageSelectorBtn=(FloatingActionButton) findViewById(R.id.profile_image_selector_button);
        firebaseAuth=FirebaseAuth.getInstance();

        DEFAULT_PROFILE_IMAGE=ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.circular_image_profile_picture)
                + '/' + getResources().getResourceTypeName(R.drawable.circular_image_profile_picture)
                + '/' + getResources().getResourceEntryName(R.drawable.circular_image_profile_picture);
        profileImageUri=DEFAULT_PROFILE_IMAGE;


        getImageFromGallery=registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        profileImageUri=result.toString();
                        setImageOnView(result);
                    }
                });

        profileImageSelectorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileImage();
            }
        });

        registrationSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
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

    private void register() {
        progressBar.setVisibility(View.VISIBLE);
        String usermail=userMailInputTextBox.getText().toString().trim();
        String username=usernameInputTextBox.getText().toString().trim();
        String password=userPswdInputTextBox.getText().toString().trim();
        String cnfPassword=cnfPswdInputTextBox.getText().toString().trim();
        if(!password.equals(cnfPassword)){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Check your password",Toast.LENGTH_SHORT).show();
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
                        setDisplay_Name_ImageUri(username,profileImageUri);
                        addUser(new User(username,usermail,profileImageUri));
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
    private void selectProfileImage(){
//        getImageFromGallery=registerForActivityResult(new ActivityResultContracts.GetContent(),
//                new ActivityResultCallback<Uri>() {
//                    @Override
//                    public void onActivityResult(Uri result) {
//                        profileImageUri=result.toString();
//                        setImageOnView(result);
//                    }
//                });
        getImageFromGallery.launch("image/*");
    }
    private void setImageOnView(Uri uri){

        Glide.with(RegistrationActivity.this).
                load(uri)
//                .transform(new CircleCrop())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(profileImageView);

    }
    private void setDisplay_Name_ImageUri(String username, String profileImageUri) {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(Uri.parse(profileImageUri))
                .build();
        if (user != null) {
            user.updateProfile(profileUpdates);
        }
    }

}