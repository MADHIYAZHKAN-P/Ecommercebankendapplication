package com.example.ecommerceapplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerceapplication.entities.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	
}