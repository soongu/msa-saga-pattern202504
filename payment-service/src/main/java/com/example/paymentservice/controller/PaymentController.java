package com.example.paymentservice.controller;

import com.example.paymentservice.dto.CustomerOrder;
import com.example.paymentservice.dto.OrderEvent;
import com.example.paymentservice.dto.PaymentEvent;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.entity.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentRepository repository;

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    private final KafkaTemplate<String, OrderEvent> kafkaOrderTemplate;

    @KafkaListener(topics = "new-orders", groupId = "orders-group")
    public void processPayment(String event) throws JsonMappingException, JsonProcessingException {
        System.out.println("Recieved event for payment " + event);
        OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);

        CustomerOrder order = orderEvent.getOrder();
        Payment payment = new Payment();

        try {
            payment.setAmount(order.getAmount());
            payment.setMode(order.getPaymentMode());
            payment.setOrderId(order.getOrderId());
            payment.setStatus("SUCCESS");
            repository.save(payment);

            PaymentEvent paymentEvent = new PaymentEvent();
            paymentEvent.setOrder(orderEvent.getOrder());
            paymentEvent.setType("PAYMENT_CREATED");
            kafkaTemplate.send("new-payments", paymentEvent);
        } catch (Exception e) {
            payment.setOrderId(order.getOrderId());
            payment.setStatus("FAILED");
            repository.save(payment);

            OrderEvent oe = new OrderEvent();
            oe.setOrder(order);
            oe.setType("ORDER_REVERSED");
            kafkaOrderTemplate.send("reversed-orders", orderEvent);
        }
    }
}