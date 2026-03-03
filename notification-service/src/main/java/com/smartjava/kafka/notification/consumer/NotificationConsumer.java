package com.smartjava.kafka.notification.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Service;

import com.smartjava.kafka.common.OrderPlacedEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for consuming Order events and sending notifications
 * (Email/SMS).
 * 
 * <p>
 * Architectural Case Study:
 * <b>Competing Consumers vs Multiple Consumer Groups</b>
 * <ul>
 * <li>Payment Service and Notification Service have <b>different</b> group
 * IDs.</li>
 * <li>This means both services receive a COPY of every message. This is
 * <b>Fan-out Architecture</b>.</li>
 * <li>If we had two Notification Services with the <b>same</b> group ID, they
 * would SHARE the load. This is <b>Competing Consumers</b>.</li>
 * </ul>
 */
@Service
@Slf4j
public class NotificationConsumer {

    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    public NotificationConsumer(org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Consumes events to trigger notifications.
     */
    @KafkaListener(topics = "order-placed-events", groupId = "notification-group")
    public void consume(ConsumerRecord<String, OrderPlacedEvent> record) {
        OrderPlacedEvent event = record.value();
        log.info("[Notification Service] Received order event for orderId: {}", event.getOrderId());
        log.info("[Kafka Consumer] Message Received by NotificationGroup. (partition={}, offset={}, orderId={})",
                record.partition(), record.offset(), event.getOrderId());

        // Push the mapped event to the WebSocket topic for real-time UI updates
        // We map to standard objects to prevent Jackson from trying to serialize Avro
        // Schema fields
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("orderId", event.getOrderId().toString());
        payload.put("customerId", event.getCustomerId().toString());
        payload.put("amount", event.getAmount());
        payload.put("status", event.getStatus() != null ? event.getStatus().toString() : "UNKNOWN");

        messagingTemplate.convertAndSend("/topic/notifications", payload);

        log.info("[Kafka Consumer] Pushed notification to WebSocket. (orderId={})", event.getOrderId());
    }
}
