package com.newlife.teamly.repository;

import com.newlife.teamly.models.SupplyRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SupplyRequestRepository extends JpaRepository<SupplyRequest, Long> {
    void deleteByCreatedAtBefore(LocalDateTime dateTime);
}
