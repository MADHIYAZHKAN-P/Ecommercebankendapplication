package com.example.ecommerceapplication.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerceapplication.Services.AuthService;
import com.example.ecommerceapplication.dtos.LoginDTO;
import com.example.ecommerceapplication.dtos.RegisterDTO;
import com.example.ecommerceapplication.dtos.RegisterResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {  
    private final AuthService authService;

    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO dto) {

        System.out.println("DTO username = " + dto.getUsername()); // 👈 ADD THIS

        return ResponseEntity.ok(authService.register(dto));
    }
    
//    @PostMapping("/register")
//    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterDTO request) {
//        return ResponseEntity.ok(authService.register(request));
//    } 

    @PostMapping("/login") 
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        return ResponseEntity.ok(Map.of("token", authService.login(dto)));
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterDTO dto) {
        return ResponseEntity.ok(authService.registerAdmin(dto));
    }
}