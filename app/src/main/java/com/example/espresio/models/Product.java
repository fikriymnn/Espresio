package com.example.espresio.models;

import java.util.List;

public class Product {
    private String id;
    private String name;
    private double price;
    private String description;
    private List<ProductIngredient> ingredients;
    private boolean isActive;
    private long createdAt;
    private long updatedAt;

    public Product() {}

    public Product(String name, double price, String description, List<ProductIngredient> ingredients) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.ingredients = ingredients;
        this.isActive = true;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<ProductIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<ProductIngredient> ingredients) { this.ingredients = ingredients; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
