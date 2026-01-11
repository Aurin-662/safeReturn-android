package com.example.a1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class reportA extends AppCompatActivity {

    private EditText etTitle, etLocation, etDescription, etSecurityQuestion, etSecurityAnswer;
    private RadioButton rbLost;
    private Button btnSubmit;

    // Firebase Firestore-এর অবজেক্ট ডিক্লেয়ার করা
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // ডাটাবেজ ইনিশিয়ালাইজ করা
        db = FirebaseFirestore.getInstance();

        // ভিউগুলো খুঁজে বের করা
        etTitle = findViewById(R.id.etItemTitle);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);

        // --- NEW: Security Question/Answer Views ---
        etSecurityQuestion = findViewById(R.id.etSecurityQuestion);
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer);

        rbLost = findViewById(R.id.rbLost);
        btnSubmit = findViewById(R.id.btnSubmitReport);

        btnSubmit.setOnClickListener(v -> {
            saveItemToFirestore();
        });
    }

    private void saveItemToFirestore() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String question = etSecurityQuestion.getText().toString().trim();
        String answer = etSecurityAnswer.getText().toString().trim();
        String type = rbLost.isChecked() ? "LOST" : "FOUND";

        // Use unique User ID (UID) instead of just email for better linking
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Validation: All fields are required for security
        if (title.isEmpty() || location.isEmpty() || question.isEmpty() || answer.isEmpty()) {
            Toast.makeText(this, "Title, Location, Question and Answer are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // ডাটাবেজে সেভ করার জন্য একটি ম্যাপ (Map) তৈরি করা
        Map<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("location", location);
        item.put("description", description);
        item.put("type", type);
        item.put("question", question); // Security Question
        item.put("answer", answer);     // Secret Answer
        item.put("userId", userId);     // Link to the user who posted it
        item.put("timestamp", System.currentTimeMillis());

        // Firestore-এ 'items' নামে একটি কালেকশনে ডাটা সেভ করা
        db.collection("items")
                .add(item)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(reportA.this, "Item Posted Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // সফলভাবে সেভ হলে পেজটি বন্ধ হয়ে যাবে
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(reportA.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
