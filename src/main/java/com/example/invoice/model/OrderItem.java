package com.example.invoice.model;

import lombok.Data;

@Data
public class OrderItem {
    private String productId;
    private String productName;
    private int quantity;
    private double price;
}
