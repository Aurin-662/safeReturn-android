package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class loginA extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToSignup = findViewById(R.id.tvGoToSignup);

        // For now, clicking Login takes you to MainActivity (the feed)
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(loginA.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Clicking this takes you to Signup screen
        tvGoToSignup.setOnClickListener(v -> {
            Intent intent = new Intent(loginA.this, signupA.class);
            startActivity(intent);
        });
    }
}
