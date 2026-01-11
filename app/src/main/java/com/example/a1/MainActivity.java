package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Post> postList;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialize List and Adapter
        postList = new ArrayList<>();
        // Add one initial dummy item
       // postList.add(new Post("Example Item", "Library", "LOST"));

        RecyclerView rvItems = findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(postList);
        rvItems.setAdapter(adapter);

        // 2. Setup FAB to open reportA for a result
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, reportA.class);
            startActivityForResult(intent, 101); // 101 is a request code
        });
    }

    // 3. This method runs when you return from reportA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String location = data.getStringExtra("location");
            String type = data.getStringExtra("type");

            // Add the new data to our list
            postList.add(0, new Post(title, location, type));

            // Tell the adapter to refresh the screen
            adapter.notifyItemInserted(0);
            ((RecyclerView) findViewById(R.id.rvItems)).scrollToPosition(0);        }
    }
}
