package com.example.planetze35;

import android.graphics.Color;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                    // send them to the home page
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

}
