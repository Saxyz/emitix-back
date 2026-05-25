package com.unimag.emitix.service;

import com.unimag.emitix.dto.CreateInvoiceRequest;
import com.unimag.emitix.dto.InvoiceResponse;
import com.unimag.emitix.dto.PageResponse;
import com.unimag.emitix.entity.*;
import com.unimag.emitix.exception.ResourceNotFoundException;
import com.unimag.emitix.mapper.InvoiceMapper;
import com.unimag.emitix.repository.CompanyRepository;
import com.unimag.emitix.repository.BuyerRepository;
import com.unimag.emitix.repository.InvoiceRepository;
import com.unimag.emitix.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BuyerRepository buyerRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final InvoiceMapper invoiceMapper;

    @Transactional(readOnly = true)
    public PageResponse<InvoiceResponse> findAll(InvoiceStatus status, String buyerName,
                                                  String invoiceNumber, Pageable pageable) {
        Page<Invoice> page = invoiceRepository.findByFilters(status, buyerName, invoiceNumber, pageable);
        return PageResponse.of(page.map(invoiceMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public InvoiceResponse findById(UUID id) {
        Invoice invoice = getInvoiceOrThrow(id);
        return invoiceMapper.toResponse(invoice);
    }

    @Transactional
    public InvoiceResponse create(CreateInvoiceRequest request, String createdBy) {
        Buyer buyer = buyerRepository.findById(request.buyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.buyerId()));

        Company company = companyRepository.findFirstByOrderByCreatedAtAsc()
                .orElseThrow(() -> new ResourceNotFoundException("Empresa emisora", "registro", "único"));

        User user = userRepository.findByUsername(createdBy)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", createdBy));

        Invoice invoice = Invoice.builder()
                .prefix("DRAFT")
                .number(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .buyer(buyer)
                .company(company)
                .status(InvoiceStatus.DRAFT)
                .createdBy(user)
                .build();

        List<InvoiceItem> items = request.items().stream().map(itemReq -> {
            BigDecimal qty = itemReq.quantity();
            BigDecimal price = itemReq.unitPrice();
            BigDecimal subtotal = qty.multiply(price).setScale(2, RoundingMode.HALF_UP);
            BigDecimal taxRate = itemReq.taxRate() != null ? itemReq.taxRate() : BigDecimal.valueOf(0.19);

            return InvoiceItem.builder()
                    .description(itemReq.description())
                    .quantity(qty)
                    .unitPrice(price)
                    .taxRate(taxRate)
                    .unit("UN") // default unit
                    .discount(BigDecimal.ZERO)
                    .taxType("01") // standard VAT (IVA)
                    .subtotal(subtotal)
                    .build();
        }).toList();

        items.forEach(invoice::addItem);

        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice created: {} by {}", saved.getId(), createdBy);
        return invoiceMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Invoice getInvoiceOrThrow(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", "id", id));
    }
}
