package com.newlife.teamly.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestStatusUpdateRequest {
    @NotBlank
    private String status;
}
