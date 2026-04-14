package com.example.hotelmanagement.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hotelmanagement.data.api.ApiClient;
import com.example.hotelmanagement.data.model.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewViewModel extends ViewModel {
    private MutableLiveData<List<Review>> reviews = new MutableLiveData<>();

    public LiveData<List<Review>> getReview(){
        return reviews;
    }
    public void loadReviews(int hotelId) {
        ApiClient.getApiService().getReviews(hotelId)
                .enqueue(new Callback<List<Review>>() {
                    @Override
                    public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                        reviews.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Review>> call, Throwable t) {

                    }
                });
    }
}
