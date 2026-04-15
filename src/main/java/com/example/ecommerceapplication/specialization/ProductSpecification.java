package com.example.ecommerceapplication.specialization;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.ecommerceapplication.entities.Product;
import com.example.ecommerceapplication.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ProductSpecification 
{
	
    // 🔍 Search by name OR description
    public static Specification<Product> hasNameOrDescriptionLike(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isEmpty()) return null;

            return cb.or(
                cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%")
            );
        };
    }

    // 💰 Price range filter
    public static Specification<Product> priceBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min == null) return cb.lessThanOrEqualTo(root.get("price"), max);
            if (max == null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            return cb.between(root.get("price"), min, max);
        };
    }

    // 📦 Stock filter
    public static Specification<Product> stockGreaterThan(Integer stock) {
        return (root, query, cb) -> {
            if (stock == null) return null;
            return cb.greaterThanOrEqualTo(root.get("stock"), stock);
        };
    }
}