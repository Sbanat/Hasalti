package com.example.hesalaty;

import android.content.*;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.*;
import android.widget.*;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgProfile;
    private TextView txtUsername, txtEmail, txtPin;
    private EditText editPhone;
    private Button btnSavePhone, btnLogout;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfile = view.findViewById(R.id.imgProfile);
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

        loadUserData();

        btnSavePhone.setOnClickListener(v -> {
            String newPhone = editPhone.getText().toString().trim();
            prefs.edit().putString("phone", newPhone).apply();
            Toast.makeText(getContext(), "تم حفظ رقم الجوال", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        imgProfile.setOnClickListener(v -> openImageChooser());

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                imgProfile.setImageBitmap(bitmap);
                // يمكنك هنا حفظ الصورة أو إرسالها للسيرفر لاحقًا
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
