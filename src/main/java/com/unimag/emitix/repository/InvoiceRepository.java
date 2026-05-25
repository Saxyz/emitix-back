package com.unimag.emitix.repository;

import com.unimag.emitix.entity.Invoice;
import com.unimag.emitix.entity.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Page<Invoice> findAll(Pageable pageable);

    Page<Invoice> findByBuyerId(UUID buyerId, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:buyerName IS NULL OR LOWER(i.buyer.fullName) LIKE LOWER(CONCAT('%', :buyerName, '%'))) AND " +
           "(:invoiceNumber IS NULL OR i.number LIKE CONCAT('%', :invoiceNumber, '%') OR CONCAT(i.prefix, i.number) LIKE CONCAT('%', :invoiceNumber, '%'))")
    Page<Invoice> findByFilters(
            @Param("status") InvoiceStatus status,
            @Param("buyerName") String buyerName,
            @Param("invoiceNumber") String invoiceNumber,
            Pageable pageable
    );

    @Query(value = "SELECT NEXTVAL('invoice_number_seq')", nativeQuery = true)
    Long getNextSequenceValue();
}
