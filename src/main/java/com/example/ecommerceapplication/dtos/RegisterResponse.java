package com.example.ecommerceapplication.dtos;

import java.util.Set;

import lombok.Data;
@Data 
public class RegisterResponse {
    private String message;
    private String username;
    private Set<String> roles;

    public RegisterResponse(String message, String username, Set<String> roles) {
        this.message = message;
        this.username = username;
        this.roles = roles;
    }

    // getters  
}