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
import com.example.hotelmanagement.ui.login.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {
    private TextView txtName, txtEmail, txtPhone, txtRole;
    private Button btnLogout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
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
    }
    private void loadUser() {
        String uid = auth.getCurrentUser().getUid();

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
                        if(role == 1){
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