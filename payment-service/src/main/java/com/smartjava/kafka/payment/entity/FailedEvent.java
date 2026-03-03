package com.smartjava.kafka.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "failed_events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FailedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false, length = 4000)
    private String payload;

    @Column(nullable = false)
    private String errorReason;

    @Column(nullable = false)
    private LocalDateTime failedAt;
}
