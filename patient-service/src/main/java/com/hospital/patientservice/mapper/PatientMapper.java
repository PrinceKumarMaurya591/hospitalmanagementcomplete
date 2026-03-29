package com.hospital.patientservice.mapper;

import com.hospital.patientservice.dto.PatientRequest;
import com.hospital.patientservice.dto.PatientResponse;
import com.hospital.patientservice.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Patient toEntity(PatientRequest request);
    
    PatientResponse toResponse(Patient patient);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(PatientRequest request, @MappingTarget Patient patient);
}