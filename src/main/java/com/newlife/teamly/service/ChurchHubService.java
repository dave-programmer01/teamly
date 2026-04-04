package com.newlife.teamly.service;

import com.newlife.teamly.dto.*;
import com.newlife.teamly.models.*;
import com.newlife.teamly.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChurchHubService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final AnnouncementRepository announcementRepository;
    private final SupplyRequestRepository supplyRequestRepository;
    private final EventRepository eventRepository;
    private final EventTaskRepository eventTaskRepository;
    private final UserTeamRepository userTeamRepository;

    public List<AnnouncementResponse> getAnnouncements() {
        return announcementRepository.findAll().stream().map(this::mapAnnouncement).toList();
    }

    public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
        User currentUser = getCurrentUser();
        Team team = teamRepository.findById(request.getTeamId()).orElseThrow(() -> new RuntimeException("Team not found"));
        Announcement announcement = new Announcement(null, currentUser, team, request.getMessage(), null);
        return mapAnnouncement(announcementRepository.save(announcement));
    }

    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    public List<RequestResponse> getRequests() {
        return supplyRequestRepository.findAll().stream().map(this::mapRequest).toList();
    }

    public RequestResponse createRequest(RequestCreateRequest request) {
        User currentUser = getCurrentUser();
        Team team = teamRepository.findById(request.getToTeamId()).orElseThrow(() -> new RuntimeException("Team not found"));
        SupplyRequest supplyRequest = new SupplyRequest(null, currentUser, team, request.getItem(), request.getQuantity(), RequestStatus.PENDING, null);
        return mapRequest(supplyRequestRepository.save(supplyRequest));
    }

    public RequestResponse updateRequestStatus(Long requestId, RequestStatusUpdateRequest request) {
        User currentUser = getCurrentUser();
        SupplyRequest supplyRequest = supplyRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Team targetTeam = supplyRequest.getToTeam();

        // Check if user is a member of the target team
        boolean isMember = userTeamRepository.findByUserIdAndTeamTeamId(currentUser.getId(), targetTeam.getTeamId()).isPresent();

        // Also check if user is the team owner OR if user.getTeam() matches (legacy/redundant check based on User model)
        if (!isMember && (targetTeam.getTeamOwner() == null || !targetTeam.getTeamOwner().getId().equals(currentUser.getId()))) {
            if (currentUser.getTeam() == null || !currentUser.getTeam().getTeamId().equals(targetTeam.getTeamId())) {
                throw new RuntimeException("You are not authorized to update this request. Only members of the " + targetTeam.getTeamName() + " team can review it.");
            }
        }

        supplyRequest.setStatus(RequestStatus.valueOf(request.getStatus().toUpperCase()));
        return mapRequest(supplyRequestRepository.save(supplyRequest));
    }

    @Scheduled(fixedRate = 3600000) // Every hour
    @Transactional
    public void deleteOldRequests() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        supplyRequestRepository.deleteByCreatedAtBefore(cutoff);
    }

    public List<EventResponse> getEvents() {
        return eventRepository.findAll().stream().map(this::mapEvent).toList();
    }

    public EventResponse createEvent(EventCreateRequest request) {
        Event event = new Event(null, request.getTitle(), request.getEventDate(), request.getEventTime(), request.getNotes());
        return mapEvent(eventRepository.save(event));
    }

    public List<TaskResponse> getEventTasks(Long eventId) {
        return eventTaskRepository.findByEventId(eventId).stream().map(this::mapTask).toList();
    }

    public TaskResponse completeTask(Long eventId, Long taskId, TaskStatusUpdateRequest request) {
        EventTask task = eventTaskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        if (!Objects.equals(task.getEvent().getId(), eventId)) {
            throw new RuntimeException("Task does not belong to this event");
        }
        task.setDone(request.getIsDone());
        return mapTask(eventTaskRepository.save(task));
    }

    private AnnouncementResponse mapAnnouncement(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .authorId(announcement.getAuthor().getId())
                .teamId(announcement.getTeam().getTeamId())
                .message(announcement.getMessage())
                .createdAt(announcement.getCreatedAt())
                .build();
    }

    private RequestResponse mapRequest(SupplyRequest request) {
        return RequestResponse.builder()
                .id(request.getId())
                .fromUserId(request.getFromUser().getId())
                .toTeamId(request.getToTeam().getTeamId())
                .item(request.getItem())
                .quantity(request.getQuantity())
                .status(request.getStatus().name())
                .createdAt(request.getCreatedAt())
                .build();
    }

    private EventResponse mapEvent(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .eventDate(event.getEventDate())
                .eventTime(event.getEventTime())
                .notes(event.getNotes())
                .build();
    }

    private TaskResponse mapTask(EventTask task) {
        return TaskResponse.builder()
                .id(task.getId())
                .eventId(task.getEvent().getId())
                .label(task.getLabel())
                .assignedTo(task.getAssignedTo() == null ? null : task.getAssignedTo().getId())
                .teamId(task.getTeam() == null ? null : task.getTeam().getTeamId())
                .isDone(task.isDone())
                .build();
    }

    private User getCurrentUser() {
        Object principal = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        String username = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : principal.toString();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
