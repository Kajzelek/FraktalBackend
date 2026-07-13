package org.example.fraktalbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.order.OrderResponse;
import org.example.fraktalbackend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/courses/{courseId}/orders")
    public ResponseEntity<OrderResponse> createOrder(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(courseId, authentication.getName()));
    }

    @GetMapping("/me/orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getMyOrders(authentication.getName()));
    }

    @GetMapping("/me/orders/{orderId}")
    public ResponseEntity<OrderResponse> getMyOrder(
            @PathVariable UUID orderId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(orderService.getMyOrder(orderId, authentication.getName()));
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrdersForAdmin() {
        return ResponseEntity.ok(orderService.getAllOrdersForAdmin());
    }

    @PatchMapping("/admin/orders/{orderId}/mark-paid")
    public ResponseEntity<OrderResponse> markOrderAsPaid(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.markOrderAsPaid(orderId));
    }

    @PatchMapping("/admin/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @PatchMapping("/admin/orders/{orderId}/fail")
    public ResponseEntity<OrderResponse> failOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.failOrder(orderId));
    }
}
