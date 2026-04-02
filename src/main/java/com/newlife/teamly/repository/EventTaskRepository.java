package com.newlife.teamly.repository;

import com.newlife.teamly.models.EventTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventTaskRepository extends JpaRepository<EventTask, Long> {
    List<EventTask> findByEventId(Long eventId);
}
