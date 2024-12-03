package com.example.planetze35.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planetze35.R;
import com.example.planetze35.userauthentication.WelcomeScreenActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {

    private Button btnLogout;
    private Button btnCancel;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogout = findViewById(R.id.btnLogout);
        btnCancel = findViewById(R.id.btnCancel);
        auth = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(v -> {
            auth.signOut();

            Intent intent = new Intent(LogoutActivity.this, WelcomeScreenActivity.class);
            startActivity(intent);
            finish();
        });

        btnCancel.setOnClickListener(v -> {
            finish();
        });
    }
}