package com.example.ecommerceapplication.Controller;

import java.util.Map;

//import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
//import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerceapplication.Services.PaymentService;
import com.example.ecommerceapplication.dtos.PaymentVerifyDTO;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ✅ CREATE PAYMENT
    @PostMapping("/create")
    public ResponseEntity<?> create(Authentication auth) {
        return ResponseEntity.ok(
                paymentService.createPayment(auth.getName())
        );
    }

    // ✅ VERIFY PAYMENT
    @PostMapping("/verify")
    public ResponseEntity<?> verify(
            @RequestParam String paymentId,
            Authentication auth
    ) {
        return ResponseEntity.ok(
                paymentService.verifyPayment(paymentId, auth.getName())
        );
    }
}