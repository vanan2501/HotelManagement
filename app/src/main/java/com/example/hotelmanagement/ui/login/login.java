package com.example.hotelmanagement.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.ui.admin.AddHotelActivity;
import com.example.hotelmanagement.ui.home.HomeActivity;
import com.example.hotelmanagement.ui.profile.profile;
import com.example.hotelmanagement.ui.register.register;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class login extends AppCompatActivity {
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView txtSignUp = findViewById(R.id.txtSignUp);

        auth = FirebaseAuth.getInstance();

        txtSignUp.setOnClickListener(v -> startActivity(new Intent(this, register.class)));
        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Nhap email");
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Nhap password");
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Dang nhap thanh cong", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, HomeActivity.class));
                        finish();
                    } else {
                        showLoginError(task.getException());
                    }
                });
    }

    private void showLoginError(Exception exception) {
        String message = "Dang nhap that bai";
        if (exception instanceof FirebaseAuthInvalidUserException) {
            message = "Tai khoan chua ton tai trong Firebase Authentication";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            message = "Sai mat khau hoac email";
        } else if (exception instanceof FirebaseNetworkException) {
            message = "Loi mang, khong ket noi duoc Firebase";
        } else if (exception instanceof FirebaseAuthException) {
            message = "Auth error: " + ((FirebaseAuthException) exception).getErrorCode();
        } else if (exception != null && exception.getMessage() != null) {
            message = exception.getMessage();
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
