package com.example.hesalaty;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;

public class CurrencyConverter extends AsyncTask<Void, Void, JSONObject> {

    public interface OnExchangeResult {
        void onResult(JSONObject result);
    }

    private final Context context;
    private final OnExchangeResult callback;
    private final String API_KEY = "aa360c621036f5ad34a7ec5e"; 

    public CurrencyConverter(Context context, OnExchangeResult callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        try {
            URL url = new URL("https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD");

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != 200) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            return new JSONObject(jsonBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        callback.onResult(result);
    }
}
