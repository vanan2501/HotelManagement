package com.example.hotelmanagement.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.ui.explore.ExploreRoomsActivity;
import com.example.hotelmanagement.ui.login.login;
import com.example.hotelmanagement.ui.trips.MyTripsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.hotelmanagement.ui.admin.AdminActivity;
import com.example.hotelmanagement.ui.login.login;
import com.example.hotelmanagement.ui.payment.PaymentActivity;
import com.example.hotelmanagement.ui.review.ReviewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {
    private TextView txtName, txtEmail, txtPhone, txtRole;
    private Button btnLogout;

//    Button btnReview, btnPayment, btnDashboard;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_with_nav);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtRole = findViewById(R.id.txtRole);
        btnLogout = findViewById(R.id.btnLogout);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUser();

        btnLogout.setOnClickListener(v -> logout());

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
                    int id = item.getItemId();
                    if (id == R.id.nav_home) {
                        startActivity(new Intent(this, ExploreRoomsActivity.class));
                        return true;
                    } else if (id == R.id.nav_trips) {
                        startActivity(new Intent(this, MyTripsActivity.class));
                        return true;
                    } else if (id == R.id.nav_profile) {
                        return true;
                    }
                    return false;
                });
//        Button btnReview = findViewById(R.id.btnReview);
//        Button btnPayment = findViewById(R.id.btnPayment);
//        Button btnDashboard = findViewById(R.id.btnDashboard);

//        btnReview.setOnClickListener(v -> {
//            startActivity(new Intent(this, ReviewActivity.class));
//        });
//
//        btnPayment.setOnClickListener(v -> {
//            startActivity(new Intent(this, PaymentActivity.class));
//        });
//
//        btnDashboard.setOnClickListener(v -> {
//            startActivity(new Intent(this, AdminActivity.class));
//        });
    }
    private void loadUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            txtName.setText("Name: Guest");
            txtEmail.setText("Email: guest@example.com");
            txtPhone.setText("Phone: 0000000000");
            txtRole.setText("Guest");
            return;
        }
        String uid = currentUser.getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if(document.exists()){
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String phone = document.getString("phone");
                        Long role = document.getLong("role_id");

                        txtName.setText("Name: " + name);
                        txtEmail.setText("Email: " + email);
                        txtPhone.setText("Phone: " + phone);
                        if(role != null && role == 1){
                            txtRole.setText("Admin");
                        } else {
                            txtRole.setText("User");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi load dữ liệu", Toast.LENGTH_SHORT).show();
                });
    }
    private void logout() {
        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(this, login.class));
        finish();
    }
}
