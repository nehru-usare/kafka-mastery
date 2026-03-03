package com.smartjava.kafka.order.repository;

import com.smartjava.kafka.order.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {

    // Find all events that have not been processed yet, ordered by creation time
    List<OutboxEvent> findByProcessedFalseOrderByCreatedAtAsc();
}
