package com.newlife.teamly.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RequestResponse {
    private Long id;
    private Long fromUserId;
    private Long toTeamId;
    private String item;
    private Integer quantity;
    private String status;
    private LocalDateTime createdAt;
}
