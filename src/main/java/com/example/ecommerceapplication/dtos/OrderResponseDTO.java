package com.example.ecommerceapplication.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResponseDTO {
    private Long orderId;
    private String status;
    private Double totalAmount;
    private List<OrderItemResponseDTO> items;
}