package com.example.invoice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
public class Order {
    @Id
    private String id;
    private String customerName;
    private String customerEmail;
    private List<OrderItem> orderItems;
    private double totalAmount;
    private LocalDateTime orderDate;
}
