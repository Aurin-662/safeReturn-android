package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello);

        // IDs must match the XML exactly (Case Sensitive)
        Button btnLogin = findViewById(R.id.btnWelcomeLogin);
        Button btnSignup = findViewById(R.id.btnWelcomeSignup);

        // Navigate to Login page
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, loginA.class);
            startActivity(intent);
        });

        // Navigate to Signup page
        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, signupA.class);
            startActivity(intent);
        });
    }
}
