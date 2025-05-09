package com.example.orderservice.controller;

import com.example.orderservice.dto.CustomerOrder;
import com.example.orderservice.dto.OrderEvent;
import com.example.orderservice.entity.Order;
import com.example.orderservice.entity.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository repository;

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @PostMapping("/orders")
    public void createOrder(@RequestBody CustomerOrder customerOrder) {
        Order order = new Order();

        try {
            order.setAmount(customerOrder.getAmount());
            order.setItem(customerOrder.getItem());
            order.setQuantity(customerOrder.getQuantity());
            order.setStatus("CREATED");
            order = repository.save(order);

            customerOrder.setOrderId(order.getId());

            OrderEvent event = new OrderEvent();
            event.setOrder(customerOrder);
            event.setType("ORDER_CREATED");
            kafkaTemplate.send("new-orders", event);
        } catch (Exception e) {
            order.setStatus("FAILED");
            repository.save(order);
        }
    }
}