package com.example.hesalaty;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

import java.util.HashMap;
import java.util.Map;

public class AllExpensesFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText editSearch;
    private TransactionAdapter adapter;
    private JSONArray allExpenses = new JSONArray();

    public AllExpensesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_expenses, container, false);

        recyclerView = view.findViewById(R.id.recyclerAllExpenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        editSearch = view.findViewById(R.id.editSearchExpense);

        adapter = new TransactionAdapter(new JSONArray());
        recyclerView.setAdapter(adapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExpenses(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenses(); // يتم التحديث تلقائيًا عند الرجوع إلى الفراجمنت
    }

    private void loadExpenses() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserData", getContext().MODE_PRIVATE);
        String token = prefs.getString("token", "");
        String url = "http://10.0.2.2:8000/api/expenses";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    allExpenses = response;
                    adapter.updateData(response);
                },
                error -> Toast.makeText(getContext(), "فشل تحميل المصروفات", Toast.LENGTH_SHORT).show()
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

    private void filterExpenses(String keyword) {
        JSONArray filtered = new JSONArray();
        for (int i = 0; i < allExpenses.length(); i++) {
            try {
                JSONObject obj = allExpenses.getJSONObject(i);
                if (obj.getString("name").contains(keyword)) {
                    filtered.put(obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter.updateData(filtered);
    }
}
