package com.newlife.teamly.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamResponse {
    private Long teamId;
    private String teamName;
    private String teamDescription;
    private LocalDate teamCreatedDate;
    private String ownerUsername;
}
