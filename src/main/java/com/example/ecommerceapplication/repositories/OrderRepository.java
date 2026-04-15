package com.example.ecommerceapplication.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerceapplication.entities.Order;
import com.example.ecommerceapplication.entities.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findByUser(User user);
	List<Order> findByUserOrderByCreatedAtDesc(User user);
	Page<Order> findByUser(User user, Pageable pageable);

}