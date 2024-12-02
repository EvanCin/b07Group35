package com.example.planetze35.userauthentication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planetze35.R;

public class WelcomeScreenActivity extends AppCompatActivity {

    private Button btnLogin, btnSignUp;
    private TextView tvMotto;
    private int index = 0;
    private String motto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignup);

        tvMotto = findViewById(R.id.tvMotto);
        motto = getString(R.string.motto);
        typeWriter();
        makeTextGradient();

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeScreenActivity.this, LoginActivityView.class);
            startActivity(intent);
            finish();
        });

        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeScreenActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void typeWriter() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < motto.length()) {
                    tvMotto.setText(motto.substring(0, index + 1));
                    index++;
                    handler.postDelayed(this, 50);
                }
            }
        }, 100);
    }

    private void makeTextGradient() {
        TextPaint paint = tvMotto.getPaint();
        float width = paint.measureText(motto);

        Shader textShader = new LinearGradient(0, 0, width, tvMotto.getTextSize(),
                new int[]{
                        Color.parseColor("#FF038C7A"),
                        Color.parseColor("#FF03DAC5"),      // teal_200
                        Color.parseColor("#FF036A54"),
                        Color.parseColor("#FF03AFA1"),
                }, null, Shader.TileMode.CLAMP);
        tvMotto.getPaint().setShader(textShader);
    }
}