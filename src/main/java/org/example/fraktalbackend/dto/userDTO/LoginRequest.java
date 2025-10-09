package org.example.fraktalbackend.dto.userDTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
