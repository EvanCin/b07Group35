package com.example.planetze35;

import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivityModel {

    private FirebaseAuth auth;

    public LoginActivityModel() {
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Log in the user
     * @param presenter The presenter
     * @param email The email
     * @param password The password
     */
    public void loginProcess(LoginActivityPresenter presenter, String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    presenter.setSnackbar("User not found", Snackbar.LENGTH_SHORT, Color.RED);
                    return;
                }

                // check if the email is verified
                if (user.isEmailVerified()) {
                    // TODO: send user to the welcome activity if it is their first time logging in
                    // greet user and send them to the home page
                    greetUser(presenter, user);
                    presenter.navigateToMainActivity();
                } else {
                    // send user to the email verification page to verify their email
                    presenter.navigateToEmailVerificationActivity(user);
                }

            } else {
                presenter.setSnackbar("Invalid email or password", Snackbar.LENGTH_SHORT, Color.RED);
            }
        });
    }

    /**
     * Get the user's first name and call the appropriate method in the presenter to greet the user
     * @param user The user
     */
    private void greetUser(LoginActivityPresenter presenter, FirebaseUser user) {
        String uid = user.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        dbRef.child("firstName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // handle the user's first name
                presenter.setToast("Hello " + dataSnapshot.getValue(String.class), Toast.LENGTH_LONG);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                presenter.setToast("Error: " + databaseError.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }
}
