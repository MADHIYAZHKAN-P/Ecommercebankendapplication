package com.example.ecommerceapplication.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ecommerceapplication.entities.User;

public interface UserRepository extends JpaRepository<User, Long> 
{
	@Query(value = "SELECT * FROM users WHERE BINARY username = :username", nativeQuery = true)
	Optional<User> findByUsernameCaseSensitive(@Param("username") String username);
//    Optional<User> findByUsername(String username);
}