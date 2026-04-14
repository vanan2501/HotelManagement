package com.example.hotelmanagement.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
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
    private OnTripStatusChangeListener statusChangeListener;

    public interface OnTripStatusChangeListener {
        void onStatusChanged();
    }

    public TripAdapter(List<Trip> tripList) {
        this.tripList = tripList;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public void setOnTripStatusChangeListener(OnTripStatusChangeListener listener) {
        this.statusChangeListener = listener;
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
        
        holder.txtId.setVisibility(View.GONE);
        holder.txtDates.setText(trip.getDates());
        holder.txtStatus.setText(trip.getStatus());
        holder.txtPrice.setText("$" + trip.getTotal_price() + " Total");
        
        // Logic hiển thị nút và màu sắc status
        if (trip.getStatus() != null) {
            String status = trip.getStatus();
            if (status.equalsIgnoreCase("UPCOMING")) {
                holder.btnCancelTrip.setVisibility(View.VISIBLE);
                holder.btnWriteReview.setVisibility(View.GONE);
                holder.txtStatus.setTextColor(Color.parseColor("#2E7D32")); // Green
            } else if (status.equalsIgnoreCase("COMPLETED")) {
                holder.btnCancelTrip.setVisibility(View.GONE);
                holder.btnWriteReview.setVisibility(View.VISIBLE);
                holder.txtStatus.setTextColor(Color.parseColor("#2E7D32")); // Green
            } else if (status.equalsIgnoreCase("CANCELLED")) {
                holder.btnCancelTrip.setVisibility(View.GONE);
                holder.btnWriteReview.setVisibility(View.GONE);
                holder.txtStatus.setTextColor(Color.RED); // Set màu đỏ cho status CANCELLED
            }
        }

        holder.btnCancelTrip.setOnClickListener(v -> showCancelConfirmation(v, trip));
        holder.btnWriteReview.setOnClickListener(v -> showReviewDialog(holder.itemView, trip));

        // Load ảnh từ drawable
        if (trip.getHotel_id() != null) {
            loadHotelInfo(holder, trip);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TripDetailsActivity.class);
            intent.putExtra("trip_data", trip);
            v.getContext().startActivity(intent);
        });
    }

    private void loadHotelInfo(TripViewHolder holder, Trip trip) {
        holder.txtName.setText("Loading...");
        db.collection("rooms")
                .whereEqualTo("hotel_id", trip.getHotel_id())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        com.google.firebase.firestore.DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        holder.txtName.setText(document.getString("hotel_name"));
                        String imgName = document.getString("imageUrl");
                        if (imgName != null) {
                            int resId = holder.itemView.getContext().getResources().getIdentifier(imgName, "drawable", holder.itemView.getContext().getPackageName());
                            holder.imgRoom.setImageResource(resId != 0 ? resId : R.drawable.room1);
                        }
                    }
                });
    }

    private void showCancelConfirmation(View view, Trip trip) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> cancelBooking(view, trip))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelBooking(View view, Trip trip) {
        if (trip.getId() == null) return;

        db.collection("bookings").document(trip.getId())
                .update("status", "CANCELLED")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(view.getContext(), "Booking cancelled", Toast.LENGTH_SHORT).show();
                    if (statusChangeListener != null) statusChangeListener.onStatusChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(view.getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showReviewDialog(View view, Trip trip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_write_review, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText edtComment = dialogView.findViewById(R.id.edtComment);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmitReview);

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            String comment = edtComment.getText().toString().trim();
            if (rating > 0 && !comment.isEmpty()) {
                saveReview(view, trip, rating, comment, dialog);
            }
        });
        dialog.show();
    }

    private void saveReview(View view, Trip trip, int rating, String comment, AlertDialog dialog) {
        Review review = new Review(comment, rating, Timestamp.now(), trip.getUser_id(), trip.getHotel_id());
        db.collection("reviews").add(review).addOnSuccessListener(doc -> {
            Toast.makeText(view.getContext(), "Review submitted!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    @Override
    public int getItemCount() { return tripList.size(); }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView txtName, txtId, txtDates, txtStatus, txtPrice;
        Button btnWriteReview, btnCancelTrip;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgTripRoom);
            txtName = itemView.findViewById(R.id.txtTripRoomName);
            txtId = itemView.findViewById(R.id.txtTripId);
            txtDates = itemView.findViewById(R.id.txtTripDates);
            txtStatus = itemView.findViewById(R.id.txtTripStatus);
            txtPrice = itemView.findViewById(R.id.txtTripPrice);
            btnWriteReview = itemView.findViewById(R.id.btnWriteReview);
            btnCancelTrip = itemView.findViewById(R.id.btnCancelTrip);
        }
    }
}
