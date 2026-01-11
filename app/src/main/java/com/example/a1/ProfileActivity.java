package com.example.a1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText etFName, etLName, etRoll, etPhone;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        etFName = findViewById(R.id.etProfileFirstName);
        etLName = findViewById(R.id.etProfileLastName);
        etRoll = findViewById(R.id.etProfileRoll);
        etPhone = findViewById(R.id.etProfilePhone);
        Button btnUpdate = findViewById(R.id.btnUpdateProfile);

        loadUserData();

        btnUpdate.setOnClickListener(v -> updateUserData());
    }

    private void loadUserData() {
        db.collection("users").document(userId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                etFName.setText(doc.getString("firstName"));
                etLName.setText(doc.getString("lastName"));
                etRoll.setText(doc.getString("roll"));
                etPhone.setText(doc.getString("phone"));
            }
        });
    }

    private void updateUserData() {
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", etFName.getText().toString().trim());
        user.put("lastName", etLName.getText().toString().trim());
        user.put("roll", etRoll.getText().toString().trim());
        user.put("phone", etPhone.getText().toString().trim());

        db.collection("users").document(userId).update(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show());
    }
}
