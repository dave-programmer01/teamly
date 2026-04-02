package com.newlife.teamly.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskStatusUpdateRequest {
    @NotNull
    private Boolean isDone;
}
