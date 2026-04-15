package com.example.hotelmanagement.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.HotelAdapter;
import com.example.hotelmanagement.model.Hotel;
import com.example.hotelmanagement.ui.hotel.HotelDetailActivity;
import com.example.hotelmanagement.ui.hotel.HotelListActivity;
import com.example.hotelmanagement.ui.profile.profile;
import com.example.hotelmanagement.ui.trips.MyTripsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String HOTEL_COLLECTION = "hotel";

    private RecyclerView rcvMostVisited;
    private RecyclerView rcvHotel;
    private final List<Hotel> topRatedHotels = new ArrayList<>();
    private final List<Hotel> nearbyHotels = new ArrayList<>();
    private HotelAdapter mostVisitedAdapter;
    private HotelAdapter nearbyAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rcvMostVisited = findViewById(R.id.rcvMostVisited);
        rcvHotel = findViewById(R.id.rcvHotel);
        TextView txtSeeAllMostVisited = findViewById(R.id.txtSeeAllMostVisited);
        TextView txtSeeAllNearby = findViewById(R.id.txtSeeAllNearby);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        rcvMostVisited.setLayoutManager(new LinearLayoutManager(this));
        rcvHotel.setLayoutManager(new LinearLayoutManager(this));
        rcvMostVisited.setNestedScrollingEnabled(false);
        rcvHotel.setNestedScrollingEnabled(false);

        mostVisitedAdapter = new HotelAdapter(this, topRatedHotels, this::openHotelDetail);
        nearbyAdapter = new HotelAdapter(this, nearbyHotels, this::openHotelDetail);
        rcvMostVisited.setAdapter(mostVisitedAdapter);
        rcvHotel.setAdapter(nearbyAdapter);

        txtSeeAllMostVisited.setOnClickListener(v -> openSeeAllHotels());
        txtSeeAllNearby.setOnClickListener(v -> openSeeAllHotels());

        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true; // đang ở home
            }
            else if (id == R.id.nav_trips) {
                startActivity(new Intent(this, MyTripsActivity.class));
                return true;
            }
            else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, profile.class));
                return true;
            }

            return false;
        });

        db = FirebaseFirestore.getInstance();
        loadHotels();
    }

    private void loadHotels() {
        db.collection(HOTEL_COLLECTION)
                .get()
                .addOnSuccessListener(this::applyHotelsSnapshot)
                .addOnFailureListener(e -> clearLists());
    }

    private void applyHotelsSnapshot(Iterable<? extends DocumentSnapshot> snapshots) {
        List<Hotel> allHotels = new ArrayList<>();

        for (DocumentSnapshot doc : snapshots) {
            Hotel hotel = doc.toObject(Hotel.class);
            if (hotel != null) {
                hotel.id = doc.getId();
                allHotels.add(hotel);
            }
        }

        Collections.sort(allHotels, Comparator.comparingDouble((Hotel hotel) -> hotel.rating).reversed());

        topRatedHotels.clear();
        nearbyHotels.clear();

        for (int i = 0; i < allHotels.size(); i++) {
            if (i < 3) {
                topRatedHotels.add(allHotels.get(i));
            } else {
                nearbyHotels.add(allHotels.get(i));
            }
        }

        if (nearbyHotels.isEmpty()) {
            nearbyHotels.addAll(topRatedHotels);
        }

        mostVisitedAdapter.notifyDataSetChanged();
        nearbyAdapter.notifyDataSetChanged();
    }

    private void clearLists() {
        topRatedHotels.clear();
        nearbyHotels.clear();
        mostVisitedAdapter.notifyDataSetChanged();
        nearbyAdapter.notifyDataSetChanged();
    }

    private void openSeeAllHotels() {
        Intent intent = new Intent(this, HotelListActivity.class);
        intent.putExtra("sort_by", "rating_desc");
        startActivity(intent);
    }

    private void openHotelDetail(Hotel hotel) {
        if (hotel == null || hotel.id == null || hotel.id.trim().isEmpty()) {
            return;
        }

        Intent intent = new Intent(this, HotelDetailActivity.class);
        intent.putExtra("hotel_id", hotel.id);
        startActivity(intent);
    }
}
