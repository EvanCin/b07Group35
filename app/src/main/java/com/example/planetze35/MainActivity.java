package com.example.planetze35;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // TODO: The following code block is only for demonstration.
        //  Please replace it with proper logic later.

        // I believe this is the correct way to get the current user. Can make user a field later if needed.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("User", "No user found");
            return;
        }

        // this is how to get the user's UID
        String uid = user.getUid();

        // this code block retrieves the user data from the database and perform the operations
        com.example.planetze35.User.fetchUserData(uid, new com.example.planetze35.User.UserCallback() {
            @Override
            public void onSuccess(com.example.planetze35.User userObject) {
                // this is where you should do the operations I think

                // show users data
                ((TextView)findViewById(R.id.textView)).setText("Welcome, " + userObject.getFirstName());
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("UserData", "Error: " + errorMessage);
            }
        });
    }
}