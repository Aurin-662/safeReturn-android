package com.example.a1;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class activity_details extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Receive data from PostAdapter
        String title = getIntent().getStringExtra("title");
        String location = getIntent().getStringExtra("location");
        String type = getIntent().getStringExtra("type");

        // Link to your XML IDs
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvType = findViewById(R.id.tvDetailType);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);

        // Set the content
        if (title != null) tvTitle.setText(title);
        if (type != null) tvType.setText(type);
        if (location != null) {
            tvDescription.setText("This item was reported at " + location + ". If you are the owner or have found this item, please take further action.");
        }
    }
}
