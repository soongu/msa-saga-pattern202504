package com.example.paymentservice.service;

import com.example.paymentservice.dto.CustomerOrder;
import com.example.paymentservice.dto.OrderEvent;
import com.example.paymentservice.dto.PaymentEvent;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.entity.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReversePayment {

    private final PaymentRepository repository;

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @KafkaListener(topics = "reversed-payments", groupId = "payments-group")
    public void reversePayment(String event) {
        System.out.println("Inside reverse payment for order "+event);

        try {
            PaymentEvent paymentEvent = new ObjectMapper().readValue(event, PaymentEvent.class);

            CustomerOrder order = paymentEvent.getOrder();

            List<Payment> payments = this.repository.findByOrderId(order.getOrderId());

            payments.forEach(p -> {
                p.setStatus("FAILED");
                repository.save(p);
            });

            OrderEvent orderEvent = new OrderEvent();
            orderEvent.setOrder(paymentEvent.getOrder());
            orderEvent.setType("ORDER_REVERSED");
            kafkaTemplate.send("reversed-orders", orderEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}