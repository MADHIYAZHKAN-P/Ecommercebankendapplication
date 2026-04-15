package com.example.ecommerceapplication.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerceapplication.Services.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

//    @PostMapping("/place")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> placeOrder(Authentication auth) {
//        return ResponseEntity.ok(orderService.placeOrder(auth.getName()));
//    }

//    @GetMapping
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> getOrders(Authentication auth) {
//        return ResponseEntity.ok(orderService.getOrders(auth.getName()));
//    }
    
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Authentication auth) {

        return ResponseEntity.ok(
                orderService.getOrders(auth.getName(), page, size)
        );
    }
    @PostMapping("/cancel/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, Authentication auth) {
        return ResponseEntity.ok(orderService.cancelOrder(auth.getName(), orderId));
    }
    
    @PutMapping("/admin/status/{orderId}")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");

        return ResponseEntity.ok(
                orderService.updateOrderStatus(orderId, status)
        );
    }
} 
