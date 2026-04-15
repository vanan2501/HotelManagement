package com.example.hotelmanagement.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.RoomAdapter;
import com.example.hotelmanagement.model.Room;
import com.example.hotelmanagement.ui.room.RoomActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    TextView txtUser, txtRoom;
    Button btnUser, btnRoom;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        txtUser = findViewById(R.id.txtUser);
        txtRoom = findViewById(R.id.txtRoom);
        btnUser = findViewById(R.id.btnUser);
        btnRoom = findViewById(R.id.btnRoom);

        db = FirebaseFirestore.getInstance();

        loadStats();


        btnRoom.setOnClickListener(v ->
                startActivity(new Intent(this, RoomActivity.class)));
    }

    private void loadStats() {
        db.collection("rooms").get()
                .addOnSuccessListener(task ->
                        txtRoom.setText("Rooms: " + task.size()));
    }
}
