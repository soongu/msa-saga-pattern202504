package com.example.consumerservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {

    @KafkaListener(topics = "mytopic2", groupId = "group_id")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}