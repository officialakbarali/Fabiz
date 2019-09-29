package com.officialakbarali.fabiz.customer.data;

public class PaymentReviewDetail {
    private int id;
    private String date;
    private double amount;
    private double total;
    private double paid;
    private double due;

    public PaymentReviewDetail(int id, String date, double amount, double total, double paid, double due) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.total = total;
        this.paid = paid;
        this.due = due;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public double getTotal() {
        return total;
    }

    public double getPaid() {
        return paid;
    }

    public double getDue() {
        return due;
    }
}
