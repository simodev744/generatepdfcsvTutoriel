package com.example.invoice.service;

import com.example.invoice.model.Invoice;
import com.example.invoice.model.OrderItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InvoicePdfService {

    public byte[] generateInvoicePdf(Invoice invoice) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLACK);
        Paragraph title = new Paragraph("Invoice", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Invoice Details
        Font detailFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
        document.add(new Paragraph("Invoice ID: " + invoice.getId(), detailFont));
        document.add(new Paragraph("Order ID: " + invoice.getOrderId(), detailFont));
        document.add(new Paragraph("Invoice Date: " + invoice.getInvoiceDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), detailFont));
        document.add(Chunk.NEWLINE);

        // Customer Details
        document.add(new Paragraph("Bill To:", detailFont));
        document.add(new Paragraph("Name: " + invoice.getCustomerName(), detailFont));
        document.add(new Paragraph("Email: " + invoice.getCustomerEmail(), detailFont));
        document.add(Chunk.NEWLINE);

        // Items Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Table Headers
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell cell;

        cell = new PdfPCell(new Phrase("Product Name", headerFont));
        cell.setBackgroundColor(BaseColor.GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Quantity", headerFont));
        cell.setBackgroundColor(BaseColor.GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Price", headerFont));
        cell.setBackgroundColor(BaseColor.GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Total", headerFont));
        cell.setBackgroundColor(BaseColor.GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        // Table Rows
        Font itemFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
        for (OrderItem item : invoice.getItems()) {
            table.addCell(new Phrase(item.getProductName(), itemFont));
            table.addCell(new Phrase(String.valueOf(item.getQuantity()), itemFont));
            table.addCell(new Phrase(String.format("%.2f", item.getPrice()), itemFont));
            table.addCell(new Phrase(String.format("%.2f", item.getQuantity() * item.getPrice()), itemFont));
        }

        document.add(table);

        // Total Amount
        Paragraph totalAmount = new Paragraph("Total Amount: " + String.format("%.2f", invoice.getTotalAmount()), detailFont);
        totalAmount.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalAmount);

        document.close();

        return baos.toByteArray();
    }
}
