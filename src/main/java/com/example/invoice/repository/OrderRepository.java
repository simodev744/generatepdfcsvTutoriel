package com.example.invoice.repository;

import com.example.invoice.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    @Query("{$or : [ { 'customerName' : { $regex: ?0, $options: 'i' } }, { 'customerEmail' : { $regex: ?0, $options: 'i' } } ]}")
    List<Order> findByCustomerNameOrCustomerEmailContainingIgnoreCase(String searchTerm);

    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}