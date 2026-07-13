package org.example.fraktalbackend.service;

import org.example.fraktalbackend.dto.auth.LoginRequest;
import org.example.fraktalbackend.dto.auth.RegisterRequest;
import org.example.fraktalbackend.exception.EmailAlreadyTakenException;
import org.example.fraktalbackend.exception.InvalidCredentialsException;
import org.example.fraktalbackend.model.Role;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.repository.UserRepository;
import org.example.fraktalbackend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesStudentUserWithEncodedPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("student");
        request.setEmail("student@test.pl");
        request.setPassword("password123");

        when(userRepository.findByEmail("student@test.pl")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("student");
        assertThat(savedUser.getEmail()).isEqualTo("student@test.pl");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
        assertThat(savedUser.getRole()).isEqualTo(Role.ROLE_STUDENT);
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }

    @Test
    void registerThrowsExceptionWhenEmailIsAlreadyTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("student");
        request.setEmail("student@test.pl");
        request.setPassword("password123");

        when(userRepository.findByEmail("student@test.pl")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyTakenException.class)
                .hasMessage("Email already taken");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("student@test.pl");
        request.setPassword("password123");

        User user = User.builder()
                .email("student@test.pl")
                .password("encoded-password")
                .build();

        when(userRepository.findByEmail("student@test.pl")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(jwtUtil.generateToken("student@test.pl")).thenReturn("jwt-token");

        var response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void loginThrowsExceptionForInvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("student@test.pl");
        request.setPassword("wrong-password");

        User user = User.builder()
                .email("student@test.pl")
                .password("encoded-password")
                .build();

        when(userRepository.findByEmail("student@test.pl")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtUtil, never()).generateToken(any());
    }
}
