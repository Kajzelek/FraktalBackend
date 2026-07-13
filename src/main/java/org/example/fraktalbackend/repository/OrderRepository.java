package org.example.fraktalbackend.repository;

import org.example.fraktalbackend.model.Order;
import org.example.fraktalbackend.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Order> findAllByOrderByCreatedAtDesc();

    Optional<Order> findByUserIdAndCourseIdAndStatus(UUID userId, UUID courseId, PaymentStatus status);
}
