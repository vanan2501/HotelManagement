package com.example.hotelmanagement.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hotelmanagement.data.api.ApiClient;
import com.example.hotelmanagement.data.model.Dashboard;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminViewModel extends ViewModel {
    private MutableLiveData<Dashboard> dashboardData=new MutableLiveData<>();

    public LiveData<Dashboard> getDashboard() {
        return dashboardData;
    }

    public void loadDashboard() {
        ApiClient.getApiService().getDashboard().enqueue(new Callback<Dashboard>() {
            @Override
            public void onResponse(Call<Dashboard> call, Response<Dashboard> response) {
                dashboardData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Dashboard> call, Throwable t) {

            }
        });
    }
}
