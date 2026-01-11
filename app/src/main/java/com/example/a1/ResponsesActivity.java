package com.example.a1;

import android.os.Bundle;
import android.widget.ListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responses);

        lvResponses = findViewById(R.id.lvResponses);
        responseList = new ArrayList<>();
        adapter = new ResponseAdapter(this, responseList);
        lvResponses.setAdapter(adapter);

        loadResponses();
    }

    private void loadResponses() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("responses")
                .whereEqualTo("posterId", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        responseList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            ResponseModel res = new ResponseModel(
                                    doc.getId(), // Fetching the Document ID for deletion
                                    doc.getString("responderName"),
                                    doc.getString("responderPhone"),
                                    doc.getString("itemName"),
                                    doc.getString("type")
                            );
                            responseList.add(res);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
