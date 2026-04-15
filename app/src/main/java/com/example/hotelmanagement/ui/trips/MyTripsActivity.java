package com.example.hotelmanagement.ui.trips;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.TripAdapter;
import com.example.hotelmanagement.model.BookingManager;
import com.example.hotelmanagement.model.Trip;
import com.example.hotelmanagement.ui.explore.ExploreRoomsActivity;
import com.example.hotelmanagement.ui.profile.profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MyTripsActivity extends AppCompatActivity {

    private RecyclerView rvTrips;
    private TripAdapter adapter;
    private List<Trip> allTrips;
    private List<Trip> filteredTrips;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        rvTrips = findViewById(R.id.rvTrips);
        tabLayout = findViewById(R.id.tabLayout);
        rvTrips.setLayoutManager(new LinearLayoutManager(this));

        allTrips = new ArrayList<>();
        filteredTrips = new ArrayList<>();
        
        adapter = new TripAdapter(filteredTrips);
        adapter.setOnTripStatusChangeListener(this::loadBookings); // Reload khi cancel thành công
        rvTrips.setAdapter(adapter);

        loadBookings();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateFilter();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_trips);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, ExploreRoomsActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_trips) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, profile.class));
                return true;
            }
            return false;
        });
    }

    private void updateFilter() {
        int pos = tabLayout.getSelectedTabPosition();
        if (pos == 0) filterByStatus("UPCOMING");
        else if (pos == 1) filterByStatus("COMPLETED");
        else filterByStatus("CANCELLED");
    }

    private void loadBookings() {
        BookingManager.getInstance().fetchBookings(new BookingManager.OnBookingsLoadedListener() {
            @Override
            public void onSuccess(List<Trip> tripList) {
                allTrips.clear();
                allTrips.addAll(tripList);
                updateFilter();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MyTripsActivity.this, "Error loading bookings: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByStatus(String status) {
        filteredTrips.clear();
        for (Trip trip : allTrips) {
            if (trip.getStatus() != null && trip.getStatus().equalsIgnoreCase(status)) {
                filteredTrips.add(trip);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }
}
