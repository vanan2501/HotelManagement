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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {
    private EditText edtName, edtPhone, edtEmail, edtPassword, edtConfirm;
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

        // NAME
        if(name.isEmpty()){
            edtName.setError("Nhập tên");
            return;
        }

// PHONE
        if(phone.isEmpty()){
            edtPhone.setError("Nhập số điện thoại");
            return;
        }

        if(!phone.matches("^0[0-9]{9}$")){
            edtPhone.setError("SĐT không hợp lệ");
            return;
        }

// EMAIL
        if(email.isEmpty()){
            edtEmail.setError("Nhập email");
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError("Email không hợp lệ");
            return;
        }

// PASSWORD
        if(password.length() < 6){
            edtPassword.setError("Password >= 6 ký tự");
            return;
        }

// CONFIRM
        if(!password.equals(confirm)) {
            edtConfirm.setError("Mật khẩu không khớp");
            return;
        }
        // 🔐 Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        String uid = auth.getCurrentUser().getUid();

                        // 📦 data Firestore
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
                                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    finish();
                                });

                    } else {
                        Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}