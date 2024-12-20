package com.example.planetze35.userauthentication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planetze35.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailVerificationActivity extends AppCompatActivity {

    private Button btnVerified;
    private Button btnResendVerificationEmail;
    private TextView tvCountDownTimer;
    private CountDownTimer countDownTimer;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_email_verification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVerified = this.findViewById(R.id.btnVerify);
        btnResendVerificationEmail = this.findViewById(R.id.btnResendVerificationEmail);
        tvCountDownTimer = this.findViewById(R.id.tvCountDownTimer);
        user = FirebaseAuth.getInstance().getCurrentUser();
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                btnResendVerificationEmail.setEnabled(false);
                tvCountDownTimer.setText(((Long)(millisUntilFinished/1000)).toString());
            }

            public void onFinish() {
                btnResendVerificationEmail.setEnabled(true);
            }
        };

        countDownTimer.start();

        btnVerified.setOnClickListener(this::checkEmailVerification);

        btnResendVerificationEmail.setOnClickListener(view -> {
            if (!user.isEmailVerified()) {
                EmailUtils.sendVerificationEmail(EmailVerificationActivity.this, user);
                countDownTimer.start();

            } else {
                Toast.makeText(this, "Email already verified", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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
                    Toast.makeText(EmailVerificationActivity.this, "Verification completed successfully", Toast.LENGTH_SHORT).show();
                    // go to the login page
                    Intent intent = new Intent(EmailVerificationActivity.this, LoginActivityView.class);
                    startActivity(intent);
                    finish();
                } else {
                    Snackbar.make(view, "Email not verified", Snackbar.LENGTH_SHORT)
                            .setTextColor(Color.RED)
                            .show();
                }
            }
        });
    }

}