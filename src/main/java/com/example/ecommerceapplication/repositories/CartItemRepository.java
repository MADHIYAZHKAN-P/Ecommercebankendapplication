package com.example.ecommerceapplication.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ecommerceapplication.entities.CartItem;
import com.example.ecommerceapplication.entities.User;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
}
