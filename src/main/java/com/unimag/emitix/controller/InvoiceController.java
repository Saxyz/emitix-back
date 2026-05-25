package com.unimag.emitix.controller;

import com.unimag.emitix.dto.CreateInvoiceRequest;
import com.unimag.emitix.dto.InvoiceResponse;
import com.unimag.emitix.dto.PageResponse;
import com.unimag.emitix.entity.Invoice;
import com.unimag.emitix.entity.enums.InvoiceStatus;
import com.unimag.emitix.service.InvoicePdfService;
import com.unimag.emitix.service.InvoiceProcessingService;
import com.unimag.emitix.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceProcessingService invoiceProcessingService;
    private final InvoicePdfService invoicePdfService;

    @GetMapping
    public ResponseEntity<PageResponse<InvoiceResponse>> getInvoices(
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) String buyerName,
            @RequestParam(required = false) String invoiceNumber,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(invoiceService.findAll(status, buyerName, invoiceNumber, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request,
            Principal principal) {
        String createdBy = principal != null ? principal.getName() : "system";
        return ResponseEntity.ok(invoiceService.create(request, createdBy));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<InvoiceResponse> confirmInvoice(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceProcessingService.confirmInvoice(id));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable UUID id) {
        Invoice invoice = invoiceService.getInvoiceOrThrow(id);
        byte[] pdfBytes = invoicePdfService.generatePdf(invoice);

        String filename = "factura-" + invoice.getPrefix() + "-" + invoice.getNumber() + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
