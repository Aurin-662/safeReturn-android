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
    private String currentTypeFilter = "ALL";
    private SearchView searchView; // ক্লাস ভেরিয়েবল হিসেবে ডিক্লেয়ার করলাম

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ১. সাবস্ক্রাইব (super.onCreate এর পর দেওয়া ভালো)
        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("all_items");

        // ২. টুলবার সেটআপ
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ৩. ফায়ারবেস ও লিস্ট ইনিশিয়ালাইজ
        db = FirebaseFirestore.getInstance();
        postList = new ArrayList<>();
        RecyclerView rvItems = findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(postList);
        rvItems.setAdapter(adapter);

        // ৪. ডাটা লোড
        loadDataFromFirestore();

        // ৫. সার্চ বার লজিক
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters(newText, currentTypeFilter);
                return true;
            }
        });

        // ৬. চিপ ফিল্টার লজিক
        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipLost)) {
                currentTypeFilter = "LOST";
            } else if (checkedIds.contains(R.id.chipFound)) {
                currentTypeFilter = "FOUND";
            } else if (checkedIds.contains(R.id.chipFree)) {
                currentTypeFilter = "FREE";
            } else {
                currentTypeFilter = "ALL";
            }
            applyFilters(searchView.getQuery().toString(), currentTypeFilter);
        });

        // ৭. অ্যাড বাটন
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, reportA.class)));
    }

    private void applyFilters(String searchText, String typeFilter) {
        List<Post> filteredList = new ArrayList<>();

        // টেস্ট করার জন্য ০ দিলাম (যাতে সাথে সাথে ফ্রি সেকশনে দেখায়)
        // পরে এটি পরিবর্তন করে ১ বা ২ মিনিট (2L * 60 * 1000) করে দিবেন
        long thresholdInMillis = 0L;
        long currentTime = System.currentTimeMillis();

        for (Post item : postList) {
            boolean matchesSearch = item.getTitle().toLowerCase().contains(searchText.toLowerCase());
            boolean matchesChip = false;

            if (typeFilter.equals("ALL")) {
                matchesChip = true;
            } else if (typeFilter.equals("LOST")) {
                matchesChip = item.getType().equalsIgnoreCase("LOST");
            } else if (typeFilter.equals("FOUND")) {
                // শুধুমাত্র নতুন পাওয়া আইটেমগুলো (২ মিনিটের কম)
                matchesChip = item.getType().equalsIgnoreCase("FOUND") && (currentTime - item.getTimestamp() <= thresholdInMillis);
            } else if (typeFilter.equals("FREE")) {
                // শুধুমাত্র পুরনো পাওয়া আইটেমগুলো (২ মিনিটের বেশি)
                matchesChip = item.getType().equalsIgnoreCase("FOUND") && (currentTime - item.getTimestamp() > thresholdInMillis);
            }

            if (matchesSearch && matchesChip) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    private void loadDataFromFirestore() {
        db.collection("items")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    if (value != null) {
                        postList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            long ts = doc.getLong("timestamp") != null ? doc.getLong("timestamp") : 0;

                            postList.add(new Post(
                                    doc.getString("title"),
                                    doc.getString("location"),
                                    doc.getString("description"),
                                    doc.getString("type"),
                                    doc.getString("question"),
                                    doc.getString("answer"),
                                    doc.getString("userId"),
                                    doc.getString("imageUrl"),
                                    doc.getString("category"),
                                    doc.getId(),
                                    ts
                            ));
                        }
                        adapter.notifyDataSetChanged();
                        if (searchView != null) {
                            applyFilters(searchView.getQuery().toString(), currentTypeFilter);
                        }
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
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return true;
        } else if (id == R.id.action_responses) {
            startActivity(new Intent(MainActivity.this, ResponsesActivity.class));
            return true;
        } else if (id == R.id.action_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
