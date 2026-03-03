package com.smartjava.kafka.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false, length = 4000)
    private String payload; // Will hold the serialized JSON of the OrderRequestDto or Avro depending on
                            // implementation

    @Column(nullable = false)
    @Builder.Default
    private boolean processed = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
