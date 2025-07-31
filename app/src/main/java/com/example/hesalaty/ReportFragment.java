package com.example.hesalaty;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;

import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.*;

import java.util.*;

public class ReportFragment extends Fragment {

    private PieChart pieShekel, pieDollar, pieDinar;
    private TextView txtSummary;

    public ReportFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        txtSummary = view.findViewById(R.id.txtSummary);
        pieShekel = view.findViewById(R.id.pieShekel);
        pieDollar = view.findViewById(R.id.pieDollar);
        pieDinar = view.findViewById(R.id.pieDinar);

        loadData();

        return view;
    }

    private void loadData() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserData", getContext().MODE_PRIVATE);
        String token = prefs.getString("token", "");

        String url = "http://10.0.2.2:8000/api/transactions";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                this::processReportData,
                error -> {
                    txtSummary.setText("فشل تحميل البيانات");
                    error.printStackTrace();
                }
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

    private void processReportData(JSONArray array) {
        Map<String, Float> shekelMap = new HashMap<>();
        Map<String, Float> dollarMap = new HashMap<>();
        Map<String, Float> dinarMap = new HashMap<>();

        float shekelTotal = 0, dollarTotal = 0, dinarTotal = 0;

        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String type = obj.getString("type");
                String currency = obj.getString("currency");
                float amount = (float) obj.getDouble("amount");

                if (type.equals("مدخول")) {
                    switch (currency) {
                        case "شيكل": shekelMap.put("مدخول", shekelMap.get("مدخول") != null ? shekelMap.get("مدخول") + amount : amount); shekelTotal += amount; break;
                        case "دولار": dollarMap.put("مدخول", dollarMap.get("مدخول") != null ? dollarMap.get("مدخول") + amount : amount); dollarTotal += amount; break;
                        case "دينار": dinarMap.put("مدخول", dinarMap.get("مدخول") != null ? dinarMap.get("مدخول") + amount : amount); dinarTotal += amount; break;
                    }
                } else if (type.equals("مصروف")) {
                    switch (currency) {
                        case "شيكل": shekelMap.put("مصروف", shekelMap.get("مصروف") != null ? shekelMap.get("مصروف") + amount : amount); shekelTotal -= amount; break;
                        case "دولار": dollarMap.put("مصروف", dollarMap.get("مصروف") != null ? dollarMap.get("مصروف") + amount : amount); dollarTotal -= amount; break;
                        case "دينار": dinarMap.put("مصروف", dinarMap.get("مصروف") != null ? dinarMap.get("مصروف") + amount : amount); dinarTotal -= amount; break;
                    }
                }
            }

            txtSummary.setText("رصيد شيكل: ₪ " + shekelTotal +
                    "\nرصيد دولار: $ " + dollarTotal +
                    "\nرصيد دينار: JD " + dinarTotal);

            setupPieChart(pieShekel, shekelMap);
            setupPieChart(pieDollar, dollarMap);
            setupPieChart(pieDinar, dinarMap);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupPieChart(PieChart chart, Map<String, Float> dataMap) {
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : dataMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(14f);

        PieData pieData = new PieData(dataSet);
        chart.setData(pieData);
        chart.getDescription().setEnabled(false);
        chart.setCenterText("الإحصائية");
        chart.setCenterTextSize(16f);
        chart.invalidate(); // refresh
    }
}
