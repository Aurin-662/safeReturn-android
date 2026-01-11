package com.example.a1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class ResponseAdapter extends BaseAdapter {
    private Context context;
    private List<ResponseModel> list;

    public ResponseAdapter(Context context, List<ResponseModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() { return list.size(); }
    @Override
    public Object getItem(int i) { return list.get(i); }
    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_response, viewGroup, false);
        }

        ResponseModel model = list.get(i);

        TextView tvMessage = view.findViewById(R.id.tvResponseMessage);
        TextView tvPhone = view.findViewById(R.id.tvResponsePhone); // Find the new TextView
        Button btnCall = view.findViewById(R.id.btnCallResponder);
        Button btnDelete = view.findViewById(R.id.btnDeleteResponse);

        // Set the message and the phone number text
        tvMessage.setText(model.getDisplayMessage());
        tvPhone.setText("Contact: " + model.getResponderPhone());

        // --- CALL BUTTON LOGIC ---
        btnCall.setOnClickListener(v -> {
            String phoneNumber = model.getResponderPhone();
            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        // --- DELETE BUTTON LOGIC ---
        btnDelete.setOnClickListener(v -> {
            FirebaseFirestore.getInstance().collection("responses").document(model.getResponseId())
                    .delete()
                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Notification removed", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        return view;
    }
}
