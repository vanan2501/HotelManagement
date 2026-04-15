package com.example.hotelmanagement.ui.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.model.Hotel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hotelmanagement.ui.room.RoomListActivity;

public class HotelDetailActivity extends AppCompatActivity {
    private static final String HOTEL_COLLECTION = "hotel";

    private FirebaseFirestore db;
    private ImageView imgHotel;
    private TextView txtName;
    private TextView txtAddress;
    private TextView txtDesc;
    private Button btnViewRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hotel_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String id = getIntent().getStringExtra("hotel_id");

        imgHotel = findViewById(R.id.imgHotel);
        txtName = findViewById(R.id.txtName);
        txtAddress = findViewById(R.id.txtAddress);
        txtDesc = findViewById(R.id.txtDesc);
        btnViewRoom = findViewById(R.id.btnViewRoom);
        db = FirebaseFirestore.getInstance();

        if (id == null || id.trim().isEmpty()) {
            Toast.makeText(this, "Khong tim thay khach san", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection(HOTEL_COLLECTION).document(id).get()
                .addOnSuccessListener(doc -> bindHotel(doc.toObject(Hotel.class)))
                .addOnFailureListener(e -> finishWithError());

        btnViewRoom.setOnClickListener(v -> {
            if (id != null && !id.isEmpty()) {
                Intent intent = new Intent(HotelDetailActivity.this, RoomListActivity.class);
                intent.putExtra("hotel_id", id); // 🔥 truyền ID khách sạn
                startActivity(intent);
            } else {
                Toast.makeText(this, "Khong co ID khach san", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindHotel(Hotel hotel) {
        if (hotel == null) {
            Toast.makeText(this, "Khong co du lieu khach san", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        txtName.setText(hotel.name != null ? hotel.name : "Hotel Detail");
        txtAddress.setText(hotel.address != null ? hotel.address : "No address");
        txtDesc.setText(hotel.description != null ? hotel.description : "No description");

        if (hotel.images != null && !hotel.images.trim().isEmpty()) {
            Glide.with(this)
                    .load(hotel.images)
                    .placeholder(R.drawable.room1)
                    .error(R.drawable.room1)
                    .into(imgHotel);
        } else {
            imgHotel.setImageResource(R.drawable.room1);
        }
    }

    private void finishWithError() {
        Toast.makeText(this, "Tai chi tiet khach san that bai", Toast.LENGTH_SHORT).show();
        finish();
    }
}
