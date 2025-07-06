package com.example.invoice.controller;

import com.example.invoice.repository.OrderRepository;
import com.example.invoice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/")
    public String home(Model model) {
        long productCount = productRepository.count();
        long orderCount = orderRepository.count();

        model.addAttribute("productCount", productCount);
        model.addAttribute("orderCount", orderCount);

        return "home";
    }
}