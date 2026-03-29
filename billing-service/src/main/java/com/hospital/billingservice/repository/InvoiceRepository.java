package com.hospital.billingservice.repository;

import com.hospital.billingservice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    
    Optional<Invoice> findByBillId(UUID billId);
    
    void deleteByBillId(UUID billId);
}