package com.example.planetze35;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planetze35.SignupPageActivity;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

public class LoginPageActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordConfirmation;
    private Button btnSignup;
    private Button btnLogin;
    private Button btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = this.findViewById(R.id.etLoginEmail);
        etPassword = this.findViewById(R.id.etLoginPassword);
        etPasswordConfirmation = this.findViewById(R.id.etLoginPasswordConfirmation);
        btnLogin = this.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (filledFields()) {
                    EmailValidator emailValidator = EmailValidator.getInstance();
                    String email = etEmail.getText().toString().trim();

                    // TODO: Check if the email and password match.
                    if (emailValidator.isValid(email) && validatePassword()) {

                        // TODO: make sure the name is the user's name you get from the database. For now we use a dummy name.
                        String name = "Ally";

                        // TODO: Launch the main activity here after it is created. That is, after greeting the user,the main menu should show up.
                        Toast.makeText(LoginPageActivity.this, "Welcome " + name, Toast.LENGTH_SHORT)
                                .show();

                    } else if (!emailValidator.isValid(email)) {
                        Snackbar.make(view, "Please enter a valid email", Snackbar.LENGTH_SHORT)
                                .setTextColor(Color.RED)
                                .show();
                    } else {
                        Snackbar.make(view, "Passwords do not match", Snackbar.LENGTH_SHORT)
                                .setTextColor(Color.RED)
                                .show();
                    }

                } else {
                    Snackbar.make(view, "Please fill all the fields", Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.RED)
                            .show();
                }
            }
        });

        btnSignup = this.findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignupPageActivity.class);
                startActivity(intent);
            }
        });

        btnForgotPassword = this.findViewById(R.id.btnForgotPassword);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean filledFields() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirmation = etPasswordConfirmation.getText().toString().trim();
        return !email.isEmpty() && !password.isEmpty() && !passwordConfirmation.isEmpty();
    }

    public boolean validatePassword() {
        String password = etPassword.getText().toString().trim();
        String passwordConfirmation = etPasswordConfirmation.getText().toString().trim();
        return password.equals(passwordConfirmation);
    }
}