package com.example.planetze35.userauthentication;

public interface Contract {

    public interface LoginActivityView {
        void launchMainActivity();
        void launchEmailVerificationActivity();
        void launchAnnualCarbonFootprintActivity();
        void showSnackbar(String message, int length, int color);
    }

    public interface LoginActivityPresenter {
        void loginUser(String email, String password);
        void navigateToMainActivity();
        void navigateToEmailVerificationActivity();
        void navigateToInitialSetupActivity();
        void setLoginViewSnackbar(String message, int length, int color);

    }

    public interface LoginActivityModel {
        void processLogin(LoginActivityPresenter presenter, String email, String password);
    }
}
