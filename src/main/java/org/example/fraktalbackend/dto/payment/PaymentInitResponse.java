package org.example.fraktalbackend.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentInitResponse {
    private UUID orderId;
    private String paymentUrl;
}