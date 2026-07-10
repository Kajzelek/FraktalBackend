package org.example.fraktalbackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.fraktalbackend.model.Role;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String username;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}
