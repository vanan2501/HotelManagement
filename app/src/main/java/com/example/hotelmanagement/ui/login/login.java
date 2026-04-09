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

import com.example.hotelmanagement.MainActivity;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.ui.profile.profile;
import com.example.hotelmanagement.ui.register.register;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
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
        // ánh xạ view
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        TextView txtSignUp = findViewById(R.id.txtSignUp);

        // Firebase
        auth = FirebaseAuth.getInstance();
        txtSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, register.class));
        });

        btnLogin.setOnClickListener(v -> login());
    }
    private void login() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if(email.isEmpty()){
            edtEmail.setError("Nhập email");
            return;
        }

        if(password.isEmpty()){
            edtPassword.setError("Nhập password");
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(this, profile.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Sai email hoặc password", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}