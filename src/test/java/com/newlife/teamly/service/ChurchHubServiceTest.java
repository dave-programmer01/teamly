package com.newlife.teamly.service;

import com.newlife.teamly.dto.RequestResponse;
import com.newlife.teamly.dto.RequestStatusUpdateRequest;
import com.newlife.teamly.models.*;
import com.newlife.teamly.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChurchHubServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private SupplyRequestRepository supplyRequestRepository;
    @Mock
    private UserTeamRepository userTeamRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private ChurchHubService churchHubService;

    private User currentUser;
    private Team hospitalityTeam;
    private SupplyRequest supplyRequest;

    @BeforeEach
    void setUp() {
        currentUser = User.builder().id(1L).username("testuser").build();
        hospitalityTeam = new Team();
        hospitalityTeam.setTeamId(2L);
        hospitalityTeam.setTeamName("Hospitality");

        supplyRequest = new SupplyRequest();
        supplyRequest.setId(10L);
        supplyRequest.setToTeam(hospitalityTeam);
        supplyRequest.setFromUser(User.builder().id(3L).username("mediaUser").build());
        supplyRequest.setStatus(RequestStatus.PENDING);
    }

    private void setupSecurity() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void updateRequestStatus_WhenUserIsMember_ShouldSucceed() {
        // Arrange
        setupSecurity();
        when(authentication.getPrincipal()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(supplyRequestRepository.findById(10L)).thenReturn(Optional.of(supplyRequest));
        when(userTeamRepository.findByUserIdAndTeamTeamId(1L, 2L)).thenReturn(Optional.of(new UserTeam()));
        when(supplyRequestRepository.save(any(SupplyRequest.class))).thenReturn(supplyRequest);

        RequestStatusUpdateRequest updateRequest = new RequestStatusUpdateRequest();
        updateRequest.setStatus("COMPLETED");

        // Act
        RequestResponse response = churchHubService.updateRequestStatus(10L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("COMPLETED", response.getStatus());
        verify(supplyRequestRepository).save(supplyRequest);
    }

    @Test
    void updateRequestStatus_WhenUserIsNotMember_ShouldThrowException() {
        // Arrange
        setupSecurity();
        when(authentication.getPrincipal()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(currentUser));
        when(supplyRequestRepository.findById(10L)).thenReturn(Optional.of(supplyRequest));
        when(userTeamRepository.findByUserIdAndTeamTeamId(1L, 2L)).thenReturn(Optional.empty());

        RequestStatusUpdateRequest updateRequest = new RequestStatusUpdateRequest();
        updateRequest.setStatus("COMPLETED");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            churchHubService.updateRequestStatus(10L, updateRequest);
        });

        assertTrue(exception.getMessage().contains("not authorized"));
        verify(supplyRequestRepository, never()).save(any());
    }

    @Test
    void deleteOldRequests_ShouldCallRepositoryWithCorrectCutoff() {
        // Act
        churchHubService.deleteOldRequests();

        // Assert
        verify(supplyRequestRepository).deleteByCreatedAtBefore(any(LocalDateTime.class));
    }
}
