package com.example.ecommerceapplication.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.ecommerceapplication.entities.CartItem;
import com.example.ecommerceapplication.entities.Order;
import com.example.ecommerceapplication.entities.Payment;
import com.example.ecommerceapplication.entities.PaymentStatus;
import com.example.ecommerceapplication.entities.User;
import com.example.ecommerceapplication.repositories.CartItemRepository;
import com.example.ecommerceapplication.repositories.PaymentRepository;
import com.example.ecommerceapplication.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CartItemRepository cartRepo;
    private final UserRepository userRepo;
    private final OrderService orderService;

    // ✅ CREATE PAYMENT (like Razorpay order)
    public Map<String, Object> createPayment(String username) {

        User user = userRepo.findByUsernameCaseSensitive(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartRepo.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double total = cartItems.stream()
                .mapToDouble(ci -> ci.getProduct().getPrice() * ci.getQuantity())
                .sum();

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(total);
        payment.setStatus(PaymentStatus.CREATED);
        payment.setCreatedAt(LocalDateTime.now());

        // 🔥 Fake payment ID (like Razorpay order_id)
        payment.setPaymentId("PAY_" + UUID.randomUUID());

        paymentRepository.save(payment);

        return Map.of(
                "paymentId", payment.getPaymentId(),
                "amount", total,
                "status", payment.getStatus()
        );
    }
//    @Transactional
//    public String verifyPayment(String paymentId, String username) {
//
//        Payment payment = paymentRepository.findByPaymentId(paymentId)
//                .orElseThrow(() -> new RuntimeException("Payment not found"));
//
//        if (!payment.getUser().getUsername().equals(username)) {
//            throw new RuntimeException("Not allowed");
//        }
//
//        if (!payment.getStatus().equals("CREATED")) {
//            throw new RuntimeException("Invalid payment state");
//        }
//
//        payment.setStatus("SUCCESS");
//
//        Order order = orderService.placeOrder(username);
//
//        payment.setOrder(order);
//
//        paymentRepository.save(payment);
//
//        return "Payment successful & Order placed!";
//    }
    
    @Transactional
    public String verifyPayment(String paymentId, String username) {

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!payment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Not allowed");
        }

        if (payment.getOrder() != null) {
            throw new RuntimeException("Order already created");
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new RuntimeException("Payment already completed");
        }

        if (payment.getStatus() != PaymentStatus.CREATED) {
            throw new RuntimeException("Invalid payment state");
        }

        payment.setStatus(PaymentStatus.SUCCESS);

        Order order = orderService.placeOrderAfterPayment(username);

        payment.setOrder(order);

        paymentRepository.save(payment);

        return "Payment successful & Order placed!";
    }
    
}
