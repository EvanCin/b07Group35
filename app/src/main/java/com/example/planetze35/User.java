package com.example.planetze35;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class User {

    // TODO: add required fields
    private String firstName;
    private String lastName;

    public User() {}

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NonNull
    @Override
    public String toString() {
        return "User {" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    /**
     * Fetch all the user data from the Firebase Realtime Database and store them in a User object
     * @param uid the uid of the user in the database
     * @param callback the callback to be called when the data is fetched
     */
    public static void fetchAllUserData(String uid, UserObjectCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User userObject = dataSnapshot.getValue(User.class);
                    if (userObject != null) {
                        callback.onSuccess(userObject);
                    } else {
                        Log.e("UserData", "User object is null");
                        callback.onFailure("User object is null");
                    }
                } else {
                    Log.d("UserData", "No user data found for UID: " + uid);
                    callback.onFailure("No user data found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserData", "Error fetching data: " + databaseError.getMessage());
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    /**
     * Fetch one specific user data field from the Firebase Realtime Database
     * @param uid the uid of the user in the database
     * @param dataField the field to be fetched
     * @param callback the callback to be called when the data is fetched
     */
    public static void fetchOneUserData(String uid, String dataField, DataFieldCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        databaseRef.child(dataField).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DataSnapshot> task) {
                 if (!task.isSuccessful()) {
                     Log.e("UserData", "Error fetching data", task.getException());
                     callback.onFailure("Error fetching data");
                 } else {
                     callback.onSuccess(task.getResult().getValue().toString());
                 }
             }
        });
    }

    public static void storeAllUserData(String uid, User userObject) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        databaseRef.setValue(userObject);
    }

    public static void storeOneDataField(String uid, String dataField, String data) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        databaseRef.child(dataField).setValue(data);
    }

    public static void storeOneDataField(String uid, String dataField, double data) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        databaseRef.child(dataField).setValue(data);
    }

    public static void storeOneDataField(String uid, String dataField, boolean data) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        databaseRef.child(dataField).setValue(data);
    }

    public static void storeOneDataField(String uid, String dataField, List<String> data) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        databaseRef.child(dataField).setValue(data);
    }

    /**
     * Callback interface for fetching user data. This allows the UserObject to be used in the every activity
     * where the function is called despite the asynchronous nature of the function.
     */
    public interface UserObjectCallback {
        void onSuccess(User userObject);
        void onFailure(String errorMessage);
    }

    /**
     * Callback interface for fetching a single user data field. This allows the data field to be used in the every activity
     * where the function is called despite the asynchronous nature of the function.
     */
    public interface DataFieldCallback {
        void onSuccess(String data);
        void onFailure(String errorMessage);
    }

    // TODO: add more methods if needed
}
