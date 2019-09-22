package com.officialakbarali.fabiz.customer.sale.data;

public class Cart {
    private int id;
    private int billid;
    private String name;
    private String brand;
    private String category;
    private double price;
    private int qty;
    private double total;
    private int returnQty;

    public Cart(int id, int billid, String name, String brand, String category, double price, int qty, double total, int returnQty) {
        this.id = id;
        this.billid = billid;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.qty = qty;
        this.total = total;
        this.returnQty = returnQty;
    }

    public int getId() {
        return id;
    }

    public int getBillid() {
        return billid;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
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

    public int getReturnQty() {
        return returnQty;
    }

}

