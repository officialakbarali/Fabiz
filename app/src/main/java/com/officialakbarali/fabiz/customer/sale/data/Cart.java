package com.officialakbarali.fabiz.customer.sale.data;

public class Cart {
    private String id;
    private String billid;
    private  String itemId;
    private String name;
    private String brand;
    private String category;
    private double price;
    private int qty;
    private double total;
    private int returnQty;

    public Cart(String id, String billid,String itemId, String name, String brand, String category, double price, int qty, double total, int returnQty) {
        this.id = id;
        this.billid = billid;
       this.itemId = itemId;
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.qty = qty;
        this.total = total;
        this.returnQty = returnQty;
    }

    public String  getId() {
        return id;
    }

    public String getBillid() {
        return billid;
    }

    public String getItemId() {
        return itemId;
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

