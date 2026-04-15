package com.example.ecommerceapplication.Services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.ecommerceapplication.dtos.CartItemDTO;
import com.example.ecommerceapplication.dtos.CartItemResponseDTO;
import com.example.ecommerceapplication.dtos.CartResponseDTO;
import com.example.ecommerceapplication.entities.CartItem;
import com.example.ecommerceapplication.entities.Product;
import com.example.ecommerceapplication.entities.User;
import com.example.ecommerceapplication.repositories.CartItemRepository;
import com.example.ecommerceapplication.repositories.ProductRepository;
import com.example.ecommerceapplication.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // ✅ ADD TO CART
    public CartResponseDTO addToCart(String username, CartItemDTO dto) {

        User user = userRepository.findByUsernameCaseSensitive(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // ❗ CHECK STOCK
        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("Not enough stock available");
        }

        CartItem item = cartItemRepository.findByUser(user).stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(new CartItem());

        item.setUser(user);
        item.setProduct(product);

        int addedQty = dto.getQuantity();

        if (item.getQuantity() == null) {
            item.setQuantity(addedQty);
        } else {
            item.setQuantity(item.getQuantity() + addedQty);
        }

        // ✅ REDUCE STOCK
        product.setStock(product.getStock() - addedQty);
        productRepository.save(product);

        cartItemRepository.save(item);

        return viewCart(username);
    }

    // ✅ VIEW CART + TOTAL
    public CartResponseDTO viewCart(String username) {

        User user = userRepository.findByUsernameCaseSensitive(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> items = cartItemRepository.findByUser(user);

        List<CartItemResponseDTO> itemDTOs = items.stream().map(i -> 
            new CartItemResponseDTO(
            	i.getId(),	
                i.getProduct().getId(),
                i.getProduct().getName(),
                i.getProduct().getPrice(),
                i.getQuantity(),
                i.getProduct().getPrice() * i.getQuantity()
            )
        ).toList();

        double total = itemDTOs.stream()
                .mapToDouble(CartItemResponseDTO::getItemTotal)
                .sum();

        return new CartResponseDTO(total, itemDTOs); 
    }
 
    // ✅ REMOVE ITEM
    public CartResponseDTO removeFromCart(String username, Long cartItemId) {

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Not allowed");
        }

        Product product = item.getProduct();

        // ✅ RESTORE STOCK
        product.setStock(product.getStock() + item.getQuantity());
        productRepository.save(product);

        cartItemRepository.delete(item);

        return viewCart(username); 
    }
}