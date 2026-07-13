package org.example.fraktalbackend.mapper;

import org.example.fraktalbackend.dto.order.OrderResponse;
import org.example.fraktalbackend.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order, boolean accessGranted) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getCourse().getId(),
                order.getCourse().getTitle(),
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getPaidAt(),
                accessGranted
        );
    }
}
