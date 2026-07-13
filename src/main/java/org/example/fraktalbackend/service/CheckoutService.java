package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.payment.PaymentInitResponse;
import org.example.fraktalbackend.model.Order;
import org.example.fraktalbackend.payment.PaymentProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final OrderService orderService;
    private final PaymentProvider paymentProvider;

    public PaymentInitResponse checkout(UUID courseId, String userEmail) {
        Order order = orderService.createPendingOrder(courseId, userEmail);
        String paymentUrl = paymentProvider.createPaymentUrl(order);

        return new PaymentInitResponse(order.getId(), paymentUrl);
    }
}
