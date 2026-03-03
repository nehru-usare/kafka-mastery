package com.smartjava.kafka.analytics.topology;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.smartjava.kafka.common.OrderPlacedEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AnalyticsTopology {

    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
        log.info("Building Kafka Streams Topology for Analytics");

        KStream<String, OrderPlacedEvent> stream = streamsBuilder.stream("order-placed-events");

        stream.peek((key, event) -> log.debug("Analytics received order: id={}, amount={}", event.getOrderId(), event.getAmount()))
              // We could do complex aggregations here! For now, we'll keep it simple and just count matching statuses.
              .filter((key, event) -> "SUCCESS".equalsIgnoreCase(event.getStatus()))
              .groupBy((key, event) -> "successful-orders")
              .count()
              .toStream()
              .peek((key, count) -> log.info("Total successful orders processed so far: {}", count));
    }
}
