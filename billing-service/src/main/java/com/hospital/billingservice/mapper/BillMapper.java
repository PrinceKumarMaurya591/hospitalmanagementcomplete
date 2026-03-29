package com.hospital.billingservice.mapper;

import com.hospital.billingservice.dto.BillResponse;
import com.hospital.billingservice.model.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BillMapper {
    
    BillMapper INSTANCE = Mappers.getMapper(BillMapper.class);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "appointmentId", target = "appointmentId")
    @Mapping(source = "patientId", target = "patientId")
    @Mapping(source = "doctorId", target = "doctorId")
    @Mapping(source = "invoiceNumber", target = "invoiceNumber")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "tax", target = "tax")
    @Mapping(source = "discount", target = "discount")
    @Mapping(source = "totalAmount", target = "totalAmount")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "generatedDate", target = "generatedDate")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "insuranceCovered", target = "insuranceCovered")
    @Mapping(source = "insuranceAmount", target = "insuranceAmount")
    @Mapping(source = "patientPayable", target = "patientPayable")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    BillResponse toResponse(Bill bill);
    
    // If needed, we can add more mapping methods here
    // For example: Bill toEntity(BillRequest request);
}