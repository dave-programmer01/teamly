package com.newlife.teamly.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class EventResponse {
    private Long id;
    private String title;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private String notes;
}
