package com.unimag.emitix.service;

import com.unimag.emitix.entity.Invoice;
import com.unimag.emitix.entity.InvoiceItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class TaxCalculationService {

    @Value("${app.invoice.tax-rate:0.19}")
    private BigDecimal defaultTaxRate;

    /**
     * Calculates and updates all monetary fields for each item in the invoice,
     * then recalculates invoice totals.
     */
    public void calculateInvoiceTotals(Invoice invoice) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (InvoiceItem item : invoice.getItems()) {
            calculateItemAmounts(item);
            subtotal = subtotal.add(item.getSubtotal());
            
            // Calculate item tax on the fly since InvoiceItem no longer stores taxAmount
            BigDecimal taxRate = (item.getTaxRate() != null) ? item.getTaxRate() : defaultTaxRate;
            BigDecimal itemTax = item.getSubtotal().multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
            totalTax = totalTax.add(itemTax);
        }

        invoice.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        invoice.setTaxTotal(totalTax.setScale(2, RoundingMode.HALF_UP));
        invoice.setTotal(subtotal.add(totalTax).setScale(2, RoundingMode.HALF_UP));

        log.debug("Invoice totals calculated — subtotal: {}, taxTotal: {}, total: {}",
                invoice.getSubtotal(), invoice.getTaxTotal(), invoice.getTotal());
    }

    /**
     * Calculates subtotal for a single invoice item.
     */
    public void calculateItemAmounts(InvoiceItem item) {
        BigDecimal taxRate = (item.getTaxRate() != null) ? item.getTaxRate() : defaultTaxRate;
        BigDecimal qty = item.getQuantity();
        BigDecimal unitPrice = item.getUnitPrice();
        BigDecimal discountPercent = (item.getDiscount() != null) ? item.getDiscount() : BigDecimal.ZERO;

        BigDecimal baseAmount = qty.multiply(unitPrice);
        BigDecimal discountAmount = baseAmount.multiply(discountPercent.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal itemSubtotal = baseAmount.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);

        item.setTaxRate(taxRate);
        item.setSubtotal(itemSubtotal);
    }
}
