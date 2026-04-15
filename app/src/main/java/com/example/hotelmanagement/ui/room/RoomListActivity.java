package com.example.hotelmanagement.ui.room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.RoomAdapter;
import com.example.hotelmanagement.model.Room;
import com.example.hotelmanagement.ui.booking.ConfirmBookingActivity;
import com.example.hotelmanagement.ui.profile.profile;
import com.example.hotelmanagement.ui.trips.MyTripsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {

    RecyclerView rvRooms;
    List<Room> list;
    RoomAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_list);

        rvRooms = findViewById(R.id.rvRooms);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();

        // 🔥 Adapter + click
        adapter = new RoomAdapter(list, new RoomAdapter.OnRoomClickListener() {
            @Override
            public void onBookNow(Room room) {

                Intent intent = new Intent(RoomListActivity.this, ConfirmBookingActivity.class);
                intent.putExtra("room", room); // truyền room
                startActivity(intent);
            }
        });

        rvRooms.setAdapter(adapter);

        // nhận hotel_id
        String hotelId = getIntent().getStringExtra("hotel_id");

        if (hotelId == null) {
            Toast.makeText(this, "Không có hotel_id", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("HOTEL_ID", hotelId);

        loadRooms(hotelId);

        // back
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_trips) {
                startActivity(new Intent(this, MyTripsActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, profile.class));
                return true;
            }
            return false;
        });
    }

    private void loadRooms(String hotelId) {
        db.collection("rooms")
                .whereEqualTo("hotel_id", hotelId)
                .get()
                .addOnSuccessListener(query -> {

                    list.clear();

                    for (DocumentSnapshot doc : query) {
                        Room r = doc.toObject(Room.class);

                        if (r != null) {
                            r.setId(doc.getId());
                            list.add(r);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi load phòng", Toast.LENGTH_SHORT).show();
                });
    }
}