package com.example.hotelmanagement.ui.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.viewmodel.AdminViewModel;

public class AdminActivity extends AppCompatActivity {
    TextView txtUsers, txtBookings, txtRevenue;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_admin);

        txtUsers = findViewById(R.id.txtUsers);
        txtBookings = findViewById(R.id.txtBookings);
        txtRevenue = findViewById(R.id.txtRevenue);

        AdminViewModel viewModel = new ViewModelProvider(this)
                .get(AdminViewModel.class);
         viewModel.getDashboard().observe(this,data -> {
             txtUsers.setText("Users: " + data.totalUsers);
             txtBookings.setText("Bookings: " + data.totalBookings);
             txtRevenue.setText("Revenue: " + data.totalRevenue);
         });

         viewModel.loadDashboard();


    }
}
