package com.example.invoice.controller;

import com.example.invoice.model.Invoice;
import com.example.invoice.model.OrderItem;
import com.example.invoice.repository.InvoiceRepository;
import com.example.invoice.service.InvoicePdfService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoicePdfService invoicePdfService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public String getAllInvoices(Model model,
                                 @RequestParam(value = "search", required = false) String search,
                                 @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                 @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Invoice> invoices;
        if (search != null && !search.isEmpty()) {
            invoices = invoiceRepository.findByOrderIdOrCustomerNameOrCustomerEmailContainingIgnoreCase(search);
        } else if (startDate != null && endDate != null) {
            invoices = invoiceRepository.findByInvoiceDateBetween(startDate, endDate);
        } else {
            invoices = invoiceRepository.findAll();
        }
        System.out.println("Fetched " + invoices.size() + " invoices.");
        model.addAttribute("invoices", invoices);
        model.addAttribute("search", search);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "invoices";
    }

    @GetMapping("/new")
    public String showAddInvoiceForm(Model model) {
        model.addAttribute("invoice", new Invoice());
        return "invoice-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditInvoiceForm(@PathVariable String id, Model model) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent()) {
            model.addAttribute("invoice", invoice.get());
            return "invoice-form";
        } else {
            return "redirect:/invoices";
        }
    }

    @PostMapping("/save")
    public String saveInvoice(@ModelAttribute Invoice invoice, @RequestParam("itemsJson") String itemsJson, Model model) {
        try {
            List<OrderItem> items = objectMapper.readValue(itemsJson, new TypeReference<List<OrderItem>>() {});
            invoice.setItems(items);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Invalid JSON for invoice items.");
            return "invoice-form";
        }
        invoice.setInvoiceDate(LocalDateTime.now());
        invoiceRepository.save(invoice);
        System.out.println("Saved invoice with ID: " + invoice.getId());
        return "redirect:/invoices";
    }

    @PostMapping("/update")
    public String updateInvoice(@ModelAttribute Invoice invoice, @RequestParam("itemsJson") String itemsJson, Model model) {
        try {
            List<OrderItem> items = objectMapper.readValue(itemsJson, new TypeReference<List<OrderItem>>() {});
            invoice.setItems(items);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Invalid JSON for invoice items.");
            return "invoice-form";
        }
        invoiceRepository.save(invoice);
        return "redirect:/invoices";
    }

    @GetMapping("/delete/{id}")
    public String deleteInvoice(@PathVariable String id) {
        invoiceRepository.deleteById(id);
        return "redirect:/invoices";
    }

    @GetMapping("/generate/{id}")
    public ResponseEntity<byte[]> generateInvoicePdf(@PathVariable String id) {
        Optional<Invoice> invoiceOptional = invoiceRepository.findById(id);
        if (invoiceOptional.isPresent()) {
            Invoice invoice = invoiceOptional.get();
            try {
                byte[] pdfBytes = invoicePdfService.generateInvoicePdf(invoice);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                String filename = "invoice-" + id + ".pdf";
                headers.setContentDispositionFormData(filename, filename);
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            } catch (DocumentException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}