package com.unimag.emitix.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"company_id", "prefix", "number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice extends BaseEntity {

    // Muchas facturas pertenecen a una empresa emisora
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Muchas facturas tienen un comprador (adquiriente)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    // Muchas facturas se emiten bajo una resolución DIAN (opcional en DRAFT)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "resolution_id")
    private Resolution resolution;

    // Muchas facturas son creadas por un usuario
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "number", nullable = false, length = 20)
    private String number;

    @Column(name = "prefix", nullable = false, length = 10)
    private String prefix;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false, length = 20)
    @Builder.Default
    private InvoiceType invoiceType = InvoiceType.SALE;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "COP";

    @Column(name = "subtotal", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_total", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    // CUFE: código único de factura electrónica (SHA-384, 96 chars hex)
    @Column(name = "cufe", length = 96, unique = true)
    private String cufe;

    // URL del código QR obligatorio UBL 2.1
    @Column(name = "qr_url", columnDefinition = "TEXT")
    private String qrUrl;

    @Column(name = "pdf_url", columnDefinition = "TEXT")
    private String pdfUrl;

    @Column(name = "xml_url", columnDefinition = "TEXT")
    private String xmlUrl;

    // Observaciones libres del emisor
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Medio de pago (efectivo, transferencia, tarjeta, etc.)
    @Column(name = "payment_method", length = 20)
    private String paymentMethod;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    // Una factura tiene muchos ítems de línea
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
    }

    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
    }
}
