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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

public class SignupPageActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword, etPasswordConfirmation;
    private Button btnSignup;
    private FirebaseAuth auth;

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

        auth = FirebaseAuth.getInstance();
        etFirstName = this.findViewById(R.id.etFirstName);
        etLastName = this.findViewById(R.id.etLastName);
        etEmail = this.findViewById(R.id.etSignupEmail);
        etPassword = this.findViewById(R.id.etSignupPassword);
        etPasswordConfirmation = this.findViewById(R.id.etSignupPasswordConfirmation);
        btnSignup = this.findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser(view);
            }
        });

    }

    /**
     * Add a valid user to the database
     * @param view The view that was clicked
     */
    private void addUser(View view) {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String passwordConfirmation = etPasswordConfirmation.getText().toString().trim();

        if (!validateAllInputs(view, firstName, lastName, email, password, passwordConfirmation)) {
            return;
        }

        // if everything is valid then add the user to the database
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                storeUserName(user, firstName, lastName);
                sendEmailVerification(view, user);

            } else {
                String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    // if an account already exists under this email
                    Snackbar.make(view, "Email already exists. Please log in!", Snackbar.LENGTH_INDEFINITE)
                            .setTextColor(Color.RED)
                            .setAction("Log in", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            })
                            .setActionTextColor(Color.BLUE)
                            .show();
                } else {
                    Snackbar.make(view, "Error: " + errorMessage, Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
                }
            }
        });
    }

    /**
     * Send an email verification to the user
     * @param view The view that was clicked
     * @param user The user's address in database
     */
    private void sendEmailVerification(View view, FirebaseUser user) {
        if (user == null) {
            return;
        }

        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignupPageActivity.this, "Verification email sent", Toast.LENGTH_SHORT)
                        .show();

                // go to the email verification page
                Intent intent = new Intent(SignupPageActivity.this, EmailVerificationPageActivity.class);
                startActivity(intent);
                finish();
            } else {
                Snackbar.make(view, "Failed to send verification email, please try again", Snackbar.LENGTH_SHORT)
                        .setTextColor(Color.RED)
                        .show();
            }
        });
    }

    /**
     * Store the user's name in the Firebase database
     * @param user The user's address in database
     * @param firstName The user's first name
     * @param lastName The user's last name
     */
    private void storeUserName(FirebaseUser user, String firstName, String lastName) {
        if (user == null) {
            return;
        }

        String uid = user.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        dbRef.child("firstName").setValue(firstName);
        dbRef.child("lastName").setValue(lastName);

    }

    /**
     * Validate all the inputs
     * @param view The view that was clicked
     * @param firstName The user's first name
     * @param lastName The user's last name
     * @param email The user's email
     * @param password The user's password
     * @param passwordConfirmation The user's password confirmation
     * @return true if all the inputs are valid, false otherwise
     */
    private boolean validateAllInputs(View view, String firstName, String lastName, String email, String password, String passwordConfirmation) {
        if (!filledFields(firstName, lastName, email, password, passwordConfirmation)) {
            Snackbar.make(view, "Please fill all the fields", Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
            return false;
        }

        EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(email)) {
            Snackbar.make(view, "Please enter a valid email", Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
            return false;
        }

        if (!validatePassword(password)) {
            Snackbar.make(view, "Password must be at least 6 characters", Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
            return false;
        }

        if (!validatePasswordConfirmation(password, passwordConfirmation)) {
            Snackbar.make(view, "Passwords do not match", Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
            return false;
        }

        return true;
    }

    private boolean filledFields(String firstName, String lastName, String email, String password, String passwordConfirmation) {
        return !firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordConfirmation.isEmpty();
    }

    private boolean validatePasswordConfirmation(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    private boolean validatePassword(String password) {
        return password.length() >= 6;
    }
}
