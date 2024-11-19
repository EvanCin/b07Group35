package com.example.planetze35.userauthentication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planetze35.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword, etPasswordConfirmation;
    private Button btnSignup;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
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

        if (!filedFields(firstName, lastName, email, password, passwordConfirmation)) {
            Snackbar.make(view, "Please fill all the fields", Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
            return;
        }

        if (!validatePasswordConfirmation(password, passwordConfirmation)) {
            Snackbar.make(view, "Passwords do not match", Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                storeUserName(user, firstName, lastName);
                EmailUtils.sendVerificationEmail(SignupActivity.this, user);
                Intent intent = new Intent(SignupActivity.this, EmailVerificationActivity.class);
                startActivity(intent);
                finish();

            } else {
                String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                if (errorMessage == null) {
                    errorMessage = "Something went wrong!";
                }
                Snackbar.make(view, errorMessage, Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
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
    private boolean validatePasswordConfirmation(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    private boolean filedFields(String firstName, String lastName, String email, String password, String passwordConfirmation) {
        return !firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !passwordConfirmation.isEmpty();
    }

}
