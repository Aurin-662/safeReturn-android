package com.example.a1;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class activity_details extends AppCompatActivity {

    private String title, location, type, question, answer, posterUserId, imageUrl, postId, category, description;
    private long timestamp;
    private TextView tvTitle, tvType, tvDescription, tvPostedBy;
    private ImageView ivDetailImage;
    private Button btnAction, btnDelete, btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // 1. Receive data from Intent
        title = getIntent().getStringExtra("title");
        location = getIntent().getStringExtra("location");
        type = getIntent().getStringExtra("type");
        question = getIntent().getStringExtra("question");
        answer = getIntent().getStringExtra("answer");
        posterUserId = getIntent().getStringExtra("userId");
        imageUrl = getIntent().getStringExtra("imageUrl");
        postId = getIntent().getStringExtra("postId");
        category = getIntent().getStringExtra("category");
        description = getIntent().getStringExtra("description"); // Critical for display
        timestamp = getIntent().getLongExtra("timestamp", 0);

        // 2. Initialize Views
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvType = findViewById(R.id.tvDetailType);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvPostedBy = findViewById(R.id.tvPostedBy);
        ivDetailImage = findViewById(R.id.ivDetailImage);
        btnAction = findViewById(R.id.btnCallUser);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        // 3. Set UI Content
        tvTitle.setText(title);

        // --- FREE SECTION LOGIC ---
        long sixtyDaysInMillis = 60L * 24 * 60 * 60 * 1000;
        boolean isFreeItem = "FOUND".equalsIgnoreCase(type) && (System.currentTimeMillis() - timestamp > sixtyDaysInMillis);

        if (isFreeItem) {
            tvType.setText("FREE SECTION (Unclaimed Item)");
            tvType.setTextColor(android.graphics.Color.parseColor("#FF9800")); // Orange
            btnAction.setText("Claim for Free");
        } else {
            String categoryText = (category != null && !category.isEmpty()) ? " | " + category : "";
            tvType.setText(type + categoryText);
            if ("LOST".equalsIgnoreCase(type)) {
                btnAction.setText("I Found It!");
            } else {
                btnAction.setText("Claim Item");
            }
        }

        // --- UPDATED DESCRIPTION DISPLAY ---
        StringBuilder fullDescription = new StringBuilder();
        fullDescription.append("📍 Location: ").append(location != null ? location : "Not specified");
        fullDescription.append("\n\n📝 Details:\n");
        if (description != null && !description.isEmpty()) {
            fullDescription.append(description);
        } else {
            fullDescription.append("No additional description provided.");
        }
        tvDescription.setText(fullDescription.toString());

        // Load Image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).placeholder(android.R.drawable.ic_menu_gallery).into(ivDetailImage);
        }

        // 4. Ownership Logic
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId.equals(posterUserId)) {
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnAction.setVisibility(View.GONE);
            fetchPosterName(true);
        } else {
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnAction.setVisibility(View.VISIBLE);
            fetchPosterName(false);
        }

        // 5. Button Listeners
        btnAction.setOnClickListener(v -> {
            if (isFreeItem) {
                new AlertDialog.Builder(this)
                        .setTitle("Free Claim")
                        .setMessage("This item is now free to take. Reveal contact info?")
                        .setPositiveButton("Yes", (dialog, which) -> revealContact())
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                showVerificationDialog();
            }
        });

        btnDelete.setOnClickListener(v -> confirmDelete());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(activity_details.this, reportA.class);
            intent.putExtra("isEdit", true);
            intent.putExtra("postId", postId);
            intent.putExtra("title", title);
            intent.putExtra("location", location);
            intent.putExtra("description", description);
            intent.putExtra("imageUrl", imageUrl);
            intent.putExtra("category", category);
            intent.putExtra("question", question);
            intent.putExtra("answer", answer);
            intent.putExtra("type", type);
            startActivity(intent);
        });
    }

    private void fetchPosterName(boolean isOwner) {
        if (isOwner) {
            tvPostedBy.setText("Posted by: You");
            return;
        }
        FirebaseFirestore.getInstance().collection("users").document(posterUserId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("firstName");
                        tvPostedBy.setText("Posted by: " + (name != null ? name : "Registered User"));
                    } else {
                        tvPostedBy.setText("Posted by: User");
                    }
                })
                .addOnFailureListener(e -> tvPostedBy.setText("Posted by: Unknown"));
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Post")
                .setMessage("Are you sure? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    FirebaseFirestore.getInstance().collection("items").document(postId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showVerificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Security Check");
        builder.setMessage("The reporter asks: " + question);

        final EditText input = new EditText(this);
        input.setHint("Answer here...");
        builder.setView(input);

        builder.setPositiveButton("Verify", (dialog, which) -> {
            String userResponse = input.getText().toString().trim();
            if (userResponse.equalsIgnoreCase(answer)) {
                revealContact();
            } else {
                Toast.makeText(this, "Wrong answer!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void revealContact() {
        FirebaseFirestore.getInstance().collection("users").document(posterUserId)
                .get().addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String phone = doc.getString("phone");
                        String name = doc.getString("firstName");
                        tvDescription.setText("✅ Verified Contact\nName: " + name + "\nPhone: " + phone);
                        saveResponseToFirestore();

                        btnAction.setText("Call Now");
                        btnAction.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phone));
                            startActivity(intent);
                        });
                    }
                });
    }

    private void saveResponseToFirestore() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users").document(currentUserId)
                .get().addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        String responderName = userDoc.getString("firstName");
                        String responderPhone = userDoc.getString("phone");

                        Map<String, Object> response = new HashMap<>();
                        response.put("posterId", posterUserId);
                        response.put("responderName", responderName != null ? responderName : "Unknown User");
                        response.put("responderPhone", responderPhone != null ? responderPhone : "No Phone");
                        response.put("itemName", title);
                        response.put("type", type);
                        response.put("timestamp", System.currentTimeMillis());

                        FirebaseFirestore.getInstance().collection("responses").add(response);
                    }
                });
    }
}
