package com.example.hesalaty;

import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONObject;

import java.util.*;

public class ExchangeFragment extends Fragment {

    private EditText editAmount;
    private Spinner spinnerFrom, spinnerTo;
    private Button btnConvert;
    private TextView txtResult;

    public ExchangeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);

        editAmount = view.findViewById(R.id.editAmount);
        spinnerFrom = view.findViewById(R.id.spinnerFrom);
        spinnerTo = view.findViewById(R.id.spinnerTo);
        btnConvert = view.findViewById(R.id.btnConvert);
        txtResult = view.findViewById(R.id.txtResult);

        setupCurrencySpinners();

        btnConvert.setOnClickListener(v -> {
            String amountStr = editAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(getContext(), "يرجى إدخال المبلغ", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String from = spinnerFrom.getSelectedItem().toString();
            String to = spinnerTo.getSelectedItem().toString();

            convertCurrency(amount, from, to);
        });

        return view;
    }

    private void setupCurrencySpinners() {
        List<String> currencyList = Arrays.asList("شيكل", "دولار", "دينار");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_spinner_item,
                currencyList
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.WHITE); // لون النص المختار
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(Color.WHITE); // لون العناصر عند السحب
                view.setBackgroundColor(Color.parseColor("#1C2B3A")); // خلفية داكنة
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    private void convertCurrency(double amount, String from, String to) {
        String url = "https://api.exchangerate-api.com/v4/latest/" + mapCurrencyCode(from);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject rates = response.getJSONObject("rates");
                        double rate = rates.getDouble(mapCurrencyCode(to));
                        double result = amount * rate;
                        txtResult.setText("النتيجة: " + String.format("%.2f", result) + " " + to);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "حدث خطأ أثناء التحويل", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "فشل الاتصال بالخادم", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private String mapCurrencyCode(String currencyName) {
        switch (currencyName) {
            case "شيكل": return "ILS";
            case "دولار": return "USD";
            case "دينار": return "JOD";
            default: return "USD";
        }
    }
}
