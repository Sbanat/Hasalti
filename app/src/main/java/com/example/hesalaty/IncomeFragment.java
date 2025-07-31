package com.example.hesalaty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.android.volley.*;
import com.android.volley.toolbox.*;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;
import android.util.Log;

public class IncomeFragment extends Fragment {

    private EditText editIncomeName, editIncomeAmount;
    private Spinner spinnerIncomeCurrency;
    private Button btnSaveIncome;

    public IncomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income, container, false);

        editIncomeName = view.findViewById(R.id.editIncomeName);
        editIncomeAmount = view.findViewById(R.id.editIncomeAmount);
        spinnerIncomeCurrency = view.findViewById(R.id.spinnerIncomeCurrency);
        btnSaveIncome = view.findViewById(R.id.btnSaveIncome);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.currencies_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIncomeCurrency.setAdapter(adapter);

        btnSaveIncome.setOnClickListener(v -> {
            String name = editIncomeName.getText().toString().trim();
            String amount = editIncomeAmount.getText().toString().trim();
            String currency = spinnerIncomeCurrency.getSelectedItem().toString();

            if (name.isEmpty() || amount.isEmpty()) {
                Toast.makeText(getContext(), "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
                return;
            }

            saveIncome(name, amount, currency);
        });

        return view;
    }

    private void saveIncome(String name, String amount, String currency) {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            Toast.makeText(getContext(), "مشكلة في المصادقة", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/incomes";

        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("amount", amount);
        params.put("currency", currency);

        // إضافة التاريخ الحالي بصيغة yyyy-MM-dd
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        params.put("date", currentDate);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                response -> {
                    Toast.makeText(getContext(), "تم حفظ المدخول بنجاح", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                },
                error -> {
                    error.printStackTrace();
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String body = new String(error.networkResponse.data);
                        Log.e("VolleyError", "Response: " + body);
                    }
                    Toast.makeText(getContext(), "فشل حفظ المدخول", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }
}
