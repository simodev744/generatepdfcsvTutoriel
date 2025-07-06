package com.example.invoice.controller;

import com.example.invoice.model.Order;
import com.example.invoice.model.OrderItem;
import com.example.invoice.repository.OrderRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public String getAllOrders(Model model,
                               @RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                               @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Order> orders;
        if (search != null && !search.isEmpty()) {
            orders = orderRepository.findByCustomerNameOrCustomerEmailContainingIgnoreCase(search);
        } else if (startDate != null && endDate != null) {
            orders = orderRepository.findByOrderDateBetween(startDate, endDate);
        } else {
            orders = orderRepository.findAll();
        }
        System.out.println("Fetched " + orders.size() + " orders.");
        model.addAttribute("orders", orders);
        model.addAttribute("search", search);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "orders";
    }

    @GetMapping("/new")
    public String showAddOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "order-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditOrderForm(@PathVariable String id, Model model) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            model.addAttribute("order", order.get());
            return "order-form";
        } else {
            return "redirect:/orders";
        }
    }

    @PostMapping("/save")
    public String saveOrder(@ModelAttribute Order order, @RequestParam("orderItemsJson") String orderItemsJson, Model model) {
        try {
            List<OrderItem> orderItems = objectMapper.readValue(orderItemsJson, new TypeReference<List<OrderItem>>() {});
            order.setOrderItems(orderItems);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Invalid JSON for order items.");
            return "order-form";
        }
        order.setOrderDate(LocalDateTime.now());
        orderRepository.save(order);
        System.out.println("Saved order with ID: " + order.getId());
        return "redirect:/orders";
    }

    @PostMapping("/update")
    public String updateOrder(@ModelAttribute Order order, @RequestParam("orderItemsJson") String orderItemsJson, Model model) {
        try {
            List<OrderItem> orderItems = objectMapper.readValue(orderItemsJson, new TypeReference<List<OrderItem>>() {});
            order.setOrderItems(orderItems);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Invalid JSON for order items.");
            return "order-form";
        }
        orderRepository.save(order);
        System.out.println("Updated order with ID: " + order.getId());
        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable String id) {
        orderRepository.deleteById(id);
        return "redirect:/orders";
    }

    // Existing REST API methods (getAllOrders, getOrderById, createOrder, updateOrder, deleteOrder)
    // ... (These methods are already in the file, so I'm not re-writing them here)
}
