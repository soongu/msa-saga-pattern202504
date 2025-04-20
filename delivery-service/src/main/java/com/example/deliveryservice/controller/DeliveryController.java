package com.example.deliveryservice.controller;

import com.example.deliveryservice.dto.CustomerOrder;
import com.example.deliveryservice.dto.DeliveryEvent;
import com.example.deliveryservice.entity.Delivery;
import com.example.deliveryservice.entity.DeliveryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryRepository repository;

    private final KafkaTemplate<String, DeliveryEvent> kafkaTemplate;

    @KafkaListener(topics = "new-stock", groupId = "stock-group")
    public void deliverOrder(String event) throws JsonMappingException, JsonProcessingException {
        System.out.println("Inside ship order for order "+event);

        Delivery shipment = new Delivery();
        DeliveryEvent inventoryEvent = new ObjectMapper().readValue(event, DeliveryEvent.class);
        CustomerOrder order = inventoryEvent.getOrder();

        try {
            if (order.getAddress() == null) {
                throw new Exception("Address not present");
            }

            shipment.setAddress(order.getAddress());
            shipment.setOrderId(order.getOrderId());

            shipment.setStatus("success");

            repository.save(shipment);
        } catch (Exception e) {
            shipment.setOrderId(order.getOrderId());
            shipment.setStatus("failed");
            repository.save(shipment);

            System.out.println(order);

            DeliveryEvent reverseEvent = new DeliveryEvent();
            reverseEvent.setType("STOCK_REVERSED");
            reverseEvent.setOrder(order);
            kafkaTemplate.send("reversed-stock", reverseEvent);
        }
    }
}