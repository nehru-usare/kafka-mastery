package com.smartjava.kafka.payment.repository;

import com.smartjava.kafka.payment.entity.FailedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedEventRepository extends JpaRepository<FailedEvent, String> {
}
