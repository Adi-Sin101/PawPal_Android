package com.example.pawpal_18nov;



public class Expense {
    private String name;
    private double amount;


    public Expense() {
        // Default constructor required for Firebase
    }

    public Expense(String description, double amount) {
        this.name = description;
        this.amount = amount;

    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }


}