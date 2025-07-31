package com.example.hesalaty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView txtUsername, txtEmail, txtPin;
    private EditText editPhone;
    private Button btnSavePhone, btnLogout;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtUsername = view.findViewById(R.id.txtUsername);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPin = view.findViewById(R.id.txtPin);
        editPhone = view.findViewById(R.id.editPhone);
        btnSavePhone = view.findViewById(R.id.btnSavePhone);
        btnLogout = view.findViewById(R.id.btnLogout);

        SharedPreferences prefs = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String pin = prefs.getString("pin", "----");
        String phone = prefs.getString("phone", "");

        txtPin.setText("PIN: " + pin);
        editPhone.setText(phone);

        // تحميل بيانات المستخدم من API
        loadUserData();

        // حفظ رقم الهاتف
        btnSavePhone.setOnClickListener(v -> {
            String newPhone = editPhone.getText().toString().trim();
            prefs.edit().putString("phone", newPhone).apply();
            Toast.makeText(getContext(), "تم حفظ رقم الجوال", Toast.LENGTH_SHORT).show();
        });

        // تسجيل الخروج
        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }

    private void loadUserData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");

        String url = "http://10.0.2.2:8000/api/me";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String name = response.getString("name");
                        String email = response.getString("email");

                        txtUsername.setText("الاسم: " + name);
                        txtEmail.setText("البريد: " + email);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "فشل في جلب البيانات", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "فشل الاتصال بالخادم", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }
}
