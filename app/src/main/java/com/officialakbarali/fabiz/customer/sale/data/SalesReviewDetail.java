package com.officialakbarali.fabiz.customer.sale.data;

public class SalesReviewDetail {
    private int id;
    private String date;
    private int qty;
    private double total;

    public SalesReviewDetail(int id, String date, int qty, double total) {
        this.id = id;
        this.date = date;
        this.qty = qty;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public int getQty() {
        return qty;
    }

    public double getTotal() {
        return total;
    }
}
