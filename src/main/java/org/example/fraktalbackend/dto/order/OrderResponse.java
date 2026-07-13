package org.example.fraktalbackend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.fraktalbackend.model.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private UUID userId;
    private UUID courseId;
    private String courseTitle;
    private Double amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private boolean accessGranted;
}
