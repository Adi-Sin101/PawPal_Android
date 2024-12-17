package com.example.pawpal_18nov;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<String> expenseList;

    public ExpenseAdapter(List<String> expenseList) {
        this.expenseList = expenseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String expense = expenseList.get(position);
        holder.tvExpense.setText(expense);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExpense;

        public ViewHolder(View itemView) {
            super(itemView);
            tvExpense = itemView.findViewById(R.id.tv_expense);
        }
    }
}

