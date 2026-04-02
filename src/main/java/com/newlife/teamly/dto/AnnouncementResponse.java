package com.newlife.teamly.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AnnouncementResponse {
    private Long id;
    private Long teamId;
    private Long authorId;
    private String message;
    private LocalDateTime createdAt;
}
