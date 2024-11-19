package com.example.planetze35;

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

public class ForgotPasswordActivityView extends AppCompatActivity implements Contract.ForgotPasswordActivityView {

    private EditText etForgotPasswordEmail;
    private Button btnSendEmail;
    private Contract.LoginPresenter presenter;

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

        presenter = new LoginPresenter(this, new LoginModel());

        btnSendEmail.setOnClickListener(view -> {
            String email = etForgotPasswordEmail.getText().toString();
            presenter.sendPasswordResetEmail(email);
        });
    }

    public void goBackToLoginActivity() {
        finish();
    }

    public void showToast(String message, int length) {
        Toast.makeText(this, message, length).show();
    }

    public void showSnackbar(String message, int length, int color) {
        Snackbar.make(findViewById(R.id.main), message, length).setTextColor(color).show();
    }

}