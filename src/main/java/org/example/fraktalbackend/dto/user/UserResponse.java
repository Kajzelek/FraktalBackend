package org.example.fraktalbackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.fraktalbackend.model.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String nickname;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}
