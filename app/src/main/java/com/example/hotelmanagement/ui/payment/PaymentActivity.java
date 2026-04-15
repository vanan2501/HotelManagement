package com.example.hotelmanagement.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.model.Trip;
import com.example.hotelmanagement.ui.trips.MyTripsActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentActivity extends AppCompatActivity {

    RadioButton rbMomo, rbCash;
    Button btnPay;
    FirebaseFirestore db;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        db = FirebaseFirestore.getInstance();

        rbMomo = findViewById(R.id.rbMomo);
        rbCash = findViewById(R.id.rbCash);
        btnPay = findViewById(R.id.btnPay);

        trip = (Trip) getIntent().getSerializableExtra("trip_data");

        btnPay.setOnClickListener(v -> handlePayment());
    }

    private void handlePayment() {

        if (trip == null) return;

        String method = rbMomo.isChecked() ? "MOMO" : "CASH";
        String status = rbMomo.isChecked() ? "PAID" : "UNPAID";

        trip.setPayment_method(method);
        trip.setPayment_status(status);

        db.collection("bookings")
                .add(trip)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MyTripsActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show());
    }
}