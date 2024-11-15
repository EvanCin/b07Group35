package com.example.planetze35;

import android.graphics.Color;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivityPresenter {

    private LoginActivityView view;
    private LoginActivityModel model;

    public LoginActivityPresenter(LoginActivityView view, LoginActivityModel model) {
        this.view = view;
        this.model = model;
    }

    public void loginUser(String email, String password) {
        if (!filledFields(email, password)) {
            setSnackbar("Please fill all the fields", Snackbar.LENGTH_SHORT, Color.RED);
            return;
        }
        model.loginProcess(this, email, password);
    }

    public boolean filledFields(String email, String password) {
        return !email.isEmpty() && !password.isEmpty();
    }

    public void navigateToMainActivity() {
        view.launchMainActivity();
    }

    public void navigateToEmailVerificationActivity(FirebaseUser user) {
        EmailUtils.sendVerificationEmail(view, user);
        view.launchEmailVerificationActivity();
    }

    public void setSnackbar(String message, int length, int color) {
        view.showSnackbar(message, length, color);
    }

    public void setToast(String message, int length) {
        view.showToast(message, length);
    }

}
