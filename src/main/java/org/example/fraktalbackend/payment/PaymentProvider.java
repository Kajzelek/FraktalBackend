package org.example.fraktalbackend.payment;

import org.example.fraktalbackend.model.Order;

public interface PaymentProvider {
    String createPaymentUrl(Order order);
}