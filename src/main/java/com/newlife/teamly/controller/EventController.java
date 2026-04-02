package com.newlife.teamly.controller;

import com.newlife.teamly.dto.*;
import com.newlife.teamly.service.ChurchHubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final ChurchHubService churchHubService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getEvents() {
        return ResponseEntity.ok(churchHubService.getEvents());
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventCreateRequest request) {
        return new ResponseEntity<>(churchHubService.createEvent(request), HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(@PathVariable Long eventId) {
        return ResponseEntity.ok(churchHubService.getEventTasks(eventId));
    }

    @PatchMapping("/{eventId}/tasks/{taskId}")
    public ResponseEntity<TaskResponse> completeTask(
            @PathVariable Long eventId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(churchHubService.completeTask(eventId, taskId, request));
    }
}
