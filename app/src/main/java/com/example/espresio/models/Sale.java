package com.example.espresio.models;

import java.util.List;

public class Sale {
    private String id;
    private String employeeId;
    private String employeeName;
    private List<SaleItem> items;
    private double totalAmount;
    private long saleDate;
    private String status; // "completed", "cancelled"

    public Sale() {}

    public Sale(String employeeId, String employeeName, List<SaleItem> items, double totalAmount) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.items = items;
        this.totalAmount = totalAmount;
        this.saleDate = System.currentTimeMillis();
        this.status = "completed";
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) { this.items = items; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public long getSaleDate() { return saleDate; }
    public void setSaleDate(long saleDate) { this.saleDate = saleDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
