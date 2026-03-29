package com.hospital.appointmentservice.mapper;

import com.hospital.appointmentservice.dto.AppointmentRequest;
import com.hospital.appointmentservice.dto.AppointmentResponse;
import com.hospital.appointmentservice.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    
    AppointmentMapper INSTANCE = Mappers.getMapper(AppointmentMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "SCHEDULED")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Appointment toEntity(AppointmentRequest request);
    
    AppointmentResponse toResponse(Appointment appointment);
}