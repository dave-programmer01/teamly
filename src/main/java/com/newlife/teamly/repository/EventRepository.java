package com.newlife.teamly.repository;

import com.newlife.teamly.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
