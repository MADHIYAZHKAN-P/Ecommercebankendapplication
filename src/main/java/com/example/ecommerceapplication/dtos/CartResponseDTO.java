package com.example.ecommerceapplication.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CartResponseDTO {
    private Double totalPrice;
    private List<CartItemResponseDTO> items;
}