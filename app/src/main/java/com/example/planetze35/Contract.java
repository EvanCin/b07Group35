package com.example.planetze35;

public interface Contract {

    public interface LoginActivityView {
        void launchMainActivity();
        void launchEmailVerificationActivity();
        void showSnackbar(String message, int length, int color);
    }

    public interface ForgotPasswordActivityView {
        void goBackToLoginActivity();
        void showToast(String message, int length);
        void showSnackbar(String message, int length, int color);
    }

    public interface LoginPresenter {
        void loginUser(String email, String password);
        void sendPasswordResetEmail(String email);
        void navigateToMainActivity();
        void navigateToEmailVerificationActivity();
        void navigateToLoginActivity();
        void setLoginViewSnackbar(String message, int length, int color);
        void setForgotPasswordViewToast(String message, int length);
    }

    public interface LoginModel {
        void processLogin(LoginPresenter presenter, String email, String password);
        void processSendingPasswordResetEmail(LoginPresenter presenter, String email);
    }
}
