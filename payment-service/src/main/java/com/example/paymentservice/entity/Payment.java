package com.example.paymentservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String mode;

    @Column
    private Long orderId;

    @Column
    private double amount;

    @Column
    private String status;

}