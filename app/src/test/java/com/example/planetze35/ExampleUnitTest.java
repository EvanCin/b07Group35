package com.example.planetze35;

import static org.mockito.Mockito.verify;

import android.graphics.Color;

import com.example.planetze35.userauthentication.Contract;
import com.example.planetze35.userauthentication.LoginActivityPresenter;
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
    Contract.LoginActivityModel model;

    @Mock
    Contract.LoginActivityView loginView;

    @Test
    public void testLoginUserEmptyEmail() {
        Contract.LoginActivityPresenter presenter = new LoginActivityPresenter(loginView, model);
        presenter.loginUser("", "password");
        verify(loginView).showSnackbar("Please fill all the fields", Snackbar.LENGTH_SHORT, Color.RED);
    }

    @Test
    public void testLoginUserEmptyPassword() {
        Contract.LoginActivityPresenter presenter = new LoginActivityPresenter(loginView, model);
        presenter.loginUser("email", "");
        verify(loginView).showSnackbar("Please fill all the fields", Snackbar.LENGTH_SHORT, Color.RED);
    }

    @Test
    public void testLoginUserFilledFields() {
        Contract.LoginActivityPresenter presenter = new LoginActivityPresenter(loginView, model);
        presenter.loginUser("email", "password");
        verify(model).processLogin(presenter, "email", "password");
    }

   @Test
    public void testNavigateToEmailVerificationActivity() {
        Contract.LoginActivityPresenter presenter = new LoginActivityPresenter(loginView, model);
        presenter.navigateToEmailVerificationActivity();
        verify(loginView).launchEmailVerificationActivity();
    }

    @Test
    public void testNavigateToMainActivity() {
        Contract.LoginActivityPresenter presenter = new LoginActivityPresenter(loginView, model);
        presenter.navigateToMainActivity();
        verify(loginView).launchMainActivity();
    }

   @Test
    public void setLoginViewSnackbar() {
        Contract.LoginActivityPresenter presenter = new LoginActivityPresenter(loginView, model);
        presenter.setLoginViewSnackbar("message", Snackbar.LENGTH_SHORT, Color.RED);
        verify(loginView).showSnackbar("message", Snackbar.LENGTH_SHORT, Color.RED);
    }

}