package com.example.invoice.controller;

import com.example.invoice.model.Product;
import com.example.invoice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping
    public String getAllProducts(Model model, @RequestParam(value = "search", required = false) String search) {
        List<Product> products;
        if (search != null && !search.isEmpty()) {
            products = productRepository.findByNameOrDescriptionContainingIgnoreCase(search);
        } else {
            products = productRepository.findAll();
        }
        model.addAttribute("products", products);
        model.addAttribute("search", search);
        return "products";
    }

    @GetMapping("/new")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable String id, Model model) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product-form";
        } else {
            return "redirect:/products";
        }
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam(value = "images", required = false) MultipartFile[] images,
                              @RequestParam(value = "videos", required = false) MultipartFile[] videos) {
        handleFileUploads(product, images, videos);
        productRepository.save(product);
        return "redirect:/products";
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute Product product,
                              @RequestParam(value = "images", required = false) MultipartFile[] images,
                              @RequestParam(value = "videos", required = false) MultipartFile[] videos) {
        handleFileUploads(product, images, videos);
        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id) {
        productRepository.deleteById(id);
        return "redirect:/products";
    }

    private void handleFileUploads(Product product, MultipartFile[] images, MultipartFile[] videos) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            List<String> imageUrls = product.getImageUrls() != null ? new ArrayList<>(product.getImageUrls()) : new ArrayList<>();
            if (images != null) {
                for (MultipartFile file : images) {
                    if (!file.isEmpty()) {
                        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath);
                        imageUrls.add("/uploads/" + fileName);
                    }
                }
            }
            product.setImageUrls(imageUrls);

            List<String> videoUrls = product.getVideoUrls() != null ? new ArrayList<>(product.getVideoUrls()) : new ArrayList<>();
            if (videos != null) {
                for (MultipartFile file : videos) {
                    if (!file.isEmpty()) {
                        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(file.getInputStream(), filePath);
                        videoUrls.add("/uploads/" + fileName);
                    }
                }
            }
            product.setVideoUrls(videoUrls);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately, e.g., throw a custom exception or add to model for error display
        }
    }
}
