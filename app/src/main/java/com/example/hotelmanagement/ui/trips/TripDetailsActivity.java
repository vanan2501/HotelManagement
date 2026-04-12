package com.example.hotelmanagement.ui.trips;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.ReviewAdapter;
import com.example.hotelmanagement.model.Review;
import com.example.hotelmanagement.model.Trip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TripDetailsActivity extends AppCompatActivity {

    private TextView txtRoomName, txtGuestName, txtGuestPhone, txtGuestEmail;
    private ImageView imgRoom;
    private FirebaseFirestore db;
    private RecyclerView rvReviews;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private CardView cvReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        db = FirebaseFirestore.getInstance();

        Trip trip = (Trip) getIntent().getSerializableExtra("trip_data");

        if (trip != null) {
            imgRoom = findViewById(R.id.imgRoom);
            txtRoomName = findViewById(R.id.txtRoomName);
            TextView txtBookingId = findViewById(R.id.txtBookingId);
            TextView txtDates = findViewById(R.id.txtDates);
            TextView txtStatus = findViewById(R.id.txtStatus);
            TextView txtPrice = findViewById(R.id.txtPrice);
            TextView txtSpecialRequests = findViewById(R.id.txtSpecialRequests);

            txtGuestName = findViewById(R.id.txtGuestName);
            txtGuestPhone = findViewById(R.id.txtGuestPhone);
            txtGuestEmail = findViewById(R.id.txtGuestEmail);
            
            // Reviews setup
            cvReviews = findViewById(R.id.cvReviews);
            rvReviews = findViewById(R.id.rvReviews);
            reviewList = new ArrayList<>();
            reviewAdapter = new ReviewAdapter(reviewList);
            rvReviews.setLayoutManager(new LinearLayoutManager(this));
            rvReviews.setAdapter(reviewAdapter);

            imgRoom.setImageResource(R.drawable.room1);
            txtRoomName.setText("Loading...");
            
            if (txtBookingId != null) {
                txtBookingId.setVisibility(View.GONE); 
            }

            txtDates.setText("Dates: " + trip.getDates());
            txtStatus.setText("Status: " + trip.getStatus());
            txtPrice.setText("Total Paid: $" + trip.getTotal_price());

            String specialReq = trip.getSpecial_request();
            txtSpecialRequests.setText("Special Requests: " + (specialReq != null && !specialReq.isEmpty() ? specialReq : "None"));

            if (trip.getHotel_id() != null) {
                fetchHotelData(trip.getHotel_id());
                fetchReviews(trip.getHotel_id());
            }

            if (trip.getUser_id() != null) {
                fetchUserData(trip.getUser_id());
            }
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void fetchHotelData(String hotelId) {
        db.collection("rooms")
                .whereEqualTo("hotel_id", hotelId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        com.google.firebase.firestore.DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String name = document.getString("hotel_name");
                        String imageUrl = document.getString("imageUrl");

                        txtRoomName.setText(name != null ? name : "Hotel Name N/A");
                        
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.room1)
                                    .into(imgRoom);
                        }
                    } else {
                        txtRoomName.setText("Hotel not found");
                    }
                })
                .addOnFailureListener(e -> {
                    txtRoomName.setText("Error loading hotel");
                });
    }

    private void fetchReviews(String hotelId) {
        db.collection("reviews")
                .whereEqualTo("hotel_id", hotelId)
                .orderBy("created_at", Query.Direction.DESCENDING)
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
                    // Nếu lỗi do thiếu Index Firestore, có thể thử fetch không cần orderBy trước
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

    private void fetchUserData(String userId) {
        db.collection("users")
                .whereEqualTo("user_id", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        updateUserUI(queryDocumentSnapshots.getDocuments().get(0));
                    } else {
                        db.collection("users").document(userId).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        updateUserUI(documentSnapshot);
                                    } else {
                                        txtGuestName.setText("Name: User not found");
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserUI(com.google.firebase.firestore.DocumentSnapshot document) {
        String name = document.getString("name");
        String phone = document.getString("phone");
        String email = document.getString("email");

        txtGuestName.setText("Name: " + (name != null ? name : "N/A"));
        txtGuestPhone.setText("Phone: " + (phone != null ? phone : "N/A"));
        txtGuestEmail.setText("Email: " + (email != null ? email : "N/A"));
    }
}
