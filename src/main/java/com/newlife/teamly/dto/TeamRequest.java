package com.newlife.teamly.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TeamRequest {
    @NotNull(message = "Team name cannot be null")
    public String teamName;

    @NotNull(message = "Team description cannot be null")
    public String teamDescription;
}
