package com.example.planetze35;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationPageActivity extends AppCompatActivity {

    private Button btnVerify;
    private Button btnResendVerificationEmail;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_verification_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVerify = this.findViewById(R.id.btnVerify);
        btnResendVerificationEmail = this.findViewById(R.id.btnResendVerificationEmail);
        user = FirebaseAuth.getInstance().getCurrentUser();


        btnVerify.setOnClickListener(this::checkEmailVerification);

        btnResendVerificationEmail.setOnClickListener(this::resendVerificationEmail);
    }

    /**
     * Check if the email is verified
     * @param view the view that was clicked
     */
    private void checkEmailVerification(View view) {
        if (user == null) {
            return;
        }
        user.reload().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (user.isEmailVerified()) {
                    Toast.makeText(EmailVerificationPageActivity.this, "Verification completed successfully", Toast.LENGTH_SHORT).show();
                    // go to the login page
                    finish();
                } else {
                    Snackbar.make(view, "Email not verified", Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.RED)
                            .show();
                }
            }
        });
    }

    /**
     * Resend the verification email
     * @param view the view that was clicked
     */
    private void resendVerificationEmail(View view) {
        if (user == null) {
            return;
        }
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EmailVerificationPageActivity.this, "Verification email sent", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(view, "Failed to send verification email, please try again", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}