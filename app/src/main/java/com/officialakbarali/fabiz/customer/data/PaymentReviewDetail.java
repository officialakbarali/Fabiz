package com.officialakbarali.fabiz.customer.data;

public class PaymentReviewDetail {
    private int id;
    private String date;
    private double amount;
    private double billId;


    public PaymentReviewDetail(int id, String date, double amount, int billId) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.billId = billId;
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

    public double getBillId() {
        return billId;
    }
}
