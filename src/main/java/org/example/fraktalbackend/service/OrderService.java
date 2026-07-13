package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.order.OrderResponse;
import org.example.fraktalbackend.exception.EnrollmentAlreadyExistsException;
import org.example.fraktalbackend.exception.InvalidOrderStatusException;
import org.example.fraktalbackend.exception.OrderAlreadyExistsException;
import org.example.fraktalbackend.exception.ResourceNotFoundException;
import org.example.fraktalbackend.mapper.OrderMapper;
import org.example.fraktalbackend.model.Course;
import org.example.fraktalbackend.model.Order;
import org.example.fraktalbackend.model.PaymentStatus;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.CourseRepository;
import org.example.fraktalbackend.repository.OrderRepository;
import org.example.fraktalbackend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentService enrollmentService;
    private final OrderMapper orderMapper;

    public OrderResponse createOrder(UUID courseId, String userEmail) {
        return toResponse(createPendingOrder(courseId, userEmail));
    }

    public Order createPendingOrder(UUID courseId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.isPublished()) {
            throw new AccessDeniedException("Course is not published");
        }

        if (enrollmentService.hasAccess(user.getId(), courseId)) {
            throw new OrderAlreadyExistsException("User already has access to this course");
        }

        if (orderRepository.findByUserIdAndCourseIdAndStatus(user.getId(), courseId, PaymentStatus.PENDING).isPresent()) {
            throw new OrderAlreadyExistsException("User already has a pending order for this course");
        }

        Order order = Order.builder()
                .user(user)
                .course(course)
                .amount(course.getPrice())
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return orderRepository.save(order);
    }

    public List<OrderResponse> getMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse getMyOrder(UUID orderId, String userEmail) {
        Order order = findOrderById(orderId);
        ensureOrderBelongsToUser(order, userEmail);

        return toResponse(order);
    }

    public List<OrderResponse> getAllOrdersForAdmin() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrderResponse markOrderAsPaid(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return markOrderAsPaid(order);
    }

    public OrderResponse markMyOrderAsPaid(UUID orderId, String userEmail) {
        Order order = findOrderById(orderId);
        ensureOrderBelongsToUser(order, userEmail);

        return markOrderAsPaid(order);
    }

    public OrderResponse cancelMyOrder(UUID orderId, String userEmail) {
        Order order = findOrderById(orderId);
        ensureOrderBelongsToUser(order, userEmail);

        return cancelOrder(order);
    }

    public OrderResponse failMyOrder(UUID orderId, String userEmail) {
        Order order = findOrderById(orderId);
        ensureOrderBelongsToUser(order, userEmail);

        return failOrder(order);
    }

    private OrderResponse markOrderAsPaid(Order order) {
        if (order.getStatus() == PaymentStatus.CANCELLED || order.getStatus() == PaymentStatus.FAILED) {
            throw new InvalidOrderStatusException("Cannot mark cancelled or failed order as paid");
        }

        order.setStatus(PaymentStatus.PAID);
        order.setPaidAt(LocalDateTime.now());

        try {
            enrollmentService.grantAccess(order.getUser().getId(), order.getCourse().getId());
        } catch (EnrollmentAlreadyExistsException ignored) {
            // Access may already exist when an admin fixes or repeats payment confirmation.
        }

        return toResponse(orderRepository.save(order));
    }

    public OrderResponse cancelOrder(UUID orderId) {
        Order order = findOrderById(orderId);

        return cancelOrder(order);
    }

    private OrderResponse cancelOrder(Order order) {
        ensureOrderIsNotPaid(order);

        order.setStatus(PaymentStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    public OrderResponse failOrder(UUID orderId) {
        Order order = findOrderById(orderId);

        return failOrder(order);
    }

    private OrderResponse failOrder(Order order) {
        ensureOrderIsNotPaid(order);

        order.setStatus(PaymentStatus.FAILED);
        return toResponse(orderRepository.save(order));
    }

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private void ensureOrderBelongsToUser(Order order, String userEmail) {
        if (!order.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You cannot change another user's order");
        }
    }

    private void ensureOrderIsNotPaid(Order order) {
        if (order.getStatus() == PaymentStatus.PAID) {
            throw new InvalidOrderStatusException("Paid order cannot be changed");
        }
    }

    private OrderResponse toResponse(Order order) {
        boolean accessGranted = enrollmentService.hasAccess(order.getUser().getId(), order.getCourse().getId());
        return orderMapper.toResponse(order, accessGranted);
    }
}
