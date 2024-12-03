package com.example.planetze35;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Utility class for interacting with the Firebase Realtime Database
 */
public class DatabaseUtils {

    /**
     * Fetch all the user data from the Firebase Realtime Database and store them in a User object
     * @param uid the uid of the user in the database
     * @param callback the callback to be called when the data is fetched
     */
    public static void fetchAllUserData(String uid, UserInfoCallback callback) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserInfo userInfo = dataSnapshot.getValue(UserInfo.class);
                    if (userInfo != null) {
                        callback.onSuccess(userInfo);
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
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        dbRef.child(dataField).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

    /**
     * Store all the user data in the Firebase Realtime Database
     * @param uid the uid of the user in the database
     * @param userInfo the User object to be stored
     */
    public static void storeAllUserData(String uid, UserInfo userInfo) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        dbRef.setValue(userInfo);
    }

    /**
     * Store one specific user data field in the Firebase Realtime Database
     * @param uid the uid of the user in the database
     * @param dataField the field to be stored
     * @param data the data to be stored
     */
    public static void storeOneDataField(String uid, String dataField, String data) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        dbRef.child(dataField).setValue(data);
    }

    /**
     * Store one specific user data field in the Firebase Realtime Database
     * @param uid the uid of the user in the database
     * @param dataField the field to be stored
     * @param data the data to be stored
     */
    public static void storeOneDataField(String uid, String dataField, double data) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        dbRef.child(dataField).setValue(data);
    }

    /**
     * Store one specific user data field in the Firebase Realtime Database
     * @param uid the uid of the user in the database
     * @param dataField the field to be stored
     * @param data the data to be stored
     */
    public static void storeOneDataField(String uid, String dataField, boolean data) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        dbRef.child(dataField).setValue(data);
    }

    /**
     * Store one specific user data field in the Firebase Realtime Database
     * @param uid the uid of the user in the database
     * @param dataField the field to be stored
     * @param data the data to be stored
     */
    public static void storeOneDataField(String uid, String dataField, List<String> data) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        dbRef.child(dataField).setValue(data);
    }

    /**
     * Callback interface for fetching user data. This allows the UserObject to be used in the every activity
     * where the function is called despite the asynchronous nature of the function.
     */
    public interface UserInfoCallback {
        void onSuccess(UserInfo userInfo);
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
