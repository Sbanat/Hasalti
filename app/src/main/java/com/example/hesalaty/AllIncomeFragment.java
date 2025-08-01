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

public class AllIncomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText editSearch;
    private TransactionAdapter adapter;
    private JSONArray allIncomes = new JSONArray();

    public AllIncomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_income, container, false);

        recyclerView = view.findViewById(R.id.recyclerAllIncome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        editSearch = view.findViewById(R.id.editSearchIncome);

        adapter = new TransactionAdapter(new JSONArray());
        recyclerView.setAdapter(adapter);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterIncomes(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadIncomes();
    }

    private void loadIncomes() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserData", getContext().MODE_PRIVATE);
        String token = prefs.getString("token", "");
        String url = "http://10.0.2.2:8000/api/incomes";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            response.getJSONObject(i).put("type", "مدخول");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    allIncomes = response;
                    adapter.updateData(response);
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

    private void filterIncomes(String keyword) {
        JSONArray filtered = new JSONArray();
        for (int i = 0; i < allIncomes.length(); i++) {
            try {
                JSONObject obj = allIncomes.getJSONObject(i);
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
