package com.newlife.teamly.repository;

import com.newlife.teamly.models.User;
import com.newlife.teamly.models.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {
    Optional<UserTeam> findByUserIdAndTeamTeamId(Long userId, Long teamId);
    List<UserTeam> findByTeamTeamId(Long teamId);
    List<UserTeam> findByUser(User user);
}
