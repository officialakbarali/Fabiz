package com.officialakbarali.fabiz.item.data;

public class ItemDetail {
    private int id;
    private String name;
    private String brand;
    private String category;
    private double price;

    public ItemDetail(int id, String name, String brand, String catagory, double price) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = catagory;
        this.price = price;
    }

    public int getId() {
        return id;
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
}
