package com.newlife.teamly.service;

import com.newlife.teamly.dto.TeamRequest;
import com.newlife.teamly.dto.TeamResponse;
import com.newlife.teamly.models.Team;
import com.newlife.teamly.models.User;
import com.newlife.teamly.repository.TeamRepository;
import com.newlife.teamly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::mapToTeamResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TeamResponse createTeam(TeamRequest teamRequest) {
        User currentUser = getCurrentUser();

        Team team = modelMapper.map(teamRequest, Team.class);
        team.setTeamOwner(currentUser);

        Team savedTeam = teamRepository.save(team);
        return mapToTeamResponse(savedTeam);
    }

    @Override
    public TeamResponse updateTeam(Long id, TeamRequest teamRequest) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        User currentUser = getCurrentUser();
        if (!team.getTeamOwner().equals(currentUser)) {
            throw new RuntimeException("Only the team owner can update the team");
        }

        modelMapper.map(teamRequest, team);

        Team updatedTeam = teamRepository.save(team);
        return mapToTeamResponse(updatedTeam);
    }

    @Override
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        User currentUser = getCurrentUser();
        if (!team.getTeamOwner().equals(currentUser)) {
            throw new RuntimeException("Only the team owner can delete the team");
        }

        teamRepository.delete(team);
    }

    private User getCurrentUser() {
        Object principal = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            assert principal != null;
            username = principal.toString();
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private TeamResponse mapToTeamResponse(Team team) {
        TeamResponse teamResponse = modelMapper.map(team, TeamResponse.class);
        teamResponse.setOwnerUsername(team.getTeamOwner().getUsername());
        return teamResponse;
    }
}
