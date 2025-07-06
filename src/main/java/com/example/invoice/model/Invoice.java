package com.example.invoice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
public class Invoice {
    @Id
    private String id;
    private String orderId;
    private String customerName;
    private String customerEmail;
    private List<OrderItem> items;
    private double totalAmount;
    private LocalDateTime invoiceDate;
}
