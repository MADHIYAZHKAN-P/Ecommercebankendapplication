package com.example.ecommerceapplication.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerceapplication.Services.ProductService;
import com.example.ecommerceapplication.dtos.ProductDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products") 
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/add")   
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.addProduct(dto));
    }

    @GetMapping
    public ResponseEntity<?> listProducts() {
        return ResponseEntity.ok(productService.listProducts());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
     
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                           @RequestBody ProductDTO dto) {
    	System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    } 
    
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer stock
    ) {
        return ResponseEntity.ok(
            productService.searchProducts(minPrice, maxPrice, keyword, stock)
        );
    }
}