package com.hospital.billingservice.service;

import com.hospital.billingservice.model.Insurance;
import com.hospital.billingservice.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsuranceService {
    
    private final InsuranceRepository insuranceRepository;
    
    public List<Insurance> getInsurancesByPatientId(UUID patientId) {
        log.info("Fetching insurances for patient ID: {}", patientId);
        return insuranceRepository.findByPatientId(patientId);
    }
    
    public Optional<Insurance> getInsuranceByPolicyNumber(String policyNumber) {
        log.info("Fetching insurance by policy number: {}", policyNumber);
        return insuranceRepository.findByPolicyNumber(policyNumber);
    }
    
    public Optional<Insurance> getFirstActiveInsuranceByPatientId(UUID patientId) {
        log.info("Fetching first active insurance for patient ID: {}", patientId);
        List<Insurance> insurances = insuranceRepository.findActiveInsurancesByPatientIdAndDate(
                patientId, LocalDate.now());
        return insurances.isEmpty() ? Optional.empty() : Optional.of(insurances.get(0));
    }
    
    @Transactional
    public Insurance createOrUpdateInsurance(Insurance insurance) {
        log.info("Creating or updating insurance for patient ID: {}", insurance.getPatientId());
        
        // Check if insurance already exists with this policy number
        Optional<Insurance> existingInsurance = insuranceRepository.findByPolicyNumber(insurance.getPolicyNumber());
        
        if (existingInsurance.isPresent()) {
            Insurance existing = existingInsurance.get();
            existing.setInsuranceProvider(insurance.getInsuranceProvider());
            existing.setPolicyHolderName(insurance.getPolicyHolderName());
            existing.setCoverageType(insurance.getCoverageType());
            existing.setCoverageAmount(insurance.getCoverageAmount());
            existing.setRemainingAmount(insurance.getRemainingAmount());
            existing.setValidFrom(insurance.getValidFrom());
            existing.setValidTo(insurance.getValidTo());
            existing.setIsActive(insurance.getIsActive());
            existing.setContactNumber(insurance.getContactNumber());
            existing.setEmail(insurance.getEmail());
            existing.setAddress(insurance.getAddress());
            existing.setTermsAndConditions(insurance.getTermsAndConditions());
            
            Insurance updated = insuranceRepository.save(existing);
            log.info("Updated insurance with ID: {}", updated.getId());
            return updated;
        } else {
            Insurance saved = insuranceRepository.save(insurance);
            log.info("Created new insurance with ID: {}", saved.getId());
            return saved;
        }
    }
    
    @Transactional
    public void deleteInsurance(UUID id) {
        log.info("Deleting insurance with ID: {}", id);
        if (!insuranceRepository.existsById(id)) {
            throw new RuntimeException("Insurance not found with ID: " + id);
        }
        insuranceRepository.deleteById(id);
    }
    
    public BigDecimal calculateInsuranceCoverage(UUID patientId, BigDecimal billAmount) {
        log.info("Calculating insurance coverage for patient ID: {} with bill amount: {}", patientId, billAmount);
        
        Optional<Insurance> insuranceOpt = getFirstActiveInsuranceByPatientId(patientId);
        if (insuranceOpt.isEmpty()) {
            log.info("No active insurance found for patient ID: {}", patientId);
            return BigDecimal.ZERO;
        }
        
        Insurance insurance = insuranceOpt.get();
        
        // Check if insurance is active
        if (!insurance.getIsActive()) {
            log.info("Insurance is not active for patient ID: {}", patientId);
            return BigDecimal.ZERO;
        }
        
        // Use remaining amount for coverage calculation
        BigDecimal remainingAmount = insurance.getRemainingAmount();
        BigDecimal coverageAmount = insurance.getCoverageAmount();
        
        // Calculate coverage based on remaining amount
        BigDecimal calculatedCoverage = billAmount;
        
        // If bill amount is more than remaining amount, use remaining amount
        if (calculatedCoverage.compareTo(remainingAmount) > 0) {
            calculatedCoverage = remainingAmount;
        }
        
        // Ensure coverage is not negative
        if (calculatedCoverage.compareTo(BigDecimal.ZERO) < 0) {
            calculatedCoverage = BigDecimal.ZERO;
        }
        
        log.info("Calculated insurance coverage: {} for patient ID: {}", calculatedCoverage, patientId);
        return calculatedCoverage;
    }
    
    public boolean isInsuranceValid(UUID patientId) {
        Optional<Insurance> insuranceOpt = getFirstActiveInsuranceByPatientId(patientId);
        if (insuranceOpt.isEmpty()) {
            return false;
        }
        
        Insurance insurance = insuranceOpt.get();
        return insurance.getIsActive() && 
               LocalDate.now().isAfter(insurance.getValidFrom()) &&
               LocalDate.now().isBefore(insurance.getValidTo());
    }
    
    @Transactional
    public void updateRemainingAmount(UUID insuranceId, BigDecimal usedAmount) {
        log.info("Updating remaining amount for insurance ID: {} with used amount: {}", insuranceId, usedAmount);
        Optional<Insurance> insuranceOpt = insuranceRepository.findById(insuranceId);
        if (insuranceOpt.isPresent()) {
            Insurance insurance = insuranceOpt.get();
            BigDecimal newRemainingAmount = insurance.getRemainingAmount().subtract(usedAmount);
            if (newRemainingAmount.compareTo(BigDecimal.ZERO) < 0) {
                newRemainingAmount = BigDecimal.ZERO;
            }
            insurance.setRemainingAmount(newRemainingAmount);
            insuranceRepository.save(insurance);
            log.info("Updated remaining amount to {} for insurance ID: {}", newRemainingAmount, insuranceId);
        }
    }
}
