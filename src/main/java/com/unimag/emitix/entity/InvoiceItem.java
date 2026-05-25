package com.unimag.emitix.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem extends BaseEntity {

    // Muchos ítems pertenecen a una factura
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    // Ítem puede estar vinculado al catálogo de productos (opcional)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "product_id")
    private Product product;

    // Número de línea dentro de la factura (UBL 2.1 obligatorio)
    @Column(name = "line_number", nullable = false)
    @Builder.Default
    private int lineNumber = 1;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "unspsc_code", length = 8)
    private String unspscCode;

    @Column(name = "unit", nullable = false, length = 10)
    private String unit;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_pct", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountPct = BigDecimal.ZERO;

    @Column(name = "tax_type", length = 10)
    private String taxType;

    @Column(name = "tax_rate", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.ZERO;

    // Valor calculado del impuesto para esta línea
    @Column(name = "tax_total", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(name = "subtotal", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;
}
