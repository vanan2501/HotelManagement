package com.example.hotelmanagement.ui.payment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.data.api.ApiClient;
import com.example.hotelmanagement.data.model.Payment;
import com.example.hotelmanagement.ui.review.ReviewActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    Button btnPay;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_payment);

        btnPay = findViewById(R.id.btnPay);

//        int bookingId = getIntent().getIntExtra("booking_id",1);
//        double amount = getIntent().getDoubleExtra("amount",500000);

        btnPay.setOnClickListener(v -> {
            Payment payment = new Payment();
            payment.amount=500000;
            payment.method="momo";

            ApiClient.getApiService().createPayment(payment)
                    .enqueue(new Callback<Payment>() {
                        @Override
                        public void onResponse(Call<Payment> call, Response<Payment> response) {
                            Toast.makeText(PaymentActivity.this,"Thanh toán thành công",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<Payment> call, Throwable t) {
                            Toast.makeText(PaymentActivity.this,"Không thể gửi review.",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("Pay Error",t.getMessage());
                        }
                    });

        });


    }
}
