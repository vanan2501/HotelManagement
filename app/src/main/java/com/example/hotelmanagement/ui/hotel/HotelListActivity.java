package com.example.hotelmanagement.ui.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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
import com.example.hotelmanagement.ui.home.HomeActivity;
import com.example.hotelmanagement.ui.profile.profile;
import com.example.hotelmanagement.ui.trips.MyTripsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class HotelListActivity extends AppCompatActivity {
    private static final String HOTEL_COLLECTION = "hotel";

    private RecyclerView rvHotels;
    private final List<Hotel> allHotels = new ArrayList<>();
    private final List<Hotel> visibleHotels = new ArrayList<>();
    private HotelAdapter adapter;
    private FirebaseFirestore db;
    private String sortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hotel_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText edtSearch = findViewById(R.id.edtSearch);
        rvHotels = findViewById(R.id.rvHotels);
        rvHotels.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        sortBy = getIntent().getStringExtra("sort_by");

        adapter = new HotelAdapter(this, visibleHotels, this::openHotelDetail);
        rvHotels.setAdapter(adapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

// chọn tab hiện tại
        bottomNav.setSelectedItemId(R.id.nav_home);

// xử lý click
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
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

        loadHotels();
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadHotels() {
        db.collection(HOTEL_COLLECTION)
                .get()
                .addOnSuccessListener(this::applyHotelsSnapshot);
    }

    private void applyHotelsSnapshot(Iterable<? extends DocumentSnapshot> snapshots) {
        allHotels.clear();
        for (DocumentSnapshot doc : snapshots) {
            Hotel hotel = doc.toObject(Hotel.class);
            if (hotel != null) {
                hotel.id = doc.getId();
                allHotels.add(hotel);
            }
        }

        sortHotels(allHotels);
        visibleHotels.clear();
        visibleHotels.addAll(allHotels);
        adapter.notifyDataSetChanged();
    }

    private void sortHotels(List<Hotel> hotels) {
        if ("rating_desc".equals(sortBy)) {
            Collections.sort(hotels, Comparator.comparingDouble((Hotel hotel) -> hotel.rating).reversed());
        }
    }

    private void filter(String keyword) {
        String normalized = keyword == null ? "" : keyword.trim().toLowerCase(Locale.getDefault());
        visibleHotels.clear();

        if (normalized.isEmpty()) {
            visibleHotels.addAll(allHotels);
        } else {
            for (Hotel hotel : allHotels) {
                String name = hotel.name == null ? "" : hotel.name.toLowerCase(Locale.getDefault());
                if (name.contains(normalized)) {
                    visibleHotels.add(hotel);
                }
            }
        }

        adapter.notifyDataSetChanged();
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
