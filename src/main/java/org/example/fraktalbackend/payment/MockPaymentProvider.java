package org.example.fraktalbackend.payment;

import org.example.fraktalbackend.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MockPaymentProvider implements PaymentProvider {
    private final String frontendUrl;

    public MockPaymentProvider(@Value("${app.frontend-url}") String frontendUrl) {
        this.frontendUrl = frontendUrl.endsWith("/")
                ? frontendUrl.substring(0, frontendUrl.length() - 1)
                : frontendUrl;
    }

    @Override
    public String createPaymentUrl(Order order) {
        return frontendUrl + "/payment/mock?orderId=" + order.getId();
    }
}
