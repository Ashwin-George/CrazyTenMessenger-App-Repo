package com.example.android.crazytenmessenger.dao;

import com.example.android.crazytenmessenger.Message;
import com.example.android.crazytenmessenger.auth.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageDAOHelper {
    private final FirebaseDatabase firebaseDatabase;
    private final DatabaseReference messageDatabaseReference;
    private final DatabaseReference userDatabaseReference;
    public MessageDAOHelper(){
        firebaseDatabase=FirebaseDatabase.getInstance();
        messageDatabaseReference =firebaseDatabase.getReference().child("Messages");
        userDatabaseReference=firebaseDatabase.getReference().child("Users");

    }

    public DatabaseReference getMessageDatabaseReference() {
        return messageDatabaseReference;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public Task<Void> addMessage(Message message){
        return messageDatabaseReference.push().setValue(message);
    }

    public Task<Void> addUser(User user){
        return userDatabaseReference.push().setValue(user);
    }

    public DatabaseReference getUserDatabaseReference() {
        return userDatabaseReference;
    }
}
