package com.example.planetze35;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPageActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etPasswordConfirmation;
    private Button btnSignup, btnLogin, btnForgotPassword;
    private FirebaseAuth auth;

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

        auth = FirebaseAuth.getInstance();
        etEmail = this.findViewById(R.id.etLoginEmail);
        etPassword = this.findViewById(R.id.etLoginPassword);
        btnLogin = this.findViewById(R.id.btnLogin);
        btnSignup = this.findViewById(R.id.btnSignup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser(view);
            }
        });

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

    /**
     * Log in the user
     * @param view The view that was clicked
     */
    private void loginUser(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (!filledFields(email, password)) {
            Snackbar.make(view, "Please fill all the fields", Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    return;
                }

                // check if the email is verified
                if (user.isEmailVerified()) {
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    // TODO: send user to the welcome activity if it is their first time logging in
                    // send user to the home page
                    greetUser(user);
                    startActivity(intent);
                    finish();
                } else {
                    Snackbar.make(view, "Email not verified", Snackbar.LENGTH_INDEFINITE)
                            .setTextColor(Color.RED)
                            .setAction("Send verification email", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EmailUtils.sendVerificationEmail(LoginPageActivity.this, user);
                                    // send user to the email verification page to verify their email
                                    Intent intent = new Intent(v.getContext(), EmailVerificationPageActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setActionTextColor(Color.BLUE)
                            .show();
                }

            } else {
                Snackbar.make(view, "Invalid email or password", Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
            }
        });
    }

    private void greetUser(@NonNull FirebaseUser user) {
        String uid = user.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        dbRef.child("firstName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Toast.makeText(LoginPageActivity.this, "Hello " + dataSnapshot.getValue(String.class), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginPageActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean filledFields(String email, String password) {
        return !email.isEmpty() && !password.isEmpty();
    }

}
