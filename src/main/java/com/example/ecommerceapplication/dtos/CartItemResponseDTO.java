package com.example.ecommerceapplication.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDTO {
	private Long cartItemId;
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double itemTotal;
} 