package com.example.ecommerceapplication.dtos;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemResponseDTO {
    private String productName;
    private Double price;
    private Integer quantity;
    private Double total;
}