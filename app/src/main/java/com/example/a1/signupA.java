package com.example.a1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class signupA extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Finding all views
        EditText etFirstName = findViewById(R.id.etFirstName);
        EditText etLastName = findViewById(R.id.etLastName);
        EditText etRoll = findViewById(R.id.etRoll);
        EditText etPhone = findViewById(R.id.etPhone);
        EditText etEmail = findViewById(R.id.etEmailSignup);
        EditText etPassword = findViewById(R.id.etPasswordSignup);
        Button btnSignup = findViewById(R.id.btnSignup);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnSignup.setOnClickListener(v -> {
            // Get text from all fields
            String fName = etFirstName.getText().toString().trim();
            String lName = etLastName.getText().toString().trim();
            String roll = etRoll.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            // Validation logic
            if (fName.isEmpty() || lName.isEmpty() || roll.isEmpty() ||
                    phone.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (pass.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Save this data to Firebase
                Toast.makeText(this, "Registering " + fName + "...", Toast.LENGTH_SHORT).show();
                // For now, return to login after success
                finish();
            }
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }
}
