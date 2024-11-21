package com.example.planetze35;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
     * Fetch user data from the Firebase Realtime Database
     * @param uid the uid of the user in the database
     * @param callback the callback to be called when the data is fetched
     */
    public static void fetchUserData(String uid, UserCallback callback) {
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

    // Callback interface for fetching user data
    public interface UserCallback {
        void onSuccess(User userObject);
        void onFailure(String errorMessage);
    }

    // TODO: add more methods if needed
}
