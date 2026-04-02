package com.newlife.teamly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnouncementRequest {
    @NotNull
    private Long teamId;

    @NotBlank
    private String message;
}
