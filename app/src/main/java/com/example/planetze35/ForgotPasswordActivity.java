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

public class ForgotPasswordActivity extends AppCompatActivity {

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

        etForgotPasswordEmail = this.findViewById(R.id.etForgotPasswordEmail);

        btnSendEmail = this.findViewById(R.id.btnForgotPasswordEmail);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmailValidator emailValidator = EmailValidator.getInstance();
                String email = etForgotPasswordEmail.getText().toString().trim();

                // TODO: Handle case with valid email that does not exist in database
                if (emailValidator.isValid(email)) {
                    // TODO: Send the email for changing passwords

                    Toast.makeText(ForgotPasswordActivity.this,
                                    "An email has been sent to " + email,
                                    Toast.LENGTH_SHORT)
                            .show();
                    finish();
                } else {
                    Snackbar.make(view, "Please enter a valid email.", Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.RED)
                            .show();
                }
            }
        });
    }
}