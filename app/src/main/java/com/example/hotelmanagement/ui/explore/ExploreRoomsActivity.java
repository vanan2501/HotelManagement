package com.example.hotelmanagement.ui.explore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExploreRoomsActivity extends AppCompatActivity {

    private RecyclerView rvRooms;
    private RoomAdapter adapter;
    private List<Room> roomList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_rooms);

        db = FirebaseFirestore.getInstance();
        rvRooms = findViewById(R.id.rvRooms);
        rvRooms.setLayoutManager(new LinearLayoutManager(this));

        roomList = new ArrayList<>();
        adapter = new RoomAdapter(roomList, room -> {
            Intent intent = new Intent(this, ConfirmBookingActivity.class);
            intent.putExtra("room_data", room); // Passing the whole object
            startActivity(intent);
        });
        rvRooms.setAdapter(adapter);

        loadRoomsFromFirestore();

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

    private void loadRoomsFromFirestore() {
        db.collection("rooms")
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Toast.makeText(this,
                                "Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    roomList.clear();

                    for (QueryDocumentSnapshot document : value) {

                        Room room = document.toObject(Room.class);
                        room.setId(document.getId());
                        roomList.add(room);
                    }

                    adapter.notifyDataSetChanged();

                    if (roomList.isEmpty()) {
                        Toast.makeText(this,
                                "No rooms found in Firestore",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
