package org.example.fraktalbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.payment.PaymentInitResponse;
import org.example.fraktalbackend.service.CheckoutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping("/courses/{courseId}/checkout")
    public ResponseEntity<PaymentInitResponse> checkout(
            @PathVariable UUID courseId,
            Authentication authentication
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(checkoutService.checkout(courseId, authentication.getName()));
    }
}
