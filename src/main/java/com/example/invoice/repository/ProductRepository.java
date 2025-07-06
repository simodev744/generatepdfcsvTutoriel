package com.example.invoice.repository;

import com.example.invoice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByDescriptionContainingIgnoreCase(String description);

    @Query("{ 'price' : { $gte: ?0, $lte: ?1 } }")
    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    @Query("{$or : [ { 'name' : { $regex: ?0, $options: 'i' } }, { 'description' : { $regex: ?0, $options: 'i' } } ]}")
    List<Product> findByNameOrDescriptionContainingIgnoreCase(String searchTerm);
}