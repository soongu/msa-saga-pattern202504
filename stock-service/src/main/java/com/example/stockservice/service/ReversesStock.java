package com.example.stockservice.service;

import com.example.stockservice.dto.DeliveryEvent;
import com.example.stockservice.dto.PaymentEvent;
import com.example.stockservice.entity.StockRepository;
import com.example.stockservice.entity.WareHouse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReversesStock {

    private final StockRepository repository;

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @KafkaListener(topics = "reversed-stock", groupId = "stock-group")
    public void reverseStock(String event) {
        System.out.println("Inside reverse stock for order "+event);

        try {
            DeliveryEvent deliveryEvent = new ObjectMapper().readValue(event, DeliveryEvent.class);

            Iterable<WareHouse> inv = this.repository.findByItem(deliveryEvent.getOrder().getItem());

            inv.forEach(i -> {
                i.setQuantity(i.getQuantity() + deliveryEvent.getOrder().getQuantity());
                repository.save(i);
            });

            PaymentEvent paymentEvent = new PaymentEvent();
            paymentEvent.setOrder(deliveryEvent.getOrder());
            paymentEvent.setType("PAYMENT_REVERSED");
            kafkaTemplate.send("reversed-payments", paymentEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}