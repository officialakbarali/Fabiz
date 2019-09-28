package com.officialakbarali.fabiz.customer.sale.data;

public class SalesReturnReviewItem {
    private int id;
    private int billId;
    private String date;
    private int itemId;
    private String name;
    private String brand;
    private String catagory;
    private double price;
    private int qty;
    private double total;

    public SalesReturnReviewItem(int id, int billId, String date, int itemId, String name, String brand, String catagory, double price, int qty, double total) {
        this.id = id;
        this.billId = billId;
        this.date = date;
        this.itemId = itemId;
        this.name = name;
        this.brand = brand;
        this.catagory = catagory;
        this.price = price;
        this.qty = qty;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public int getBillId() {
        return billId;
    }

    public String getDate() {
        return date;
    }

    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getCatagory() {
        return catagory;
    }

    public double getPrice() {
        return price;
    }

    public int getQty() {
        return qty;
    }

    public double getTotal() {
        return total;
    }
}
