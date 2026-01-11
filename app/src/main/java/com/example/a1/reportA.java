package com.example.a1;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
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
    private RadioButton rbLost, rbFound;
    private Button btnSubmit;
    private ImageView ivItemImage;
    private Spinner spCategory;

    private FirebaseFirestore db;

    // Edit Mode Variables
    private boolean isEditMode = false;
    private String existingPostId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        db = FirebaseFirestore.getInstance();

        // Find Views
        etTitle = findViewById(R.id.etItemTitle);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        etSecurityQuestion = findViewById(R.id.etSecurityQuestion);
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer);
        etImageUrl = findViewById(R.id.etImageUrl);
        ivItemImage = findViewById(R.id.ivItemImage);
        rbLost = findViewById(R.id.rbLost);
        rbFound = findViewById(R.id.rbFound);
        btnSubmit = findViewById(R.id.btnSubmitReport);
        spCategory = findViewById(R.id.spCategory);

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        // Check if we are in Edit Mode
        if (getIntent().hasExtra("isEdit")) {
            isEditMode = getIntent().getBooleanExtra("isEdit", false);
            existingPostId = getIntent().getStringExtra("postId");
            setupEditMode();
        }

        // Image Preview Logic
        etImageUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadImage(s.toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmit.setOnClickListener(v -> saveItemToFirestore());
    }

    private void setupEditMode() {
        // Change UI for Edit Mode
        btnSubmit.setText("Update Report");
        setTitle("Edit Report");

        // Fill fields with existing data
        etTitle.setText(getIntent().getStringExtra("title"));
        etLocation.setText(getIntent().getStringExtra("location"));
        etDescription.setText(getIntent().getStringExtra("description"));
        etSecurityQuestion.setText(getIntent().getStringExtra("question"));
        etSecurityAnswer.setText(getIntent().getStringExtra("answer"));
        etImageUrl.setText(getIntent().getStringExtra("imageUrl"));

        String type = getIntent().getStringExtra("type");
        if ("LOST".equalsIgnoreCase(type)) rbLost.setChecked(true);
        else rbFound.setChecked(true);

        String category = getIntent().getStringExtra("category");
        if (category != null) {
            ArrayAdapter myAdap = (ArrayAdapter) spCategory.getAdapter();
            int spinnerPosition = myAdap.getPosition(category);
            spCategory.setSelection(spinnerPosition);
        }

        loadImage(etImageUrl.getText().toString());
    }

    private void loadImage(String url) {
        if (!url.isEmpty()) {
            Glide.with(reportA.this)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(android.R.drawable.ic_menu_camera)
                    .error(android.R.drawable.stat_notify_error)
                    .into(ivItemImage);
        }
    }

    private void saveItemToFirestore() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String question = etSecurityQuestion.getText().toString().trim();
        String answer = etSecurityAnswer.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String type = rbLost.isChecked() ? "LOST" : "FOUND";
        String category = spCategory.getSelectedItem().toString();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (title.isEmpty() || location.isEmpty() || question.isEmpty() || answer.isEmpty() || category.equals("Select Category")) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> item = new HashMap<>();
        item.put("title", title);
        item.put("location", location);
        item.put("description", description);
        item.put("type", type);
        item.put("category", category);
        item.put("question", question);
        item.put("answer", answer);
        item.put("userId", userId);
        item.put("imageUrl", imageUrl);
        item.put("timestamp", System.currentTimeMillis());

        if (isEditMode) {
            // UPDATE existing document
            db.collection("items").document(existingPostId)
                    .set(item)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            // ADD new document
            db.collection("items").add(item)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Posted successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}
