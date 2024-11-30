package com.example.planetze35.userauthentication;

import android.graphics.Color;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivityModel implements Contract.LoginActivityModel {

    private FirebaseAuth auth;

    public LoginActivityModel() {
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Process the logging in
     * @param presenter The presenter
     * @param email The email
     * @param password The password
     */
    public void processLogin(Contract.LoginActivityPresenter presenter, String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    presenter.setLoginViewSnackbar("User not found", Snackbar.LENGTH_SHORT, Color.RED);
                    return;
                }

                // check if the email is verified
                if (user.isEmailVerified()) {
                    // send them to the home page
                    presenter.navigateToMainActivity();
                } else {
                    // send user to the email verification page to verify their email
                    presenter.navigateToEmailVerificationActivity();
                }

            } else {
                presenter.setLoginViewSnackbar("Invalid email or password", Snackbar.LENGTH_SHORT, Color.RED);
            }
        });
    }

}
