package com.newlife.teamly.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestCreateRequest {
    @NotNull
    private Long toTeamId;

    @NotBlank
    private String item;

    @NotNull
    @Min(1)
    private Integer quantity;
}
