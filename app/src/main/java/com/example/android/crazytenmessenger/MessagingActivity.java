package com.example.android.crazytenmessenger;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.crazytenmessenger.auth.UserProfileActivity;
import com.example.android.crazytenmessenger.dao.MessageDAOHelper;
import com.example.android.crazytenmessenger.utils.DateUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {

    private ListView listView;
    private FloatingActionButton sendButton;
    private EditText messageEditTextBox;
    private ProgressBar progressBar;
    private ImageView imagePicker;

    private static final String TAG=MessagingActivity.class.getSimpleName();
    private static final int MAX_WORD_LIMIT = 1000;

    private DatabaseReference messageDatabaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference chatPhotoStorageReference;
    private ChildEventListener childEventListener;
    private List<Message> messages;
    private SharedPreferences sharedPreferences;
    private String username;
    private MessageDAOHelper daoHelper;
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        listView=(ListView) findViewById(R.id.message_list_view);
        messageEditTextBox=(EditText) findViewById(R.id.message_box);
        sendButton=(FloatingActionButton) findViewById(R.id.send_button);
        imagePicker=(ImageView) findViewById(R.id.image_picker);
        progressBar=(ProgressBar)  findViewById(R.id.progress_Bar);

        daoHelper=new MessageDAOHelper();
        messageDatabaseReference=daoHelper.getMessageDatabaseReference();
        firebaseStorage=FirebaseStorage.getInstance();
        chatPhotoStorageReference=firebaseStorage.getReference().child("chat_photos");
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);

        messages=new ArrayList<>();
        loadPreviousMessages();
        messageAdapter=new MessageAdapter(this,R.layout.item_message,messages);
        listView.setAdapter(messageAdapter);
        username=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        ActivityResultLauncher<String> mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                uploadPhoto(result);
            }
        });


        childEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message currentMsg=snapshot.getValue(Message.class);
                messageAdapter.add(currentMsg);
                listView.smoothScrollToPosition(messageAdapter.getCount());
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Snackbar.make(findViewById(android.R.id.content),"succesfully removed", BaseTransientBottomBar.LENGTH_SHORT).show();
                Log.v(TAG,"Removed : "+snapshot.getValue());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };

        messageEditTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()>0)
                    sendButton.setEnabled(true);
                else
                    sendButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        messageEditTextBox.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_WORD_LIMIT)});

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                username=sharedPreferences.getString(getString(R.string.my_profile_username_key),"USER");
                username=FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String timeStamp= DateUtils.getCurrentTimestamp();
                Message message=new Message(messageEditTextBox.getText().toString(),username, null, timeStamp);
                daoHelper.addMessage(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(android.R.id.content),"Message Not Sent "+e.getMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                });

                messageEditTextBox.setText("");
            }
        });
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });
        messageDatabaseReference.addChildEventListener(childEventListener);
    }

    /**
     * Signing in the the current user automatically using OnStart
     * Using Firebase Auth getCurrentUser
    */

    @Override
    protected void onStart() {
        super.onStart();
        String user= FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        if(user==null)
            user="";
        Snackbar.make(findViewById(android.R.id.content),"Welcome "+user,BaseTransientBottomBar.LENGTH_SHORT).show();
    }



    public void uploadPhoto(Uri uri){
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image.....");
        progressDialog.show();
        StorageReference photoRef=chatPhotoStorageReference.child(uri.getLastPathSegment());
        photoRef.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> result=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        progressDialog.dismiss();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String timeStamp=DateUtils.getCurrentTimestamp();
                                Message newMessage=new Message(null,username,uri.toString(), timeStamp);
                                daoHelper.addMessage(newMessage);
                            }
                        });

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double percentage=(100.00*snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        Log.v(TAG,"Progress : "+percentage);
                        progressDialog.setMessage("Progress  : "+(int)percentage+"%");
                    }
                });
        Toast.makeText(this,"Image uploaded",Toast.LENGTH_SHORT).show();
    }



    private void deleteAllMessages(){
        AlertDialog dialog=new AlertDialog.Builder(this)
                .setTitle("Delete all messages")
                .setMessage("Are you sure you want to delete all messages")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getApplicationContext(),"Deleting... ",Toast.LENGTH_SHORT).show();

                        messageDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                        chatPhotoStorageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"All files Deleted ",Toast.LENGTH_SHORT).show();
                                messages.clear();
                                messageAdapter.notifyDataSetChanged();
                                listView.setAdapter(messageAdapter);
                                loadPreviousMessages();

                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no,null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }



    private void loadPreviousMessages(){
        messageDatabaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful())
                    Log.e(TAG,"Error in retrieving data ",task.getException());
                else{
                    int count= (int) task.getResult().getChildrenCount();
                    if(count==0){

                        Snackbar.make(findViewById(android.R.id.content),
                                "Let's start chatting",
                                BaseTransientBottomBar.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.profile_menu){
//            Intent intent=new Intent(MessagingActivity.this,MyProfileActivity.class);
            Intent intent=new Intent(MessagingActivity.this, UserProfileActivity.class);
            startActivity(intent);
        }else if(id==R.id.delete_all_option){
            deleteAllMessages();
        }else if(id==R.id.signout){
            signoutCurrentUser();
        }
        return super.onOptionsItemSelected(item);
    }



    private void signoutCurrentUser() {
        FirebaseAuth.getInstance().signOut();
        finish();
    }


}