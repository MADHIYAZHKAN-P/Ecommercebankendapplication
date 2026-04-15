package com.example.ecommerceapplication.Services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.ecommerceapplication.dtos.LoginDTO;
import com.example.ecommerceapplication.dtos.RegisterDTO;
import com.example.ecommerceapplication.dtos.RegisterResponse;
import com.example.ecommerceapplication.entities.Role;
import com.example.ecommerceapplication.entities.User;
import com.example.ecommerceapplication.repositories.RoleRepository;
import com.example.ecommerceapplication.repositories.UserRepository;
import com.example.ecommerceapplication.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

//    public User registerUser(RegisterDTO dto) {
//    	
//        if (userRepository.findByUsername(dto.getUsername()).isPresent())
//            throw new RuntimeException("Username already exists");
//
//        User user = new User();
//        user.setUsername(dto.getUsername());
//        user.setPassword(passwordEncoder.encode(dto.getPassword()));
//
//        Role role = roleRepository.findByName("USER")
//                .orElseThrow(() -> new RuntimeException("USER role not found"));
//
//        user.getRoles().add(role);
//        return userRepository.save(user);
//    }
    
    public RegisterResponse register(RegisterDTO request) {
    	System.out.println("User count: " + userRepository.count());
        if (userRepository.findByUsernameCaseSensitive(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
 
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Set<Role> roles;

        // 🔥 First user becomes ADMIN
        if (userRepository.count() == 0) {
            roles = Set.of(adminRole);
        } else {
            roles = Set.of(userRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        // convert roles → String
        Set<String> roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new RegisterResponse(
                "User registered successfully",
                user.getUsername(),
                roleNames
        );
    }

    public User registerAdmin(RegisterDTO dto) {
        User user = userRepository.findByUsernameCaseSensitive(dto.getUsername())
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername(dto.getUsername());
                    u.setPassword(passwordEncoder.encode(dto.getPassword()));
                    return u;
                });

        Role role = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        user.getRoles().add(role);
        return userRepository.save(user);
    }

//    public String login(LoginDTO dto) {
//        User user = userRepository.findByUsername(dto.getUsername())
//                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
//
//        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
//            throw new RuntimeException("Invalid credentials");
//
//        return jwtUtil.generateToken(user.getUsername());
//    }
    
    public String login(LoginDTO dto) {

        // 🔥 Authenticate
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()
                )
        );

        // ❌ if authentication fails → exception thrown (flow stops)

        // ✅ EXTRA SAFETY CHECK
        if (!authentication.isAuthenticated()) {
            throw new RuntimeException("Invalid credentials");
        }

        // ✅ FETCH USER FROM DB (IMPORTANT)
        User user = userRepository.findByUsernameCaseSensitive(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ generate token ONLY for valid user
        return jwtUtil.generateToken(user.getUsername());
    }
    
    
}