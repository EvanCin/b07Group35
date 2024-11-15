package com.example.planetze35;

import android.content.Intent;
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

public class LoginActivityView extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnSignup, btnLogin, btnForgotPassword;
    private LoginActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etEmail = this.findViewById(R.id.etLoginEmail);
        etPassword = this.findViewById(R.id.etLoginPassword);
        btnLogin = this.findViewById(R.id.btnLogin);
        btnSignup = this.findViewById(R.id.btnSignup);

        presenter = new LoginActivityPresenter(this, new LoginActivityModel());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.loginUser(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignupActivity.class);
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

    public void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchEmailVerificationActivity() {
        Intent intent = new Intent(this, EmailVerificationActivity.class);
        startActivity(intent);
    }

    public void showSnackbar(String message, int length, int color) {
        Snackbar.make(findViewById(R.id.main), message, length).setTextColor(color).show();
    }

    public void showToast(String message, int length) {
        Toast.makeText(this, message, length).show();
    }
}
