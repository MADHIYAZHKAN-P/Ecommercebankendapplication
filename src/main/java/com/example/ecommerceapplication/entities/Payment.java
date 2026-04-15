package com.example.ecommerceapplication.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentId;
    // fake Razorpay/Stripe id
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
        // CREATED, SUCCESS, FAILED

    private double amount;

    private LocalDateTime createdAt;

    @OneToOne
    private Order order;

    @ManyToOne 
    private User user;
}