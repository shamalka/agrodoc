package com.snov.agrodoc.Models;

public class Product {
    String ProductName;
    String SupplierName;
    String Price;
    String Rating;
    String Type;
    String Timestamp;


    public String getProductName() {
        return ProductName;
    }

    public String getSupplierName() {
        return SupplierName;
    }

    public String getPrice() {
        return Price;
    }

    public String getRating() {
        return Rating;
    }

    public String getType() {
        return Type;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public void setSupplierName(String supplierName) {
        SupplierName = supplierName;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }
}
