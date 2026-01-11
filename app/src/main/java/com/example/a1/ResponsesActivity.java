package com.example.a1;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ResponsesActivity extends AppCompatActivity {

    private ListView lvResponses;
    private List<String> responseMessages;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses);

        // টাইটেল সেট করা
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Responses");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lvResponses = findViewById(R.id.lvResponses);
        responseMessages = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, responseMessages);
        lvResponses.setAdapter(adapter);

        loadResponses();
    }

    private void loadResponses() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // শুধুমাত্র আমার পোস্টের (posterId) রেসপন্সগুলো আনবে
        FirebaseFirestore.getInstance().collection("responses")
                .whereEqualTo("posterId", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        responseMessages.clear();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            ResponseModel res = new ResponseModel(            doc.getString("responderName"),
                                    doc.getString("responderPhone"), // নতুন ফিল্ড
                                    doc.getString("itemName"),
                                    doc.getString("type")
                            );
                            responseMessages.add(res.getDisplayMessage());
                        }


                        if (responseMessages.isEmpty()) {
                            responseMessages.add("No responses yet.");
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
