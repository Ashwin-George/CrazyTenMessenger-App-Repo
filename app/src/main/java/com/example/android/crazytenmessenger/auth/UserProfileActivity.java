package com.example.android.crazytenmessenger.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide.*;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.android.crazytenmessenger.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {
    private FloatingActionButton button;
    private ImageView profileImageView;
    private FirebaseAuth mAuth;
    private TextView usernameTextView;
    private TextView usermailTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        button=findViewById(R.id.load_image_button);
        profileImageView=(ImageView) findViewById(R.id.profile_pic_view);
        usermailTextView=(TextView)findViewById(R.id.usermail_textview);
        usernameTextView=(TextView)findViewById(R.id.username_textview);

        mAuth=FirebaseAuth.getInstance();
        String imageUri="https://helostatus.com/wp-content/uploads/2021/09/2021-profile-WhatsApp-hd.jpg";

        FirebaseUser curUser=mAuth.getCurrentUser();
        if(curUser!=null){
            usernameTextView.setText(curUser.getDisplayName());
            usermailTextView.setText(curUser.getEmail());
            imageUri=curUser.getPhotoUrl().toString();
        }

        Glide.with(UserProfileActivity.this).
                load(imageUri)
                .transform(new CircleCrop())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(profileImageView);


    }
}