package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;

public class reportA extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        EditText etTitle = findViewById(R.id.etItemTitle);
        EditText etLocation = findViewById(R.id.etLocation);
        RadioButton rbLost = findViewById(R.id.rbLost);
        Button btnSubmit = findViewById(R.id.btnSubmitReport);

        btnSubmit.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String location = etLocation.getText().toString();
            String type = rbLost.isChecked() ? "LOST" : "FOUND";

            // Create an Intent to carry the data back
            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("location", location);
            resultIntent.putExtra("type", type);

            setResult(RESULT_OK, resultIntent);
            finish(); // Close this activity
        });
    }
}
