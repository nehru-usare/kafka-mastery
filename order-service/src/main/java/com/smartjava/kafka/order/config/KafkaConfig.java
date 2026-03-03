package com.smartjava.kafka.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Central Kafka Infrastructure Configuration.
 * 
 * <p>
 * In a production environment, topics are often managed outside the Java code
 * (via Terraform or CLI), but Spring allows declarative topic creation for
 * development.
 */
@Configuration
public class KafkaConfig {

    /**
     * The primary topic for order-related events.
     */
    public static final String ORDER_PLACED_TOPIC = "order-placed-events";

    /**
     * Defines the 'order-placed-events' topic.
     * 
     * <p>
     * <b>Design Considerations:</b>
     * <ul>
     * <li><b>Partitions (3):</b> Allows up to 3 consumers in a group to work in
     * parallel.
     * Increasing this number increases throughput but also overhead.</li>
     * <li><b>Replicas (1):</b> In a real cluster (e.g., Confluent/AWS MSK), this
     * would be <b>3</b>. If one broker fails, the data remains available.</li>
     * </ul>
     */
    @Bean
    public NewTopic orderPlacedTopic() {
        return TopicBuilder.name(ORDER_PLACED_TOPIC)
                .partitions(3)
                .replicas(1) // Limited to 1 because we are running a single-broker Docker setup.
                .build();
    }

    /*
     * Note on Log Compaction:
     * If this topic was for 'Order Status Updates', we might use .compact()!
     * Log compaction ensures Kafka only keeps the LATEST value for each key.
     * .config(TopicConfig.CLEANUP_POLICY_CONFIG,
     * TopicConfig.CLEANUP_POLICY_COMPACT)
     */
}
