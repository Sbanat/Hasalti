package com.example.hesalaty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnLogin, btnGoToRegister;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail); // ✅ تغيير إلى بريد إلكتروني
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        queue = Volley.newRequestQueue(this);

        btnLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "يرجى إدخال البريد وكلمة المرور", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String url = "http://10.0.2.2:8000/api/login";
                JSONObject data = new JSONObject();
                data.put("email", email);
                data.put("password", password);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                        response -> {
                            try {
                                String token = response.getString("access_token");

                                SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                                prefs.edit().putString("token", token).apply();

                                Toast.makeText(this, "تم تسجيل الدخول", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } catch (Exception e) {
                                Toast.makeText(this, "خطأ في الاستجابة", Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> Toast.makeText(this, "فشل تسجيل الدخول", Toast.LENGTH_SHORT).show()
                );

                queue.add(request);

            } catch (Exception e) {
                Toast.makeText(this, "حدث خطأ", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoToRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }
}
