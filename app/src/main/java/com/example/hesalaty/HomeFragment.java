package com.example.hesalaty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import org.json.*;

import java.util.*;

public class HomeFragment extends Fragment {

    TextView txtShekel, txtDollar, txtDinar, txtWelcome;
    Button btnAddIncome, btnAddExpense, btnReports, btnViewAllIncome, btnViewAllExpenses;
    RecyclerView recyclerView;
    TransactionAdapter adapter;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        txtShekel = view.findViewById(R.id.txtShekel);
        txtDollar = view.findViewById(R.id.txtDollar);
        txtDinar = view.findViewById(R.id.txtDinar);
        txtWelcome = view.findViewById(R.id.txtWelcome);
        btnAddIncome = view.findViewById(R.id.btnAddIncome);
        btnAddExpense = view.findViewById(R.id.btnAddExpense);
        btnReports = view.findViewById(R.id.btnReports);
        btnViewAllIncome = view.findViewById(R.id.btnViewAllIncome);
        btnViewAllExpenses = view.findViewById(R.id.btnViewAllExpenses);
        recyclerView = view.findViewById(R.id.recyclerTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionAdapter(new JSONArray());
        recyclerView.setAdapter(adapter);

        btnAddIncome.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new IncomeFragment()));
        btnAddExpense.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new ExpenseFragment()));
        btnReports.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new ReportFragment()));
        btnViewAllIncome.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new AllIncomeFragment()));
        btnViewAllExpenses.setOnClickListener(v -> ((MainActivity) getActivity()).loadFragment(new AllExpensesFragment()));

        loadDashboard();
        loadUserName();

        return view;
    }

    private void loadDashboard() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserData", getContext().MODE_PRIVATE);
        String token = prefs.getString("token", "");

        String url = "http://10.0.2.2:8000/api/recent-transactions";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject balances = response.getJSONObject("balances");
                        JSONArray transactions = response.getJSONArray("transactions");

                        txtShekel.setText("₪ " + String.format("%.0f", balances.optDouble("شيكل", 0)));
                        txtDollar.setText("$ " + String.format("%.0f", balances.optDouble("دولار", 0)));
                        txtDinar.setText("JD " + String.format("%.0f", balances.optDouble("دينار", 0)));

                        adapter.updateData(transactions);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "فشل تحميل البيانات", Toast.LENGTH_SHORT).show()
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

    private void loadUserName() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");

        String url = "http://10.0.2.2:8000/api/me";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String name = response.getString("name");
                        txtWelcome.setText("أهلاً، " + name + " ");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        txtWelcome.setText("مرحبًا ");
                    }
                },
                error -> {
                    error.printStackTrace();
                    txtWelcome.setText("مرحبًا ");
                }
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
