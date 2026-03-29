package com.hospital.billingservice.repository;

import com.hospital.billingservice.model.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, UUID> {
    
    Optional<Insurance> findByPolicyNumber(String policyNumber);
    
    List<Insurance> findByPatientId(UUID patientId);
    
    List<Insurance> findByInsuranceProvider(String insuranceProvider);
    
    List<Insurance> findByIsActive(Boolean isActive);
    
    @Query("SELECT i FROM Insurance i WHERE i.patientId = :patientId AND i.isActive = true AND i.validFrom <= :date AND i.validTo >= :date")
    List<Insurance> findActiveInsurancesByPatientIdAndDate(
            @Param("patientId") UUID patientId,
            @Param("date") LocalDate date);
    
    @Query("SELECT i FROM Insurance i WHERE i.validTo < :date AND i.isActive = true")
    List<Insurance> findExpiredInsurances(@Param("date") LocalDate date);
    
    @Query("SELECT i FROM Insurance i WHERE i.validTo BETWEEN :startDate AND :endDate AND i.isActive = true")
    List<Insurance> findInsurancesExpiringBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(i.remainingAmount) FROM Insurance i WHERE i.patientId = :patientId AND i.isActive = true")
    Optional<Double> getTotalRemainingCoverageByPatientId(@Param("patientId") UUID patientId);
}