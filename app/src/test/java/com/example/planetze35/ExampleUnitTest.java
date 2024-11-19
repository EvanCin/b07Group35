package com.example.planetze35;

import static org.mockito.Mockito.verify;

import android.graphics.Color;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    @Mock
    Contract.LoginModel model;

    @Mock
    Contract.LoginActivityView loginView;
    @Mock
    Contract.ForgotPasswordActivityView forgotPasswordView;


    @Test
    public void testLoginUserEmptyEmail() {
        Contract.LoginPresenter presenter = new LoginPresenter(loginView, model);
        presenter.loginUser("", "password");
        verify(loginView).showSnackbar("Please fill all the fields", Snackbar.LENGTH_SHORT, Color.RED);
    }

    @Test
    public void testLoginUserEmptyPassword() {
        Contract.LoginPresenter presenter = new LoginPresenter(loginView, model);
        presenter.loginUser("email", "");
        verify(loginView).showSnackbar("Please fill all the fields", Snackbar.LENGTH_SHORT, Color.RED);
    }

    @Test
    public void testLoginUserFilledFields() {
        Contract.LoginPresenter presenter = new LoginPresenter(loginView, model);
        presenter.loginUser("email", "password");
        verify(model).processLogin(presenter, "email", "password");
    }

    @Test
    public void testSendPasswordResetEmailInvalidEmail() {
        Contract.LoginPresenter presenter = new LoginPresenter(forgotPasswordView, model);
        presenter.sendPasswordResetEmail("email");
        verify(forgotPasswordView).showSnackbar("Please enter a valid email.", Snackbar.LENGTH_SHORT, Color.RED);
    }

    @Test
    public void testSendPasswordResetEmailValidEmail() {
        Contract.LoginPresenter presenter = new LoginPresenter(forgotPasswordView, model);
        presenter.sendPasswordResetEmail("email@gmail.com");
        verify(model).processSendingPasswordResetEmail(presenter, "email@gmail.com");
    }

    @Test
    public void testNavigateToEmailVerificationActivity() {
        Contract.LoginPresenter presenter = new LoginPresenter(loginView, model);
        presenter.navigateToEmailVerificationActivity();
        verify(loginView).launchEmailVerificationActivity();
    }

    @Test
    public void testNavigateToMainActivity() {
        Contract.LoginPresenter presenter = new LoginPresenter(loginView, model);
        presenter.navigateToMainActivity();
        verify(loginView).launchMainActivity();
    }

    @Test
    public void testNavigateToLoginActivity() {
        Contract.LoginPresenter presenter = new LoginPresenter(forgotPasswordView, model);
        presenter.navigateToLoginActivity();
        verify(forgotPasswordView).goBackToLoginActivity();
    }

    @Test
    public void setLoginViewSnackbar() {
        Contract.LoginPresenter presenter = new LoginPresenter(loginView, model);
        presenter.setLoginViewSnackbar("message", Snackbar.LENGTH_SHORT, Color.RED);
        verify(loginView).showSnackbar("message", Snackbar.LENGTH_SHORT, Color.RED);
    }

    @Test
    public void setForgotPasswordViewToast() {
        Contract.LoginPresenter presenter = new LoginPresenter(forgotPasswordView, model);
        presenter.setForgotPasswordViewToast("message", Toast.LENGTH_SHORT);
        verify(forgotPasswordView).showToast("message", Toast.LENGTH_SHORT);
    }

}