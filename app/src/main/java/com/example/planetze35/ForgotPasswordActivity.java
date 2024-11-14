package com.example.planetze35;

import android.graphics.Color;
import android.os.Bundle;
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

import org.apache.commons.validator.routines.EmailValidator;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText etForgotPasswordEmail;
    private Button btnSendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();

        etForgotPasswordEmail = this.findViewById(R.id.etForgotPasswordEmail);

        btnSendEmail = this.findViewById(R.id.btnForgotPasswordEmail);
        btnSendEmail.setOnClickListener(view -> {
            EmailValidator emailValidator = EmailValidator.getInstance();
            String email = etForgotPasswordEmail.getText().toString().trim();
            if (emailValidator.isValid(email)) {
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this,
                                                "An email has been sent to " + email,
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                finish();
            } else {
                Snackbar.make(view, "Please enter a valid email.", Snackbar.LENGTH_SHORT)
                        .setTextColor(Color.RED)
                        .show();
            }
        });
    }
}