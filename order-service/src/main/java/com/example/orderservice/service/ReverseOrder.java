package com.example.orderservice.service;

import com.example.orderservice.dto.OrderEvent;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReverseOrder {

    private final OrderRepository repository;

    @KafkaListener(topics = "reversed-orders", groupId = "orders-group")
    public void reverseOrder(String event) {
        System.out.println("Inside reverse order for order "+event);

        try {
            OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);

            Optional<Order> order = repository.findById(orderEvent.getOrder().getOrderId());

            order.ifPresent(o -> {
                o.setStatus("FAILED");
                this.repository.save(o);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}