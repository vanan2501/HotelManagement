package com.example.hotelmanagement.ui.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.model.Hotel;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddHotelActivity extends AppCompatActivity {
    private static final String HOTEL_COLLECTION = "hotel";

    private EditText edtName;
    private EditText edtAddress;
    private EditText edtDescription;
    private EditText edtImage;
    private EditText edtPrice;
    private EditText edtCategoryId;
    private EditText edtRating;
    private Button btnSaveHotel;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_hotel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtName = findViewById(R.id.edtHotelName);
        edtAddress = findViewById(R.id.edtHotelAddress);
        edtDescription = findViewById(R.id.edtHotelDescription);
        edtImage = findViewById(R.id.edtHotelImage);
        edtPrice = findViewById(R.id.edtHotelPrice);
        edtCategoryId = findViewById(R.id.edtHotelCategoryId);
        edtRating = findViewById(R.id.edtHotelRating);
        btnSaveHotel = findViewById(R.id.btnSaveHotel);

        db = FirebaseFirestore.getInstance();
        btnSaveHotel.setOnClickListener(v -> saveHotel());
    }

    private void saveHotel() {
        String name = edtName.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String image = edtImage.getText().toString().trim();
        String priceText = edtPrice.getText().toString().trim();
        String categoryIdText = edtCategoryId.getText().toString().trim();
        String ratingText = edtRating.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Nhap ten khach san");
            return;
        }
        if (address.isEmpty()) {
            edtAddress.setError("Nhap dia chi");
            return;
        }
        if (description.isEmpty()) {
            edtDescription.setError("Nhap mo ta");
            return;
        }
        if (image.isEmpty()) {
            edtImage.setError("Nhap link hinh");
            return;
        }
        if (priceText.isEmpty()) {
            edtPrice.setError("Nhap gia");
            return;
        }
        if (categoryIdText.isEmpty()) {
            edtCategoryId.setError("Nhap category id");
            return;
        }
        if (ratingText.isEmpty()) {
            edtRating.setError("Nhap rating");
            return;
        }

        int price;
        int categoryId;
        double rating;
        try {
            price = Integer.parseInt(priceText);
            categoryId = Integer.parseInt(categoryIdText);
            rating = Double.parseDouble(ratingText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Gia, category id va rating phai la so", Toast.LENGTH_LONG).show();
            return;
        }

        Hotel hotel = new Hotel(name, address, description, image, price, categoryId, rating);
        db.collection(HOTEL_COLLECTION)
                .add(hotel)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Them hotel thanh cong", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Them hotel that bai", Toast.LENGTH_LONG).show());
    }

    private void clearForm() {
        edtName.setText("");
        edtAddress.setText("");
        edtDescription.setText("");
        edtImage.setText("");
        edtPrice.setText("");
        edtCategoryId.setText("");
        edtRating.setText("");
        edtName.requestFocus();
    }
}
