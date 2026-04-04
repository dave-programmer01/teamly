package com.newlife.teamly.controller;

import com.newlife.teamly.dto.TeamRequest;
import com.newlife.teamly.dto.TeamResponse;
import com.newlife.teamly.dto.JoinTeamRequest;
import com.newlife.teamly.dto.MessageResponse;
import com.newlife.teamly.dto.UserResponse;
import com.newlife.teamly.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest teamRequest) {
        return new ResponseEntity<>(teamService.createTeam(teamRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(@PathVariable Long id, @Valid @RequestBody TeamRequest teamRequest) {
        return ResponseEntity.ok(teamService.updateTeam(id, teamRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/join")
    public ResponseEntity<MessageResponse> joinTeam(@PathVariable Long teamId, @Valid @RequestBody JoinTeamRequest request) {
        teamService.joinTeam(teamId, request.getPosition());
        return ResponseEntity.ok(new MessageResponse("Joined team successfully"));
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<UserResponse>> getTeamMembers(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }
}
