package com.example.planetze35.userauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planetze35.MainActivity;
import com.example.planetze35.R;
import com.example.planetze35.setup.AnnualCarbonFootprintActivity;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivityView extends AppCompatActivity implements Contract.LoginActivityView {

    private EditText etEmail, etPassword;
    private Button btnSignup, btnLogin, btnForgotPassword;
    private Contract.LoginActivityPresenter presenter;

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
                presenter.loginUser(etEmail.getText().toString().trim(), etPassword.getText().toString());
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignupActivity.class);
                startActivity(intent);
                finish();
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
        finish();
    }

    public void launchAnnualCarbonFootprintActivity() {
        Intent intent = new Intent(this, AnnualCarbonFootprintActivity.class);
        startActivity(intent);
        finish();
    }

    public void showSnackbar(String message, int length, int color) {
        Snackbar.make(findViewById(R.id.main), message, length).setTextColor(color).show();
    }

}
