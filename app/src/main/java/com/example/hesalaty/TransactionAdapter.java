package com.example.hesalaty;

import android.graphics.Color;
import android.view.*;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.*;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private JSONArray transactions;

    public TransactionAdapter(JSONArray data) {
        this.transactions = data;
    }

    public void updateData(JSONArray newData) {
        this.transactions = newData;
        notifyDataSetChanged();
    }

    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionAdapter.ViewHolder holder, int position) {
        try {
            JSONObject obj = transactions.getJSONObject(position);
            String name = obj.getString("name");
            double amount = obj.getDouble("amount");
            String type = obj.optString("type", "مدخول");

            holder.txtName.setText(name);

            if (type.equals("مصروف")) {
                holder.txtAmount.setText("- " + amount);
                holder.txtAmount.setTextColor(Color.RED);
            } else {
                holder.txtAmount.setText("+ " + amount);
                holder.txtAmount.setTextColor(Color.parseColor("#4CAF50")); // أخضر
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return transactions.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtTransactionName);
            txtAmount = itemView.findViewById(R.id.txtTransactionAmount);
        }
    }
}
