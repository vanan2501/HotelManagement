package com.example.hotelmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.model.Review;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private FirebaseFirestore db;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.txtComment.setText(review.getComment());
        holder.txtRating.setText("★ " + review.getRating());
        
        // 1. Hiển thị ngày tháng từ create_at
        Date reviewDate = review.getCreateAtDate();
        if (reviewDate != null) {
            holder.txtDate.setText(dateFormat.format(reviewDate));
        } else {
            holder.txtDate.setText("No date");
        }

        // 2. Lấy tên user dựa trên user_id từ collection "users"
        holder.txtUserName.setText("Loading...");
        if (review.getUser_id() != null) {
            // Thử tìm theo custom user_id
            db.collection("users")
                    .whereEqualTo("user_id", review.getUser_id())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String name = queryDocumentSnapshots.getDocuments().get(0).getString("name");
                            holder.txtUserName.setText(name != null ? name : "Anonymous");
                        } else {
                            // Thử tìm theo UID (document id)
                            db.collection("users").document(review.getUser_id()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String name = documentSnapshot.getString("name");
                                            holder.txtUserName.setText(name != null ? name : "Anonymous");
                                        } else {
                                            holder.txtUserName.setText("User: " + review.getUser_id());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> holder.txtUserName.setText("User"));
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView txtComment, txtRating, txtDate, txtUserName;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            txtComment = itemView.findViewById(R.id.txtReviewComment);
            txtRating = itemView.findViewById(R.id.txtReviewRating);
            txtDate = itemView.findViewById(R.id.txtReviewDate);
            txtUserName = itemView.findViewById(R.id.txtReviewUserName);
        }
    }
}
