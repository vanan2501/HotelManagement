package com.example.hotelmanagement.ui.booking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.ReviewAdapter;
import com.example.hotelmanagement.model.Review;
import com.example.hotelmanagement.model.Room;
import com.example.hotelmanagement.model.Trip;
import com.example.hotelmanagement.ui.trips.MyTripsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ConfirmBookingActivity extends AppCompatActivity {

    private TextView txtCheckInDate, txtCheckOutDate, txtRoomPriceTotal, txtTotalPrice, txtServiceFee, txtTax;
    private EditText edtSpecialRequests;
    private Calendar checkInCalendar, checkOutCalendar;
    private SimpleDateFormat dateFormat;
    private Room room;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private CardView cvReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_booking);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        room = (Room) getIntent().getSerializableExtra("room_data");

        TextView txtRoomNameLabel = findViewById(R.id.txtRoomName);
        txtRoomPriceTotal = findViewById(R.id.txtRoomPriceTotal);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtServiceFee = findViewById(R.id.txtServiceFee);
        txtTax = findViewById(R.id.txtTax);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        
        txtCheckInDate = findViewById(R.id.txtCheckInDate);
        txtCheckOutDate = findViewById(R.id.txtCheckOutDate);
        LinearLayout btnSelectCheckIn = findViewById(R.id.btnSelectCheckIn);
        LinearLayout btnSelectCheckOut = findViewById(R.id.btnSelectCheckOut);
        
        edtSpecialRequests = findViewById(R.id.edtSpecialRequests);

        // Reviews setup
        cvReviews = findViewById(R.id.cvReviews);
        rvReviews = findViewById(R.id.rvReviews);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        checkInCalendar = Calendar.getInstance();
        checkOutCalendar = Calendar.getInstance();
        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);

        updateDateLabels();

        btnSelectCheckIn.setOnClickListener(v -> showDatePicker(true));
        btnSelectCheckOut.setOnClickListener(v -> showDatePicker(false));

        if (room != null) {
            txtRoomNameLabel.setText(room.getName());
            calculatePrice();

            btnConfirm.setOnClickListener(v -> {
                double total = calculateTotalPrice();
                saveBookingToFirestore(total);
            });

            if (room.getHotel_id() != null) {
                fetchReviews(room.getHotel_id());
            }
        }
    }

    private void calculatePrice() {
        if (room == null) return;

        long diff = checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis();
        int days = (int) (diff / (24 * 60 * 60 * 1000));
        if (days <= 0) days = 1;

        double basePrice = room.getPrice() * days;
        double serviceFee = basePrice * 0.15;
        double tax = basePrice * 0.10;
        double total = basePrice + serviceFee + tax;

        txtRoomPriceTotal.setText(String.format(Locale.getDefault(), "($%.0f x %d)", room.getPrice(), days));
        if (txtServiceFee != null) txtServiceFee.setText(String.format(Locale.getDefault(), "($%.1f)", serviceFee));
        if (txtTax != null) txtTax.setText(String.format(Locale.getDefault(), "($%.1f)", tax));
        txtTotalPrice.setText(String.format(Locale.getDefault(), "$%.1f", total));
        
        TextView txtDaysCount = findViewById(R.id.txtDaysCount);
        if (txtDaysCount != null) {
            txtDaysCount.setText(days + (days > 1 ? " Nights" : " Night"));
        }
    }

    private double calculateTotalPrice() {
        long diff = checkOutCalendar.getTimeInMillis() - checkInCalendar.getTimeInMillis();
        int days = (int) (diff / (24 * 60 * 60 * 1000));
        if (days <= 0) days = 1;

        double basePrice = room.getPrice() * days;
        double serviceFee = basePrice * 0.15;
        double tax = basePrice * 0.10;
        return basePrice + serviceFee + tax;
    }

    private void fetchReviews(String hotelId) {
        db.collection("reviews")
                .whereEqualTo("hotel_id", hotelId)
                .orderBy("create_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reviewList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Review review = document.toObject(Review.class);
                        reviewList.add(review);
                    }
                    if (!reviewList.isEmpty()) {
                        cvReviews.setVisibility(View.VISIBLE);
                        reviewAdapter.notifyDataSetChanged();
                    } else {
                        cvReviews.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    fetchReviewsWithoutOrder(hotelId);
                });
    }

    private void fetchReviewsWithoutOrder(String hotelId) {
        db.collection("reviews")
                .whereEqualTo("hotel_id", hotelId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    reviewList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Review review = document.toObject(Review.class);
                        reviewList.add(review);
                    }
                    if (!reviewList.isEmpty()) {
                        cvReviews.setVisibility(View.VISIBLE);
                        reviewAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void saveBookingToFirestore(double total) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to book", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            String customUserId = null;
            if (documentSnapshot.exists()) {
                customUserId = documentSnapshot.getString("user_id");
            }
            
            if (customUserId == null || customUserId.isEmpty()) {
                customUserId = uid;
            }

            String requests = edtSpecialRequests.getText().toString().trim();
            String checkin = dateFormat.format(checkInCalendar.getTime());
            String checkout = dateFormat.format(checkOutCalendar.getTime());

            Trip newTrip = new Trip(
                    checkin,
                    checkout,
                    room.getHotel_id(),
                    requests,
                    "UPCOMING",
                    total,
                    customUserId
            );

            db.collection("bookings")
                    .add(newTrip)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Booking Confirmed!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MyTripsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to book: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar calendar = isCheckIn ? checkInCalendar : checkOutCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabels();
            calculatePrice(); // Recalculate when date changes
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        
        if (isCheckIn) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        } else {
            datePickerDialog.getDatePicker().setMinDate(checkInCalendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
        }
        
        datePickerDialog.show();
    }

    private void updateDateLabels() {
        txtCheckInDate.setText(dateFormat.format(checkInCalendar.getTime()));
        txtCheckOutDate.setText(dateFormat.format(checkOutCalendar.getTime()));
    }
}
