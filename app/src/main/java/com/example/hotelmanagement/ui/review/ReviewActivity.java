package com.example.hotelmanagement.ui.review;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.data.api.ApiClient;
import com.example.hotelmanagement.data.model.Review;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {
    EditText edtComment;
    RatingBar ratingBar;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_review);

        edtComment = findViewById(R.id.edtComment);
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmit = findViewById(R.id.btnSubmit);

        int hotelId = getIntent().getIntExtra("hotel_id",1);
        int userId=1;

        btnSubmit.setOnClickListener(v -> {
            Review review = new Review();
            review.rating = (int) ratingBar.getRating();
            review.comment = edtComment.getText().toString();

            ApiClient.getApiService().addReview(review).enqueue(new Callback<Review>() {
                @Override
                public void onResponse(Call<Review> call, Response<Review> response) {
                    Toast.makeText(ReviewActivity.this,"Thành công",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Review> call, Throwable t) {
                    Toast.makeText(ReviewActivity.this,"Không thể gửi review.",
                            Toast.LENGTH_SHORT).show();
                    Log.e("Review Error",t.getMessage());
                }
            });

        });


    }
}
