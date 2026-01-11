package com.example.a1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore; // Import Firestore

import java.util.HashMap;
import java.util.Map;

public class signupA extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Declare Firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        EditText etFirstName = findViewById(R.id.etFirstName);
        EditText etLastName = findViewById(R.id.etLastName);
        EditText etRoll = findViewById(R.id.etRoll);
        EditText etPhone = findViewById(R.id.etPhone);
        EditText etEmail = findViewById(R.id.etEmailSignup);
        EditText etPassword = findViewById(R.id.etPasswordSignup);

        Button btnSignup = findViewById(R.id.btnSignup);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnSignup.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String fName = etFirstName.getText().toString().trim();
            String lName = etLastName.getText().toString().trim();
            String roll = etRoll.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (email.isEmpty() || password.length() < 6 || fName.isEmpty() || lName.isEmpty() || roll.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Create User in Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // 2. Auth Success! Now save extra info to Firestore
                            String userId = mAuth.getCurrentUser().getUid(); // Get unique User ID

                            Map<String, Object> user = new HashMap<>();
                            user.put("firstName", fName);
                            user.put("lastName", lName);
                            user.put("roll", roll);
                            user.put("phone", phone);
                            user.put("email", email);

                            db.collection("users").document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(signupA.this, "Profile Created Successfully!", Toast.LENGTH_SHORT).show();
                                        finish(); // Go to Login
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(signupA.this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(signupA.this, "Auth Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }
}
