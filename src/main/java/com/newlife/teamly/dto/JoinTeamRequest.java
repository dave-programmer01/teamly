package com.newlife.teamly.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JoinTeamRequest {
    @NotBlank
    private String position;
}
