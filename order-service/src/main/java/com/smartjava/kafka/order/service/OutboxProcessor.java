package com.smartjava.kafka.order.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartjava.kafka.common.OrderPlacedEvent;
import com.smartjava.kafka.order.dto.OrderRequestDto;
import com.smartjava.kafka.order.entity.OutboxEvent;
import com.smartjava.kafka.order.producer.OrderProducer;
import com.smartjava.kafka.order.repository.OutboxEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final OutboxEventRepository outboxEventRepository;
    private final OrderProducer orderProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelayString = "3000")
    @Transactional("transactionManager")
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();

        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} unprocessed outbox events", events.size());

        for (OutboxEvent outboxEvent : events) {
            try {
                // Deserialize payload back to DTO
                OrderRequestDto request = objectMapper.readValue(outboxEvent.getPayload(), OrderRequestDto.class);

                // Map to Avro event
                OrderPlacedEvent avroEvent = OrderPlacedEvent.newBuilder()
                        .setOrderId(request.getOrderId())
                        .setCustomerId(request.getCustomerId())
                        .setAmount(request.getAmount() != null ? request.getAmount() : 0.0)
                        .setStatus(request.getStatus() != null ? request.getStatus() : "PENDING")
                        .build();

                // Send to Kafka
                orderProducer.sendOrderEvent(avroEvent);

                // Mark as processed
                outboxEvent.setProcessed(true);
                outboxEventRepository.save(outboxEvent);

                log.info("Successfully processed outbox event for orderId: {}", outboxEvent.getOrderId());
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize outbox event payload for orderId: {}", outboxEvent.getOrderId(), e);
                // Optionally mark this as failed structurally, but for simplicity we log it
            } catch (Exception e) {
                log.error("Failed to process and send outbox event for orderId: {}", outboxEvent.getOrderId(), e);
                // Don't swallow everything, let the transaction roll back if Kafka fails
                // synchronously
                throw e;
            }
        }
    }
}
