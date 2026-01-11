package com.example.a1;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ResponsesActivity extends AppCompatActivity {

    private ListView lvResponses;
    private List<ResponseModel> responseList;
    private ResponseAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses);

        // Enable Back Button in Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Notifications & Responses");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        lvResponses = findViewById(R.id.lvResponses);
        responseList = new ArrayList<>();

        // Use the custom ResponseAdapter we created earlier
        adapter = new ResponseAdapter(this, responseList);
        lvResponses.setAdapter(adapter);

        loadResponses();
    }

    private void loadResponses() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Listen for responses where I am the poster (User1)
        db.collection("responses")
                .whereEqualTo("posterId", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        responseList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            // Fetch all fields including Document ID for deletion
                            ResponseModel res = new ResponseModel(
                                    doc.getId(),
                                    doc.getString("responderName"),
                                    doc.getString("responderPhone"),
                                    doc.getString("itemName"),
                                    doc.getString("type")
                            );
                            responseList.add(res);
                        }

                        // Update the UI
                        adapter.notifyDataSetChanged();

                        // Optional: Show message if list is empty
                        if (responseList.isEmpty()) {
                            Toast.makeText(ResponsesActivity.this, "No new responses.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Handle Toolbar Back Button click
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
