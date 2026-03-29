package com.hospital.appointmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentEvent {
    
    private String eventType;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDateTime;
    private String appointmentType;
    private String status;
    private LocalDateTime eventTimestamp;
    
    public static AppointmentEvent fromAppointmentResponse(AppointmentResponse response, String eventType) {
        return AppointmentEvent.builder()
                .eventType(eventType)
                .appointmentId(response.getId())
                .patientId(response.getPatientId())
                .doctorId(response.getDoctorId())
                .appointmentDateTime(response.getAppointmentDateTime())
                .appointmentType(response.getAppointmentType())
                .status(response.getStatus())
                .eventTimestamp(LocalDateTime.now())
                .build();
    }
}