package com.newlife.teamly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventCreateRequest {
    @NotBlank
    private String title;

    @NotNull
    private LocalDate eventDate;

    @NotNull
    private LocalTime eventTime;

    private String notes;
}
