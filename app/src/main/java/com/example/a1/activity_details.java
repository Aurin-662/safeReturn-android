package com.example.a1;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Required for loading images
import com.google.firebase.firestore.FirebaseFirestore;

public class activity_details extends AppCompatActivity {

    private String title, location, type, question, answer, posterUserId, imageUrl;
    private TextView tvTitle, tvType, tvDescription;
    private ImageView ivDetailImage;
    private Button btnAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // 1. Receive data from PostAdapter
        title = getIntent().getStringExtra("title");
        location = getIntent().getStringExtra("location");
        type = getIntent().getStringExtra("type");
        question = getIntent().getStringExtra("question");
        answer = getIntent().getStringExtra("answer");
        posterUserId = getIntent().getStringExtra("userId");
        imageUrl = getIntent().getStringExtra("imageUrl"); // NEW: Get image URL

        // 2. Link to XML IDs
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvType = findViewById(R.id.tvDetailType);
        tvDescription = findViewById(R.id.tvDetailDescription);
        ivDetailImage = findViewById(R.id.ivDetailImage); // NEW: Image Link
        btnAction = findViewById(R.id.btnCallUser);

        // 3. Set basic content
        if (title != null) tvTitle.setText(title);
        if (type != null) tvType.setText(type);
        tvDescription.setText("Location: " + location + "\n\nYou must verify your identity to see contact details.");

        // 4. Load Image using Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery) // Show while loading
                    .error(android.R.drawable.stat_notify_error)      // Show if link fails
                    .into(ivDetailImage);
        }

        // 5. Change button text based on Type (LOST/FOUND)
        if ("LOST".equalsIgnoreCase(type)) {
            btnAction.setText("I Found It!");
        } else {
            btnAction.setText("Claim Item");
        }

        // 6. Set Click Listener for Verification
        btnAction.setOnClickListener(v -> {
            showVerificationDialog();
        });
    }

    private void showVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Security Verification");
        builder.setMessage("The reporter asked: " + question);

        // Create an input field for the answer
        final EditText input = new EditText(this);
        input.setHint("Type your answer here...");
        builder.setView(input);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String userResponse = input.getText().toString().trim();

            if (userResponse.equalsIgnoreCase(answer)) {
                Toast.makeText(this, "Verified Successfully!", Toast.LENGTH_SHORT).show();
                fetchPosterContactInfo();
            } else {
                Toast.makeText(this, "Wrong Answer. Try again.", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void fetchPosterContactInfo() {
        // Fetch the phone number and name from the 'users' collection
        FirebaseFirestore.getInstance().collection("users").document(posterUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String phone = documentSnapshot.getString("phone");
                        String name = documentSnapshot.getString("firstName") + " " + documentSnapshot.getString("lastName");

                        // Update Description
                        tvDescription.setText("Verified Owner/Finder: " + name + "\nPhone: " + phone);

                        // Change Button to actually Call
                        btnAction.setText("Call " + name);
                        btnAction.setOnClickListener(v -> {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + phone));
                            startActivity(callIntent);
                        });
                    } else {
                        Toast.makeText(this, "User profile not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching contact: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
