package com.example.espresio.models;

public class Ingredient {
    private String id;
    private String name;
    private double stock; // in grams
    private String unit;
    private double minStock; // minimum stock alert
    private long createdAt;
    private long updatedAt;

    public Ingredient() {}

    public Ingredient(String name, double stock, String unit, double minStock) {
        this.name = name;
        this.stock = stock;
        this.unit = unit;
        this.minStock = minStock;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getStock() { return stock; }
    public void setStock(double stock) { this.stock = stock; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getMinStock() { return minStock; }
    public void setMinStock(double minStock) { this.minStock = minStock; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
