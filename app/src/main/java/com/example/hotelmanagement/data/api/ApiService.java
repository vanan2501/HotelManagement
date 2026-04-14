package com.example.hotelmanagement.data.api;

import com.example.hotelmanagement.data.model.Dashboard;
import com.example.hotelmanagement.data.model.Payment;
import com.example.hotelmanagement.data.model.Review;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ApiService {
    @GET("reviews/{hotel_id}")
    Call<List<Review>> getReviews(@Path("hotel_id") int hotelId);

    @POST("reviews")
    Call<Review> addReview(@Body Review review);

    @POST("payments")
    Call<Payment> createPayment(@Body Payment payment);

    @GET("payments/{booking_id}")
    Call<Payment> getPayment(@Path("booking_id") int bookingId);

    @GET("admin/dashboard")
    Call<Dashboard> getDashboard();
}
