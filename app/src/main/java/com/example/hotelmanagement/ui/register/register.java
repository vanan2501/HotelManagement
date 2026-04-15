package com.example.hotelmanagement.ui.register;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hotelmanagement.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {
    private EditText edtName;
    private EditText edtPhone;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirm;
    private Button btnRegister;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);
        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirm = edtConfirm.getText().toString().trim();

        if (name.isEmpty()) {
            edtName.setError("Nhap ten");
            return;
        }

        if (phone.isEmpty()) {
            edtPhone.setError("Nhap so dien thoai");
            return;
        }

        if (!phone.matches("^0[0-9]{9}$")) {
            edtPhone.setError("SDT khong hop le");
            return;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Nhap email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email khong hop le");
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError("Password phai co it nhat 6 ky tu");
            return;
        }

        if (!password.equals(confirm)) {
            edtConfirm.setError("Mat khau khong khop");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = auth.getCurrentUser();
                        if (currentUser == null) {
                            Toast.makeText(this, "Dang ky that bai, vui long thu lai", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String uid = currentUser.getUid();
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("phone", phone);
                        user.put("email", email);
                        user.put("role_id", 1);
                        user.put("create_at", new Date());

                        db.collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Dang ky thanh cong", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    currentUser.delete();
                                    Toast.makeText(this, "Tao auth thanh cong nhung luu users that bai", Toast.LENGTH_LONG).show();
                                });
                    } else {
                        showRegisterError(task.getException());
                    }
                });
    }

    private void showRegisterError(Exception exception) {
        String message = "Dang ky that bai";
        if (exception instanceof FirebaseAuthUserCollisionException) {
            message = "Email da ton tai trong Firebase Authentication";
        } else if (exception instanceof FirebaseAuthWeakPasswordException) {
            message = "Mat khau qua yeu";
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            message = "Email khong hop le";
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
