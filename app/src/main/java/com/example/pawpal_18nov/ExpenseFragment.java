package com.example.pawpal_18nov;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

public class ExpenseFragment extends Fragment {

    private Button btnSelectMonth, btnAddExpense;
    private TextView tvSelectedMonth, tvMonthTotal, tvYearTotal;
    private EditText etExpenseName, etExpenseCost;
    private RecyclerView rvExpenseList;

    private ArrayList<String> expenseList;
    private ExpenseAdapter expenseAdapter;

    private double monthTotal = 0;
    private double yearTotal = 0;
    private int selectedYear, selectedMonth;
    private String userId;

    private DatabaseReference mDatabase;
    private String userUID = "user123"; // Replace with actual user UID from authentication

    @Override
    public void onStart() {
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference(userId + "/Expense");
        loadExpensesForSelectedMonth(selectedYear, selectedMonth);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("users").child(userUID).child("expenses");

        // Initialize UI components
        btnSelectMonth = view.findViewById(R.id.btn_select_month);
        btnAddExpense = view.findViewById(R.id.btn_add_expense);
        tvSelectedMonth = view.findViewById(R.id.tv_selected_month);
        tvMonthTotal = view.findViewById(R.id.tv_month_total);
        tvYearTotal = view.findViewById(R.id.tv_year_total);
        etExpenseName = view.findViewById(R.id.et_expense_name);
        etExpenseCost = view.findViewById(R.id.et_expense_cost);
        rvExpenseList = view.findViewById(R.id.rv_expense_list);

        // Set up RecyclerView
        expenseList = new ArrayList<>();
        expenseAdapter = new ExpenseAdapter(expenseList);
        rvExpenseList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenseList.setAdapter(expenseAdapter);

        // Set button click listeners
        btnSelectMonth.setOnClickListener(v -> showMonthPicker());
        btnAddExpense.setOnClickListener(v -> addExpense());

        return view;
    }

    private void showMonthPicker() {
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            selectedYear = year;
            selectedMonth = monthOfYear;

            String monthName = new DateFormatSymbols().getMonths()[monthOfYear];
            tvSelectedMonth.setText("Selected Month: " + monthName + " " + year);

            // Load and display expenses for the selected month
            loadExpensesForSelectedMonth(year, monthOfYear);

            // Calculate yearly total
            calculateYearlyTotal(year);

        }, selectedYear, selectedMonth, 1);

        datePickerDialog.show();
    }

    private void loadExpensesForSelectedMonth(int year, int month) {
        monthTotal = 0;  // Reset monthly total
        String monthKey = String.format("%04d-%02d", year, month + 1);

        mDatabase.child(monthKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expenseList.clear();
                for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                    String expense = expenseSnapshot.getValue(String.class);
                    if (expense != null) {
                        expenseList.add(expense);

                        // Calculate total
                        String[] expenseParts = expense.split(":");
                        if (expenseParts.length > 1) {
                            try {
                                double cost = Double.parseDouble(expenseParts[1].trim().replace("$", ""));
                                monthTotal += cost;
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                expenseAdapter.notifyDataSetChanged();

                // Update monthly total TextView
                String monthName = new DateFormatSymbols().getMonths()[month];
                tvMonthTotal.setText("Total for " + monthName + " " + year + ": $" + monthTotal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load expenses.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateYearlyTotal(int year) {
        yearTotal = 0;
        String yearKey = String.format("%04d", year);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot monthSnapshot : dataSnapshot.getChildren()) {
                    String key = monthSnapshot.getKey();
                    if (key != null && key.startsWith(yearKey)) {
                        for (DataSnapshot expenseSnapshot : monthSnapshot.getChildren()) {
                            String expense = expenseSnapshot.getValue(String.class);
                            if (expense != null) {
                                String[] expenseParts = expense.split(":");
                                if (expenseParts.length > 1) {
                                    try {
                                        double cost = Double.parseDouble(expenseParts[1].trim().replace("$", ""));
                                        yearTotal += cost;
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
                tvYearTotal.setText("Total for the Year " + year + ": $" + yearTotal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to calculate yearly total.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addExpense() {
        String expenseName = etExpenseName.getText().toString().trim();
        String expenseCost = etExpenseCost.getText().toString().trim();

        if (expenseName.isEmpty() || expenseCost.isEmpty()) {
            Toast.makeText(getContext(), "Please enter both name and cost of the expense.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double cost = Double.parseDouble(expenseCost);
            String monthKey = String.format("%04d-%02d", selectedYear, selectedMonth + 1);
            String expense = expenseName + ": $" + cost;

            // Add to Firebase
            mDatabase.child(monthKey).push().setValue(expense);

            // Add to local list and update UI
            expenseList.add(expense);
            expenseAdapter.notifyDataSetChanged();

            // Update totals
            monthTotal += cost;
            String monthName = new DateFormatSymbols().getMonths()[selectedMonth];
            tvMonthTotal.setText("Total for " + monthName + " " + selectedYear + ": $" + monthTotal);

            etExpenseName.setText("");
            etExpenseCost.setText("");

            Toast.makeText(getContext(), "Expense added successfully.", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid cost value.", Toast.LENGTH_SHORT).show();
        }
    }
}

