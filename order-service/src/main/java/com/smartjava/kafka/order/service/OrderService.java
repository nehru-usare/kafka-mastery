package com.smartjava.kafka.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartjava.kafka.order.dto.OrderRequestDto;
import com.smartjava.kafka.order.entity.OutboxEvent;
import com.smartjava.kafka.order.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional("transactionManager")
    public void placeOrder(OrderRequestDto request) {
        log.info("Processing order request: {}", request.getOrderId());

        // In a real application, you would save the Order entity here first
        // inside the same transaction
        // orderRepository.save(new Order(request.getOrderId(), ...));

        // Save the event to the outbox table
        try {
            String payload = objectMapper.writeValueAsString(request);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .orderId(request.getOrderId())
                    .payload(payload)
                    .processed(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(outboxEvent);
            log.info("Saved outbox event for order: {}", request.getOrderId());

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order request to outbox event", e);
            throw new RuntimeException("Failed to process order", e);
        }
    }
}
