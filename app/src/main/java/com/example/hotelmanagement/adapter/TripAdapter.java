package com.example.hotelmanagement.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.model.Review;
import com.example.hotelmanagement.model.Trip;
import com.example.hotelmanagement.ui.trips.TripDetailsActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public TripAdapter(List<Trip> tripList) {
        this.tripList = tripList;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        
        // 1. Hiển thị thông tin cơ bản
        holder.txtId.setVisibility(View.GONE); // Ẩn TextView hiển thị Booking ID
        holder.txtDates.setText(trip.getDates());
        holder.txtStatus.setText(trip.getStatus());
        holder.txtPrice.setText("$" + trip.getTotal_price() + " Total");

        holder.btnWriteReview.setOnClickListener(v -> showReviewDialog(holder.itemView, trip));

        // Trạng thái mặc định khi đang tải
        holder.txtName.setText("Loading...");
        holder.imgRoom.setImageResource(R.drawable.room1);

        // 2. Lấy tên và ảnh từ bảng rooms dựa trên hotel_id
        if (trip.getHotel_id() != null) {
            db.collection("rooms")
                    .whereEqualTo("hotel_id", trip.getHotel_id())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            com.google.firebase.firestore.DocumentSnapshot
                                    document = queryDocumentSnapshots.getDocuments().get(0);
                            String hotelName = document.getString("hotel_name");
                            String imageUrl = document.getString("imageUrl");

                            // Hiển thị tên phòng thực tế
                            holder.txtName.setText(hotelName != null ? hotelName : "Hotel Name N/A");
                            
                            // Hiển thị ảnh thực tế
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(holder.itemView.getContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.room1)
                                        .into(holder.imgRoom);
                            }
                        } else {
                            holder.txtName.setText("Hotel ID: " + trip.getHotel_id());
                        }
                    })
                    .addOnFailureListener(e -> {
                        holder.txtName.setText("Error loading room");
                    });
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TripDetailsActivity.class);
            intent.putExtra("trip_data", trip);
            v.getContext().startActivity(intent);
        });

        //3. Hiển thị thông tin thanh toán và button Review
        if ("PAID".equalsIgnoreCase(trip.getPayment_status())) {
            holder.btnWriteReview.setVisibility(View.VISIBLE);
        }
        else { holder.btnWriteReview.setVisibility(View.GONE); }
    }

    private void showReviewDialog(View view, Trip trip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_write_review, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText edtComment = dialogView.findViewById(R.id.edtComment);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmitReview);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            String comment = edtComment.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(view.getContext(), "Please select a rating", Toast.LENGTH_SHORT).show();
                return;
            }

            if (comment.isEmpty()) {
                edtComment.setError("Please enter a comment");
                return;
            }

            saveReview(view, trip, rating, comment, dialog);
        });

        dialog.show();
    }

    private void saveReview(View view, Trip trip, int rating, String comment, AlertDialog dialog) {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(view.getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo review object
        Review review = new Review(
                comment,
                rating,
                Timestamp.now(), // create_at
                trip.getUser_id(), // Lấy user_id từ trip
                trip.getHotel_id()
        );

        db.collection("reviews")
                .add(review)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(view.getContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(view.getContext(), "Failed to submit review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView txtName, txtId, txtDates, txtStatus, txtPrice;
        Button btnWriteReview;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgTripRoom);
            txtName = itemView.findViewById(R.id.txtTripRoomName);
            txtId = itemView.findViewById(R.id.txtTripId);
            txtDates = itemView.findViewById(R.id.txtTripDates);
            txtStatus = itemView.findViewById(R.id.txtTripStatus);
            txtPrice = itemView.findViewById(R.id.txtTripPrice);
            btnWriteReview = itemView.findViewById(R.id.btnWriteReview);
        }
    }
}
