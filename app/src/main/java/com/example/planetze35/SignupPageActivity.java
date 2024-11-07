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

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

public class SignupPageActivity extends AppCompatActivity {

    private Button btnSignup;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = this.findViewById(R.id.etName);
        etEmail = this.findViewById(R.id.etSignupEmail);
        etPassword = this.findViewById(R.id.etSignupPassword);
        etPasswordConfirmation = this.findViewById(R.id.etSignupPasswordConfirmation);
        btnSignup = this.findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (filledFields()) {
                    EmailValidator emailValidator = EmailValidator.getInstance();
                    String email = etEmail.getText().toString().trim();
                    if (emailValidator.isValid(email) && validatePassword()) {

                        // TODO: I think the login activity should be launched here, so that the user's logs in after account confirmation. Not sure tho?
                        // TODO: Add the user to the database here.
                        Toast.makeText(SignupPageActivity.this, "Welcome " + etName.getText().toString().trim(), Toast.LENGTH_SHORT)
                                .show();

                        Intent intent = new Intent(SignupPageActivity.this, com.example.planetze35.LoginPageActivity.class);
                        startActivity(intent);

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
    }

    private boolean filledFields() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        return !name.isEmpty() && !email.isEmpty() && !password.isEmpty();
    }

    public boolean validatePassword() {
        String password = etPassword.getText().toString().trim();
        String passwordConfirmation = etPasswordConfirmation.getText().toString().trim();
        return password.equals(passwordConfirmation);
    }
}