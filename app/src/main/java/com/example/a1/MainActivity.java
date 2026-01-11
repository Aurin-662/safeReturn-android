package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Post> postList;
    private PostAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ১. ডাটাবেজ ইনিশিয়ালাইজ করা
        db = FirebaseFirestore.getInstance();

        // ২. লিস্ট এবং অ্যাডাপ্টার সেটআপ
        postList = new ArrayList<>();
        RecyclerView rvItems = findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(postList);
        rvItems.setAdapter(adapter);

        // ৩. Firestore থেকে রিয়েল-টাইম ডাটা লোড করা
        loadDataFromFirestore();

        // ৪. FAB সেটআপ (আইটেম রিপোর্ট করার জন্য)
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, reportA.class);
            startActivity(intent); // এখন আর ForResult দরকার নেই, কারণ ডাটাবেজ থেকে অটো আসবে
        });
    }

    private void loadDataFromFirestore() {
        // 'items' কালেকশন থেকে সময় অনুযায়ী (Timestamp) ডাটা আনা
        db.collection("items")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, "Error loading: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        postList.clear(); // পুরনো লিস্ট ক্লিয়ার করা যাতে ডুপ্লিকেট না হয়
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            // ডাটাবেজের ফিল্ডের সাথে মিলিয়ে ডাটা পড়া
                            String title = doc.getString("title");
                            String location = doc.getString("location");
                            String type = doc.getString("type");

                            postList.add(new Post(title, location, type));
                        }
                        adapter.notifyDataSetChanged(); // UI আপডেট করা
                    }
                });
    }


}
