package com.leavems.leave_management_system.controller;

import com.leavems.leave_management_system.dto.AuthResponse;
import com.leavems.leave_management_system.dto.LoginRequest;
import com.leavems.leave_management_system.model.User;
import com.leavems.leave_management_system.service.JwtService;
import com.leavems.leave_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        User user = userService.findByEmail(req.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthResponse.builder().token(token).userId(user.getId()).role(user.getRole().name()).build());
    }
}
