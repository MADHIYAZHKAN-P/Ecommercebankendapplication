package com.example.ecommerceapplication.Services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.ecommerceapplication.dtos.OrderItemResponseDTO;
import com.example.ecommerceapplication.dtos.OrderResponseDTO;
import com.example.ecommerceapplication.entities.CartItem;
import com.example.ecommerceapplication.entities.Order;
import com.example.ecommerceapplication.entities.OrderItem;
import com.example.ecommerceapplication.entities.OrderStatus;
import com.example.ecommerceapplication.entities.Product;
import com.example.ecommerceapplication.entities.User;
import com.example.ecommerceapplication.repositories.CartItemRepository;
import com.example.ecommerceapplication.repositories.OrderRepository;
import com.example.ecommerceapplication.repositories.ProductRepository;
import com.example.ecommerceapplication.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // ✅ PLACE ORDER (MANUAL - optional)
    @Transactional
    public Order placeOrder(String username) {

        User user = userRepository.findByUsernameCaseSensitive(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty");

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setCreatedAt(LocalDateTime.now());

        double total = 0;

        for (CartItem ci : cartItems) {

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getProduct().getPrice());

            order.getOrderItems().add(oi);

            total += oi.getPrice() * oi.getQuantity();
        }

        order.setTotalAmount(total);

        orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);

        return order;
    }

    // ✅ GET ORDERS (ONLY CONFIRMED)
    public List<OrderResponseDTO> getOrders(String username) {

        User user = userRepository.findByUsernameCaseSensitive(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
                .toList();

        return orders.stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ✅ GET ORDERS WITH PAGINATION (ONLY CONFIRMED)
    public Map<String, Object> getOrders(String username, int page, int size) {

        if (size > 10) size = 10;

        User user = userRepository.findByUsernameCaseSensitive(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Order> orderPage = orderRepository.findByUser(user, pageable);

        List<OrderResponseDTO> orders = orderPage.getContent().stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
                .map(this::mapToDTO)
                .toList();

        return Map.of(
                "content", orders,
                "page", orderPage.getNumber(),
                "size", orderPage.getSize(),
                "totalElements", orderPage.getTotalElements(),
                "totalPages", orderPage.getTotalPages()
        );
    }

    // ✅ CANCEL ORDER (ONLY CONFIRMED)
    @Transactional
    public OrderResponseDTO cancelOrder(String username, Long orderId) {

        User user = userRepository.findByUsernameCaseSensitive(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not allowed");
        }

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Only confirmed orders can be cancelled");
        }

        // restore stock
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return mapToDTO(order);
    }

    // ✅ ADMIN: UPDATE ORDER STATUS
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, String status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus newStatus;

        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid status");
        }

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cancelled order cannot be updated");
        }

        if (currentStatus == OrderStatus.DELIVERED) {
            throw new RuntimeException("Order already delivered");
        }

        // ✅ Correct flow
        if (currentStatus == OrderStatus.CONFIRMED && newStatus != OrderStatus.SHIPPED) {
            throw new RuntimeException("Order must go CONFIRMED → SHIPPED");
        }

        if (currentStatus == OrderStatus.SHIPPED && newStatus != OrderStatus.DELIVERED) {
            throw new RuntimeException("Order must go SHIPPED → DELIVERED");
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        return mapToDTO(order);
    }

    // ✅ DTO MAPPER
    private OrderResponseDTO mapToDTO(Order order) {

        List<OrderItemResponseDTO> items = order.getOrderItems().stream()
                .map(i -> new OrderItemResponseDTO(
                        i.getProduct().getName(),
                        i.getPrice(),
                        i.getQuantity(),
                        i.getPrice() * i.getQuantity()
                ))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                items
        );
    }

    // ✅ AFTER PAYMENT (MAIN FLOW)
    @Transactional
    public Order placeOrderAfterPayment(String username) {

        User user = userRepository.findByUsernameCaseSensitive(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty");

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED); // 🔥 FIXED
        order.setCreatedAt(LocalDateTime.now());

        double total = 0;

        for (CartItem ci : cartItems) {

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getProduct().getPrice());

            order.getOrderItems().add(oi);

            total += oi.getPrice() * oi.getQuantity();
        }

        order.setTotalAmount(total);

        orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);
 
        return order;
    }
  /* feature of my apk
   * 1st register will be  a  admin
   * 2d register should be  a user 
   * admin can create admin
   * admin add product update delelet and view by id
   * user can view by id 
   * user add product to cart
   * after payment and verify process 
   * order will be confirmemd
   * order can be cancelled as well and while cancelling ill r=increase and vise versa 
   * 
   * */
}