package com.smartjava.kafka.order.producer;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartjava.kafka.common.OrderPlacedEvent;
import com.smartjava.kafka.order.config.KafkaConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsible for producing Order events to Kafka.
 * 
 * <p>
 * Architectural Significance:
 * <ul>
 * <li><b>Decoupling:</b> The Order Service doesn't know about Payment or
 * Notification services.</li>
 * <li><b>Durability:</b> Messages are persisted in Kafka's write-ahead
 * log.</li>
 * <li><b>High Availability:</b> The producer uses retries and acknowledgments
 * to ensure delivery.</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    /**
     * Publishes an OrderPlacedEvent to the Kafka topic.
     * 
     * <p>
     * <b>Kafka Features Used:</b>
     * <ul>
     * <li><b>Partitioning:</b> We use the {@code orderId} as the record key. Kafka
     * hashes this key
     * to determine the partition. All events for the same orderId will always land
     * in the <b>same partition</b>, ensuring strict ordering per order.</li>
     * <li><b>Transactions:</b> The {@code @Transactional} annotation ensures that
     * the
     * message is only "committed" in Kafka if this method completes successfully.
     * This prevents "phantom" messages if a database operation fails later.</li>
     * <li><b>Asynchronous Sending:</b> {@code kafkaTemplate.send()} is
     * non-blocking.
     * We use a {@link CompletableFuture} to handle the result once Kafka
     * acknowledges.</li>
     * </ul>
     * 
     * @param event The data to be sent to Kafka
     */
    @Transactional("kafkaTransactionManager")
    public void sendOrderEvent(OrderPlacedEvent event) {
        log.info("[Kafka Producer] Preparing to send event. (topic={}, orderId={}, key={})",
                KafkaConfig.ORDER_PLACED_TOPIC, event.getOrderId(), event.getOrderId());

        // We use orderId as the KEY to ensure all events for this specific order
        // go to the same partition. This is CRITICAL for maintaining ordering and
        // concurrency.
        CompletableFuture<SendResult<String, OrderPlacedEvent>> future = kafkaTemplate
                .send(KafkaConfig.ORDER_PLACED_TOPIC, event.getOrderId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[Kafka Producer] ACK Received! (topic={}, partition={}, offset={}, timestamp={})",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        result.getRecordMetadata().timestamp());
            } else {
                log.error("[Kafka Producer] Delivery Failed after retries! (orderId={}, error={})",
                        event.getOrderId(), ex.getMessage(), ex);
            }
        });
    }
}
