package com.example.hotelmanagement.ui.review;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.model.Review;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReviewActivity extends AppCompatActivity {

    private EditText edtComment;
    private RatingBar ratingBar;
    private Button btnSubmit;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String hotelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        //Init Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Bind view
        edtComment = findViewById(R.id.edtComment);
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Nhận dữ liệu từ Intent
        hotelId = getIntent().getStringExtra("hotel_id");

        btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        int rating = (int) ratingBar.getRating();
        String comment = edtComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
            return;
        }

        if (comment.isEmpty()) {
            edtComment.setError("Nhập nội dung review");
            return;
        }

        //Tạo object Review
        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setHotel_id(hotelId);
        review.setUser_id(auth.getCurrentUser().getUid());
        review.setCreate_at(Timestamp.now());

        //Lưu Firestore
        db.collection("reviews")
                .add(review)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Gửi review thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // quay lại
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}