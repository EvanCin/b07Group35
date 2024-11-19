package com.example.planetze35.userauthentication;

import android.graphics.Color;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginPresenter implements Contract.LoginPresenter {

    private Contract.LoginActivityView loginView;
    private Contract.ForgotPasswordActivityView forgotPasswordView;
    private Contract.LoginModel model;


    public LoginPresenter(Contract.LoginActivityView loginView, Contract.LoginModel model) {
        this.loginView = loginView;
        this.model = model;
    }

    public LoginPresenter(Contract.ForgotPasswordActivityView forgotPasswordView, Contract.LoginModel model) {
        this.forgotPasswordView = forgotPasswordView;
        this.model = model;
    }

    public void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            loginView.showSnackbar("Please fill all the fields", Snackbar.LENGTH_SHORT, Color.RED);
            return;
        }

        model.processLogin(this, email, password);
    }

    public void sendPasswordResetEmail(String email) {
        EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(email)) {
            forgotPasswordView.showSnackbar("Please enter a valid email.", Snackbar.LENGTH_SHORT, Color.RED);
            return;
        }

        model.processSendingPasswordResetEmail(this, email);
    }

    public void navigateToMainActivity() {
        loginView.launchMainActivity();
    }

    public void navigateToEmailVerificationActivity() {
        loginView.launchEmailVerificationActivity();
    }

    public void navigateToLoginActivity() {
        forgotPasswordView.goBackToLoginActivity();
    }

    public void setLoginViewSnackbar(String message, int length, int color) {
        loginView.showSnackbar(message, length, color);
    }

    public void setForgotPasswordViewToast(String message, int length) {
        forgotPasswordView.showToast(message, length);
    }
}
