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

public class activity_details extends AppCompatActivity {

    private String title, location, type, question, answer, posterUserId, imageUrl, postId, category, description;
    private TextView tvTitle, tvType, tvDescription, tvPostedBy;
    private ImageView ivDetailImage;
    private Button btnAction, btnDelete, btnEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // 1. Receive data from Intent (Passed from PostAdapter)
        title = getIntent().getStringExtra("title");
        location = getIntent().getStringExtra("location");
        type = getIntent().getStringExtra("type");
        question = getIntent().getStringExtra("question");
        answer = getIntent().getStringExtra("answer");
        posterUserId = getIntent().getStringExtra("userId");
        imageUrl = getIntent().getStringExtra("imageUrl");
        postId = getIntent().getStringExtra("postId");
        category = getIntent().getStringExtra("category");
        description = getIntent().getStringExtra("description");

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
        tvType.setText(type + " | " + (category != null ? category : "General"));
        tvDescription.setText("Location: " + location + "\n\nVerify identity to see contact info.");

        // Load Image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).placeholder(android.R.drawable.ic_menu_gallery).into(ivDetailImage);
        }

        // 4. Ownership Logic: Show Edit/Delete only for the creator
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId.equals(posterUserId)) {
            btnEdit.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnAction.setVisibility(View.GONE); // Owner doesn't need to verify themselves
            fetchPosterName(true);
        } else {
            btnEdit.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnAction.setVisibility(View.VISIBLE);
            fetchPosterName(false);
        }

        // 5. Button Listeners
        btnAction.setOnClickListener(v -> showVerificationDialog());

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

        // posterUserId ব্যবহার করে users কালেকশন থেকে নাম আনা
        FirebaseFirestore.getInstance().collection("users").document(posterUserId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        // নিশ্চিত করুন যে আপনার Firestore-এ ফিল্ডের নাম 'firstName' ই আছে
                        String name = doc.getString("firstName");

                        if (name != null && !name.isEmpty()) {
                            tvPostedBy.setText("Posted by: " + name);
                        } else {
                            tvPostedBy.setText("Posted by: Unknown User");
                        }
                    } else {
                        // যদি ইউজার প্রোফাইল না পাওয়া যায় (পুরনো ডাটা হলে এমন হতে পারে)
                        tvPostedBy.setText("Posted by: Registered Student");
                    }
                })
                .addOnFailureListener(e -> {
                    tvPostedBy.setText("Posted by: Error loading name");
                });
    }


    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Post")
                .setMessage("Are you sure? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    FirebaseFirestore.getInstance().collection("items").document(postId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Post removed", Toast.LENGTH_SHORT).show();
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
                        tvDescription.setText("Verified Contact: " + phone);
                        btnAction.setText("Call Now");
                        btnAction.setOnClickListener(v -> {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phone));
                            startActivity(intent);
                        });
                    }
                });
    }
}
