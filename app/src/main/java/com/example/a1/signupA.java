package com.example.a1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth; // Import this

public class signupA extends AppCompatActivity {
    private FirebaseAuth mAuth; // Declaration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        mAuth = FirebaseAuth.getInstance(); // Initialize

        EditText etEmail = findViewById(R.id.etEmailSignup);
        EditText etPassword = findViewById(R.id.etPasswordSignup);
        Button btnSignup = findViewById(R.id.btnSignup);

        btnSignup.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.length() < 6) {
                Toast.makeText(this, "Valid email and 6-char password required", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- FIREBASE CREATE USER ---
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(signupA.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to Login
                        } else {
                            Toast.makeText(signupA.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
