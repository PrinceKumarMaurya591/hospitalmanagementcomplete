package com.hospital.billingservice.repository;

import com.hospital.billingservice.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
    
    Optional<Bill> findByInvoiceNumber(String invoiceNumber);
    
    List<Bill> findByPatientId(UUID patientId);
    
    List<Bill> findByAppointmentId(UUID appointmentId);
    
    List<Bill> findByStatus(Bill.BillStatus status);
    
    List<Bill> findByDueDateBeforeAndStatusNot(LocalDate date, Bill.BillStatus status);
    
    @Query("SELECT b FROM Bill b WHERE b.patientId = :patientId AND b.status IN :statuses ORDER BY b.generatedDate DESC")
    List<Bill> findByPatientIdAndStatusIn(@Param("patientId") UUID patientId, @Param("statuses") List<Bill.BillStatus> statuses);
    
    @Query("SELECT SUM(b.totalAmount) FROM Bill b WHERE b.patientId = :patientId AND b.status = :status")
    Optional<Double> getTotalAmountByPatientIdAndStatus(@Param("patientId") UUID patientId, @Param("status") Bill.BillStatus status);
    
    @Query("SELECT COUNT(b) FROM Bill b WHERE b.generatedDate BETWEEN :startDate AND :endDate")
    Long countByGeneratedDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(b.totalAmount) FROM Bill b WHERE b.generatedDate BETWEEN :startDate AND :endDate AND b.status = :status")
    Optional<Double> getTotalRevenueByDateRangeAndStatus(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate, 
            @Param("status") Bill.BillStatus status);
}