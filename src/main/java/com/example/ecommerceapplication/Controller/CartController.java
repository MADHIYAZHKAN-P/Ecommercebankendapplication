package com.example.ecommerceapplication.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerceapplication.Services.CartService;
import com.example.ecommerceapplication.dtos.CartItemDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDTO dto, Authentication auth) {
        return ResponseEntity.ok(cartService.addToCart(auth.getName(), dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> viewCart(Authentication auth) {
        return ResponseEntity.ok(cartService.viewCart(auth.getName()));
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeFromCart(@PathVariable Long id, Authentication auth) {
        cartService.removeFromCart(auth.getName(), id);
        return ResponseEntity.ok(Map.of("message", "Removed successfully"));
    }
}