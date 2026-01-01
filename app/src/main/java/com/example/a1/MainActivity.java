package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the Floating Action Button by its ID from your XML
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        // Set the click listener to open ReportActivity
        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, reportA.class);
            startActivity(intent);
        });
    }
}
