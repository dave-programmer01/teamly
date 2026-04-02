package com.newlife.teamly.service;

import com.newlife.teamly.dto.TeamRequest;
import com.newlife.teamly.dto.TeamResponse;
import com.newlife.teamly.dto.UserResponse;

import java.util.List;

public interface TeamService {
    List<TeamResponse> getAllTeams();
    TeamResponse createTeam(TeamRequest teamRequest);
    TeamResponse updateTeam(Long id, TeamRequest teamRequest);
    void deleteTeam(Long id);
    void joinTeam(Long teamId, String position);
    List<UserResponse> getTeamMembers(Long teamId);
}
