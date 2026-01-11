package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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
        // Subscribe to notifications topic
        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("all_items").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // success subscription
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Campus Lost & Found");
        }

        // 2. Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // 3. Setup List and Adapter
        postList = new ArrayList<>();
        RecyclerView rvItems = findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(postList);
        rvItems.setAdapter(adapter);

        // 4. Load Live Data from Firestore
        loadDataFromFirestore();

        // 5. Setup FAB (Report Activity)
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, reportA.class);
            startActivity(intent);
        });

        // 6. Setup Search Bar
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterByTitle(newText);
                return true;
            }
        });

        // 7. Setup LOST/FOUND Filter Chips
        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipLost)) {
                filterByType("LOST");
            } else if (checkedIds.contains(R.id.chipFound)) {
                filterByType("FOUND");
            } else {
                adapter.filterList(postList); // Show All
            }
        });
    }

    private void filterByTitle(String text) {
        List<Post> filteredList = new ArrayList<>();
        for (Post item : postList) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void filterByType(String type) {
        List<Post> filteredList = new ArrayList<>();
        for (Post item : postList) {
            if (item.getType().equalsIgnoreCase(type)) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void loadDataFromFirestore() {
        db.collection("items")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivity.this, "Error loading: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        postList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            String title = doc.getString("title");
                            String location = doc.getString("location");
                            String description = doc.getString("description");
                            String type = doc.getString("type");
                            String question = doc.getString("question");
                            String answer = doc.getString("answer");
                            String userId = doc.getString("userId");
                            String imageUrl = doc.getString("imageUrl");
                            String category = doc.getString("category");
                            String postId = doc.getId();

                            postList.add(new Post(title, location, description, type, question, answer, userId, imageUrl, category, postId));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        else if (id == R.id.action_responses) {
            Intent intent = new Intent(MainActivity.this, ResponsesActivity.class);
            startActivity(intent);
            return true;
        }

        // NEW: Navigate to Profile screen
        else if (id == R.id.action_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
