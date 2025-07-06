package com.example.invoice.repository;

import com.example.invoice.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    @Query("{$or : [ { 'orderId' : { $regex: ?0, $options: 'i' } }, { 'customerName' : { $regex: ?0, $options: 'i' } }, { 'customerEmail' : { $regex: ?0, $options: 'i' } } ]}")
    List<Invoice> findByOrderIdOrCustomerNameOrCustomerEmailContainingIgnoreCase(String searchTerm);

    List<Invoice> findByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}