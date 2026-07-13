package org.example.fraktalbackend.service;

import org.example.fraktalbackend.exception.OrderAlreadyExistsException;
import org.example.fraktalbackend.mapper.OrderMapper;
import org.example.fraktalbackend.model.Course;
import org.example.fraktalbackend.model.Order;
import org.example.fraktalbackend.model.PaymentStatus;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.CourseRepository;
import org.example.fraktalbackend.repository.OrderRepository;
import org.example.fraktalbackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentService enrollmentService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository,
                userRepository,
                courseRepository,
                enrollmentService,
                new OrderMapper()
        );
    }

    @Test
    void createOrderCreatesPendingOrderForPublishedCourse() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        User user = createUser(userId);
        Course course = createCourse(courseId, true);

        when(userRepository.findByEmail("student@test.pl")).thenReturn(Optional.of(user));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentService.hasAccess(userId, courseId)).thenReturn(false);
        when(orderRepository.findByUserIdAndCourseIdAndStatus(userId, courseId, PaymentStatus.PENDING))
                .thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = orderService.createOrder(courseId, "student@test.pl");

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getUser()).isEqualTo(user);
        assertThat(savedOrder.getCourse()).isEqualTo(course);
        assertThat(savedOrder.getAmount()).isEqualTo(199.0);
        assertThat(savedOrder.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(savedOrder.getCreatedAt()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(response.getAmount()).isEqualTo(199.0);
        assertThat(response.getCourseTitle()).isEqualTo("Matura podstawowa");
    }

    @Test
    void createOrderThrowsAccessDeniedForUnpublishedCourse() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        User user = createUser(userId);
        Course course = createCourse(courseId, false);

        when(userRepository.findByEmail("student@test.pl")).thenReturn(Optional.of(user));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> orderService.createOrder(courseId, "student@test.pl"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Course is not published");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrderThrowsWhenUserAlreadyHasAccess() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        User user = createUser(userId);
        Course course = createCourse(courseId, true);

        when(userRepository.findByEmail("student@test.pl")).thenReturn(Optional.of(user));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentService.hasAccess(userId, courseId)).thenReturn(true);

        assertThatThrownBy(() -> orderService.createOrder(courseId, "student@test.pl"))
                .isInstanceOf(OrderAlreadyExistsException.class)
                .hasMessage("User already has access to this course");

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void markOrderAsPaidGrantsCourseAccess() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        User user = createUser(userId);
        Course course = createCourse(courseId, true);
        Order order = Order.builder()
                .id(orderId)
                .user(user)
                .course(course)
                .amount(course.getPrice())
                .status(PaymentStatus.PENDING)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = orderService.markOrderAsPaid(orderId);

        verify(enrollmentService).grantAccess(userId, courseId);
        assertThat(order.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(order.getPaidAt()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(response.getPaidAt()).isNotNull();
    }

    private User createUser(UUID userId) {
        return User.builder()
                .id(userId)
                .email("student@test.pl")
                .build();
    }

    private Course createCourse(UUID courseId, boolean published) {
        return Course.builder()
                .id(courseId)
                .title("Matura podstawowa")
                .price(199.0)
                .published(published)
                .build();
    }
}
