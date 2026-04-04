package com.newlife.teamly.controller;

import com.newlife.teamly.dto.AnnouncementRequest;
import com.newlife.teamly.dto.AnnouncementResponse;
import com.newlife.teamly.dto.MessageResponse;
import com.newlife.teamly.service.ChurchHubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final ChurchHubService churchHubService;

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncements() {
        return ResponseEntity.ok(churchHubService.getAnnouncements());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AnnouncementResponse> postAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        return new ResponseEntity<>(churchHubService.createAnnouncement(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteAnnouncement(@PathVariable Long id) {
        churchHubService.deleteAnnouncement(id);
        return ResponseEntity.ok(new MessageResponse("Announcement deleted successfully"));
    }
}
