package org.example.fraktalbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.order.OrderResponse;
import org.example.fraktalbackend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments/mock")
@RequiredArgsConstructor
public class MockPaymentController {
    private final OrderService orderService;

    @PostMapping("/{orderId}/success")
    public ResponseEntity<OrderResponse> confirmPayment(
            @PathVariable UUID orderId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(orderService.markMyOrderAsPaid(orderId, authentication.getName()));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelPayment(
            @PathVariable UUID orderId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(orderService.cancelMyOrder(orderId, authentication.getName()));
    }

    @PostMapping("/{orderId}/fail")
    public ResponseEntity<OrderResponse> failPayment(
            @PathVariable UUID orderId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(orderService.failMyOrder(orderId, authentication.getName()));
    }
}
