package com.newlife.teamly.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResponse {
    private Long id;
    private Long eventId;
    private String label;
    private Long assignedTo;
    private Long teamId;
    private boolean isDone;
}
