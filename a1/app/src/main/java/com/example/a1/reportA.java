package com.example.a1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class reportA extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This links to the activity_report.xml you created earlier
        setContentView(R.layout.activity_report);

        Button btnSubmit = findViewById(R.id.btnSubmitReport);

        btnSubmit.setOnClickListener(v -> {
            // For now, just show a message and go back
            Toast.makeText(this, "Report Submitted (Design Only)", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
