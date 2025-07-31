package com.example.hesalaty;

import android.content.Intent;
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

public class RegisterActivity extends AppCompatActivity {

    EditText editName, editEmail, editPassword, editPasswordConfirm;
    Button btnRegister;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editPasswordConfirm = findViewById(R.id.editPasswordConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        queue = Volley.newRequestQueue(this);

        btnRegister.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String passwordConfirm = editPasswordConfirm.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(passwordConfirm)) {
                Toast.makeText(this, "كلمتا المرور غير متطابقتين", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String url = "http://10.0.2.2:8000/api/register";
                JSONObject data = new JSONObject();
                data.put("name", name);
                data.put("email", email);
                data.put("password", password);
                data.put("password_confirmation", passwordConfirm);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                        response -> {
                            Toast.makeText(this, "تم إنشاء الحساب بنجاح", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        },
                        error -> {
                            String message = "فشل في إنشاء الحساب";
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    String body = new String(error.networkResponse.data, "UTF-8");
                                    message = "خطأ: " + body;
                                } catch (Exception ignored) {}
                            }
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                );

                queue.add(request);

            } catch (Exception e) {
                Toast.makeText(this, "حدث خطأ أثناء الإرسال", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
