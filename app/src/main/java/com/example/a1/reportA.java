package com.example.a1;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class reportA extends AppCompatActivity {

    private EditText etTitle, etLocation, etDescription, etSecurityQuestion, etSecurityAnswer, etImageUrl;
    private RadioButton rbLost;
    private Button btnSubmit;
    private ImageView ivItemImage;

    // Firebase Firestore object
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // ১. ডাটাবেজ ইনিশিয়ালাইজ করা
        db = FirebaseFirestore.getInstance();

        // ২. ভিউগুলো খুঁজে বের করা
        etTitle = findViewById(R.id.etItemTitle);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        etSecurityQuestion = findViewById(R.id.etSecurityQuestion);
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer);
        etImageUrl = findViewById(R.id.etImageUrl);
        ivItemImage = findViewById(R.id.ivItemImage);

        rbLost = findViewById(R.id.rbLost);
        btnSubmit = findViewById(R.id.btnSubmitReport);

        // --- ইমেজ প্রিভিউ লজিক (Updated with better Error Handling) ---
        etImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                if (!url.isEmpty()) {
                    // Glide ব্যবহার করে ইমেজ লোড করা
                    Glide.with(reportA.this)
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.ALL) // ক্যাশ মেমোরি ব্যবহার করবে দ্রুত লোড হওয়ার জন্য
                            .placeholder(android.R.drawable.ic_menu_camera)
                            .error(android.R.drawable.stat_notify_error)
                            .into(ivItemImage);
                } else {
                    ivItemImage.setImageResource(android.R.drawable.ic_menu_camera);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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
        String imageUrl = etImageUrl.getText().toString().trim();
        String type = rbLost.isChecked() ? "LOST" : "FOUND";

        // ইউজার লগইন আছে কিনা চেক করা
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // ভ্যালিডেশন
        if (title.isEmpty() || location.isEmpty() || question.isEmpty() || answer.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // ডাটাবেজে সেভ করার জন্য ম্যাপ তৈরি করা
        Map<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("location", location);
        item.put("description", description);
        item.put("type", type);
        item.put("question", question);
        item.put("answer", answer);
        item.put("userId", userId);
        item.put("imageUrl", imageUrl);
        item.put("timestamp", System.currentTimeMillis());


        db.collection("items")
                .add(item)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(reportA.this, "Item Posted Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(reportA.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
