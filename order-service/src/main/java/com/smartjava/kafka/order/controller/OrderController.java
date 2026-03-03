package com.smartjava.kafka.order.controller;

import com.smartjava.kafka.order.service.OrderService;
import com.smartjava.kafka.order.dto.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public String placeOrder(@RequestBody OrderRequestDto request) {
        if (request.getOrderId() == null) {
            request.setOrderId(UUID.randomUUID().toString());
        }

        // This transactionally saves the order and the outbox event in the database
        // It no longer talks to Kafka directly!
        orderService.placeOrder(request);

        return "Order Placed: " + request.getOrderId();
    }
}
