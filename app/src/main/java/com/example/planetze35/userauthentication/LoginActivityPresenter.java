package com.example.planetze35.userauthentication;

import android.graphics.Color;

import com.google.android.material.snackbar.Snackbar;

public class LoginActivityPresenter implements Contract.LoginActivityPresenter {

    private Contract.LoginActivityView view;
    private Contract.LoginActivityModel model;


    public LoginActivityPresenter(Contract.LoginActivityView View, Contract.LoginActivityModel model) {
        this.view = View;
        this.model = model;
    }

    public void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.showSnackbar("Please fill all the fields", Snackbar.LENGTH_SHORT, Color.RED);
            return;
        }

        model.processLogin(this, email, password);
    }

    public void navigateToMainActivity() {
        view.launchMainActivity();
    }

    public void navigateToEmailVerificationActivity() {
        view.launchEmailVerificationActivity();
    }

    public void setLoginViewSnackbar(String message, int length, int color) {
        view.showSnackbar(message, length, color);
    }

}
