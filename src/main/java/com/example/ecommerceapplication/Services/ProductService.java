package com.example.ecommerceapplication.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.ecommerceapplication.dtos.ProductDTO;
import com.example.ecommerceapplication.entities.Product;
import com.example.ecommerceapplication.repositories.ProductRepository;
import com.example.ecommerceapplication.specialization.ProductSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product addProduct(ProductDTO dto) {

        Product product = productRepository.findByName(dto.getName())
                .map(existing -> {
                    // 🔁 UPDATE 
                    existing.setDescription(dto.getDescription());
                    existing.setPrice(dto.getPrice());
                    existing.setStock(dto.getStock());
                    return existing;
                })
                .orElseGet(() -> {
                    // 🆕 CREATE
                    Product p = new Product();
                    p.setName(dto.getName());
                    p.setDescription(dto.getDescription());
                    p.setPrice(dto.getPrice());
                    p.setStock(dto.getStock());
                    return p;
                });

        return productRepository.save(product);
    }

    public List<Product> listProducts() {
        return productRepository.findAll();
    }
    
    public Product updateProduct(Long id, ProductDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        return productRepository.save(product);
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
}
    
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productRepository.delete(product);
    }
    
   
	public List<Product> searchProducts(
	        Double minPrice,
	        Double maxPrice,
	        String keyword,
	        Integer stock
	) {
	     Specification<Product> spec = (root, query, cb) -> cb.conjunction();

	    spec = spec.and(ProductSpecification.priceBetween(minPrice, maxPrice));
	    spec = spec.and(ProductSpecification.hasNameOrDescriptionLike(keyword));
	    spec = spec.and(ProductSpecification.stockGreaterThan(stock));

	    return productRepository.findAll(spec);
	}

    
}