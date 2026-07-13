package org.example.fraktalbackend.service;

import lombok.RequiredArgsConstructor;
import org.example.fraktalbackend.dto.user.UserResponse;
import org.example.fraktalbackend.exception.ResourceNotFoundException;
import org.example.fraktalbackend.mapper.UserMapper;
import org.example.fraktalbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
