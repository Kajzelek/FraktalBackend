package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.auth.AuthResponse;
import org.example.fraktalbackend.dto.auth.LoginRequest;
import org.example.fraktalbackend.dto.auth.RegisterRequest;
import org.example.fraktalbackend.exception.EmailAlreadyTakenException;
import org.example.fraktalbackend.exception.InvalidCredentialsException;
import org.example.fraktalbackend.model.User;
import org.example.fraktalbackend.model.Role;
import org.example.fraktalbackend.repository.UserRepository;
import org.example.fraktalbackend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request){

        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new EmailAlreadyTakenException("Email already taken");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_STUDENT)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password ");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token);
    }


}
