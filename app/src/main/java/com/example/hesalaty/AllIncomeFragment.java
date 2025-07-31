package com.example.hesalaty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.Map;
import java.util.HashMap;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AllIncomeFragment extends Fragment {

    ListView listView;
    EditText searchField;
    ArrayAdapter<String> adapter;
    ArrayList<String> incomeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_income, container, false);

        listView = view.findViewById(R.id.listAllIncome);
        searchField = view.findViewById(R.id.editSearchIncome);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, incomeList);
        listView.setAdapter(adapter);

        fetchIncomes();

        searchField.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        return view;
    }

    private void fetchIncomes() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String token = prefs.getString("token", null);
        if (token == null) return;

        String url = "http://10.0.2.2:8000/api/incomes";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    incomeList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject income = response.getJSONObject(i);
                            String display = income.getString("name") + " - " +
                                    income.getString("amount") + " " +
                                    income.getString("currency") + "\n" +
                                    income.getString("date");
                            incomeList.add(display);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(getContext(), "فشل تحميل المدخلات", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }
}