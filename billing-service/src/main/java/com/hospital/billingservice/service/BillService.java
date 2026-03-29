package com.hospital.billingservice.service;

import com.hospital.billingservice.dto.AppointmentCompletedEvent;
import com.hospital.billingservice.dto.BillRequest;
import com.hospital.billingservice.dto.BillResponse;
import com.hospital.billingservice.kafka.BillEventProducer;
import com.hospital.billingservice.mapper.BillMapper;
import com.hospital.billingservice.model.Bill;
import com.hospital.billingservice.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillService {
    
    private final BillRepository billRepository;
    private final BillMapper billMapper;
    private final BillEventProducer billEventProducer;
    private final InsuranceService insuranceService;
    
    @Transactional
    public BillResponse createBill(BillRequest request) {
        log.info("Creating bill for appointment ID: {}", request.getAppointmentId());
        
        // Calculate totals
        BigDecimal tax = request.getTax() != null ? request.getTax() : BigDecimal.ZERO;
        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal totalAmount = request.getAmount()
                .add(tax)
                .subtract(discount);
        
        // Check insurance coverage
        boolean insuranceCovered = request.getInsuranceCovered() != null ? request.getInsuranceCovered() : false;
        BigDecimal insuranceAmount = request.getInsuranceAmount() != null ? request.getInsuranceAmount() : BigDecimal.ZERO;
        BigDecimal patientPayable = totalAmount.subtract(insuranceAmount);
        
        if (patientPayable.compareTo(BigDecimal.ZERO) < 0) {
            patientPayable = BigDecimal.ZERO;
        }
        
        // Generate invoice number
        String invoiceNumber = generateInvoiceNumber();
        
        Bill bill = Bill.builder()
                .appointmentId(request.getAppointmentId())
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .invoiceNumber(invoiceNumber)
                .amount(request.getAmount())
                .tax(tax)
                .discount(discount)
                .totalAmount(totalAmount)
                .status(Bill.BillStatus.GENERATED)
                .dueDate(request.getDueDate() != null ? request.getDueDate() : LocalDate.now().plusDays(30))
                .generatedDate(LocalDate.now())
                .description(request.getDescription())
                .insuranceCovered(insuranceCovered)
                .insuranceAmount(insuranceAmount)
                .patientPayable(patientPayable)
                .build();
        
        Bill savedBill = billRepository.save(bill);
        log.info("Bill created successfully with ID: {}", savedBill.getId());
        
        // Send Kafka event
        billEventProducer.sendBillGeneratedEvent(savedBill);
        
        return billMapper.toResponse(savedBill);
    }
    
    @Transactional
    public void createBillFromAppointment(AppointmentCompletedEvent event) {
        log.info("Creating bill from appointment completion event for appointment ID: {}", event.getAppointmentId());
        
        BillRequest request = BillRequest.builder()
                .appointmentId(event.getAppointmentId())
                .patientId(event.getPatientId())
                .doctorId(event.getDoctorId())
                .amount(event.getTotalFee())
                .description(String.format("Appointment completed on %s. Diagnosis: %s", 
                        event.getAppointmentDate(), event.getDiagnosis()))
                .dueDate(LocalDate.now().plusDays(30))
                .build();
        
        createBill(request);
    }
    
    public BillResponse getBillById(UUID id) {
        log.info("Fetching bill with ID: {}", id);
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + id));
        return billMapper.toResponse(bill);
    }
    
    public BillResponse getBillByInvoiceNumber(String invoiceNumber) {
        log.info("Fetching bill with invoice number: {}", invoiceNumber);
        Bill bill = billRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Bill not found with invoice number: " + invoiceNumber));
        return billMapper.toResponse(bill);
    }
    
    public List<BillResponse> getAllBills() {
        log.info("Fetching all bills");
        return billRepository.findAll().stream()
                .map(billMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<BillResponse> getBillsByPatientId(UUID patientId) {
        log.info("Fetching bills for patient ID: {}", patientId);
        return billRepository.findByPatientId(patientId).stream()
                .map(billMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<BillResponse> getBillsByAppointmentId(UUID appointmentId) {
        log.info("Fetching bills for appointment ID: {}", appointmentId);
        return billRepository.findByAppointmentId(appointmentId).stream()
                .map(billMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public BillResponse updateBillStatus(UUID id, Bill.BillStatus status) {
        log.info("Updating bill status to {} for bill ID: {}", status, id);
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + id));
        
        bill.setStatus(status);
        Bill updatedBill = billRepository.save(bill);
        
        // If bill is paid, send event
        if (status == Bill.BillStatus.PAID) {
            billEventProducer.sendBillGeneratedEvent(updatedBill);
        }
        
        return billMapper.toResponse(updatedBill);
    }
    
    @Transactional
    public void deleteBill(UUID id) {
        log.info("Deleting bill with ID: {}", id);
        if (!billRepository.existsById(id)) {
            throw new RuntimeException("Bill not found with ID: " + id);
        }
        billRepository.deleteById(id);
    }
    
    public BigDecimal getTotalOutstandingByPatientId(UUID patientId) {
        log.info("Calculating total outstanding amount for patient ID: {}", patientId);
        List<Bill> outstandingBills = billRepository.findByPatientIdAndStatusIn(
                patientId, 
                List.of(Bill.BillStatus.GENERATED, Bill.BillStatus.PARTIALLY_PAID, Bill.BillStatus.OVERDUE)
        );
        
        return outstandingBills.stream()
                .map(Bill::getPatientPayable)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private String generateInvoiceNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "INV-" + timestamp.substring(timestamp.length() - 8) + "-" + random;
    }
}