package com.example.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentEvent {

    private String type;
    private CustomerOrder order;

}