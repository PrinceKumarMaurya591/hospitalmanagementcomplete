package com.hospital.billingservice.repository;

import com.hospital.billingservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByBillId(UUID billId);
    
    List<Payment> findByPatientId(UUID patientId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    @Query("SELECT p FROM Payment p WHERE p.patientId = :patientId AND p.paymentDate BETWEEN :startDate AND :endDate ORDER BY p.paymentDate DESC")
    List<Payment> findByPatientIdAndPaymentDateBetween(
            @Param("patientId") UUID patientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.billId = :billId AND p.status = 'SUCCESS'")
    Optional<Double> getTotalPaidAmountByBillId(@Param("billId") UUID billId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.patientId = :patientId AND p.status = 'SUCCESS' AND p.paymentDate BETWEEN :startDate AND :endDate")
    Optional<Double> getTotalPaymentByPatientIdAndDateRange(
            @Param("patientId") UUID patientId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.status = 'SUCCESS'")
    Long countSuccessfulPaymentsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.status = 'SUCCESS'")
    Optional<Double> getTotalRevenueByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}