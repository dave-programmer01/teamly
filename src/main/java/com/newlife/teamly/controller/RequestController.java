package com.newlife.teamly.controller;

import com.newlife.teamly.dto.RequestCreateRequest;
import com.newlife.teamly.dto.RequestResponse;
import com.newlife.teamly.dto.RequestStatusUpdateRequest;
import com.newlife.teamly.service.ChurchHubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {

    private final ChurchHubService churchHubService;

    @GetMapping
    public ResponseEntity<List<RequestResponse>> getAllRequests() {
        return ResponseEntity.ok(churchHubService.getRequests());
    }

    @PostMapping
    public ResponseEntity<RequestResponse> sendRequest(@Valid @RequestBody RequestCreateRequest request) {
        return new ResponseEntity<>(churchHubService.createRequest(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RequestResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody RequestStatusUpdateRequest request) {
        return ResponseEntity.ok(churchHubService.updateRequestStatus(id, request));
    }
}
